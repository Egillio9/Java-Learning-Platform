# JavaLearn – Learn Java, Crack DSA

A full-stack learning platform that combines a structured **Java course** with a **LeetCode-style online judge**. Learners sign in, study topic-wise lessons, take graded quizzes, and solve DSA interview problems in an in-browser editor. Their Java code is compiled and run against hidden test cases on the server.

**Live demo:** https://himanshu-javalearn.onrender.com (free tier, so the first load can take ~50s to wake up)

## Features

- **Online code judge.** Write Java in a Monaco (VS Code) editor. **Run** checks your code against the examples or your own inputs, and **Submit** checks it against hidden tests. Verdicts: Accepted, Wrong Answer, Compile Error (with line numbers), Runtime Error and Time Limit Exceeded. Per-test output, stdout capture and runtime are shown.
- **24 DSA problems** across Arrays, Strings, Hashing, Two Pointers, Sliding Window, Stack, Binary Search, Linked List, Trees, Graphs (BFS/DFS), Heap, Dynamic Programming and Greedy, from Easy to Hard. LeetCode-style inputs, including `ListNode` and `TreeNode`.
- **13-topic Java course**: Basics, Arrays, Methods & Recursion, OOP, Strings, Interfaces & Abstract Classes, Exceptions, Collections, Generics, File I/O, Streams, Multithreading and Modern Java (records, switch expressions, Optional). Each topic has lessons, code examples and a server-graded quiz.
- **Authentication**: guest sign-in by name, or **Google OAuth 2.0** (Spring Security), with session-based auth and protected API routes.
- **Dashboard**: solved count by difficulty, acceptance rate, daily streak, course progress, recent submissions and a "next problem" suggestion.
- **Leaderboard**, **submission history** with "load into editor", and an **editorial** (reference solution) that unlocks after your first accepted submission.
- **Professional UI**: React 18 + React Router SPA with landing, sign-in, dashboard, problem list (search and filters), split-pane workspace, course pages and leaderboard. Drafts autosave to localStorage, and Ctrl+Enter runs the code.

## Tech Stack

| Layer | Technology |
| --- | --- |
| Backend | Java 17, Spring Boot 3, Spring Web, Spring Data JPA (Hibernate), Spring Security, OAuth2 Client, Bean Validation |
| Judge | `javax.tools` in-process compiler + sandboxed child JVM per submission |
| Database | H2 (file/in-memory) or PostgreSQL |
| Frontend | React 18, React Router 6, Monaco Editor, Vite |
| Testing | JUnit 5, AssertJ, MockMvc, spring-security-test |
| Deploy | Multi-stage Docker (Node → Maven → JDK), Render Blueprint |

## Architecture

```
frontend/                      React SPA (built into src/main/resources/static)
src/main/java/com/himanshu/javalearn/
├── auth/        SecurityConfig, guest + Google sign-in, session principal (AuthUser)
├── judge/       CodeRunner (compile + sandboxed run), SourceGuard (blocked APIs), Execution
├── controller/  REST: topics, progress, problems, stats/leaderboard, SPA routing
├── service/     JudgeService (verdicts), ProblemService, StatsService, Quiz/Progress/Catalog
├── model/       JPA entities: Learner, Topic, Lesson, Question, Problem, TestCase, Submission …
├── repository/  Spring Data repositories (incl. aggregate JPQL for stats)
└── config/      CourseSeeder + ExtendedCurriculum, ProblemSeeder
src/main/resources/
├── problems/NN-slug/   problem.json · description.md · starter.java · driver.java · solution.java
└── judge/              JudgeRuntime.java, ListNode.java, TreeNode.java (compiled with each submission)
scripts/generate_problems.py   authoring tool for the problem bank
```

### How the judge works

1. The submission (`Solution.java`) is checked by **SourceGuard**, which rejects file, network, process, thread and reflection APIs.
2. It is compiled together with the problem's hidden **driver** (`Main`) and `JudgeRuntime` using `javax.tools.JavaCompiler`. Compiler errors are mapped back to the learner's line numbers.
3. All test inputs run in **one short-lived child JVM** with an empty environment, a private temp directory, a capped heap, a capped output size and a wall-clock timeout. A semaphore limits concurrent runs.
4. The driver parses each input (LeetCode notation, one argument per line), calls the solution, captures `System.out`, and reports results over a small line protocol. JudgeService compares the results with the expected outputs and decides the verdict.
5. Hidden test cases may omit expected outputs. The reference solution produces them once, and they are cached in the database.

> This sandbox is designed for a demo deployment. For untrusted public traffic at scale, run each submission in its own container (e.g. gVisor, nsjail or Judge0).

## Run locally

```bash
# 1. build the UI into Spring Boot's static folder
cd frontend && npm install && npm run build && cd ..

# 2. start the app (needs a JDK 17+, not a JRE)
mvn spring-boot:run
```

Open http://localhost:8080. For UI development with hot reload, run `npm run dev` in `frontend/` (port 5173, which proxies the API to 8080).

### Google sign-in (optional)

1. Create an OAuth client ("Web application") at https://console.cloud.google.com/apis/credentials.
2. Add the redirect URI `https://<your-host>/login/oauth2/code/google` (locally: `http://localhost:8080/login/oauth2/code/google`).
3. Start the app with `SPRING_PROFILES_ACTIVE=google`, `GOOGLE_CLIENT_ID=...` and `GOOGLE_CLIENT_SECRET=...`.

The "Continue with Google" button appears automatically once these are set.

### Docker

```bash
docker build -t javalearn .
docker run -p 8080:8080 javalearn
```

## REST API (selection)

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| POST | `/api/auth/guest` | – | Sign in by name |
| GET | `/api/auth/me` | ✓ | Current learner |
| GET | `/api/problems` | – | Problems with status and acceptance rate |
| GET | `/api/problems/{slug}` | – | Statement, starter code, examples |
| POST | `/api/problems/{slug}/run` | ✓ | Run on examples or custom inputs |
| POST | `/api/problems/{slug}/submit` | ✓ | Judge against all tests, record a submission |
| GET | `/api/problems/{slug}/submissions` | ✓ | Your submission history |
| GET | `/api/problems/{slug}/solution` | ✓ | Reference solution (after solving) |
| GET | `/api/topics`, `/api/topics/{slug}` | – | Course topics and lessons |
| POST | `/api/topics/{slug}/quiz` | ✓ | Grade a quiz |
| POST | `/api/lessons/{id}/complete` | ✓ | Mark a lesson complete |
| GET | `/api/me/dashboard` | ✓ | Stats, streak, rank, recent activity |
| GET | `/api/leaderboard` | – | Ranked learners |

## Tests

```bash
mvn test
```

There are 21 tests. They include **every problem's reference solution being judged Accepted** against its examples and hidden tests in a real child JVM, each verdict type, blocked APIs, custom inputs, auth rules, session flow and SPA routing.

## Adding a problem

Add an entry to `scripts/generate_problems.py` (statement, signature, driver, reference solution, examples and hidden inputs). Then run:

```bash
python scripts/generate_problems.py src/main/resources/problems
```

Problems sync into the database on startup.
