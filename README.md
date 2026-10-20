# JavaLearn – Java Self-Learning Platform

A self-paced Java learning web app built with **Spring Boot**. Learners work through topic-wise lessons with code examples, mark lessons complete, take practice quizzes with instant feedback, and track their progress per topic.

## Features

- **6 topics, 12 lessons, 18 practice questions**, from Java Basics and OOP to Collections and Streams, each tagged Beginner, Intermediate or Advanced
- **Practice quizzes graded on the server.** The API never sends correct answers before you submit, and every answer comes back with an explanation
- **Progress tracking**: per-topic lesson completion %, best quiz score, attempt count and overall course %
- **Idempotent lesson completion**: marking the same lesson twice doesn't double count
- Validation and consistent JSON error responses through a global exception handler
- Plain HTML/CSS/JS frontend served by Spring Boot, with no build step

## Tech Stack

Java 17 · Spring Boot 3 · Spring Web · Spring Data JPA (Hibernate) · Bean Validation · H2 / PostgreSQL · JUnit 5 · MockMvc · Docker

## Architecture

```
controller/   REST endpoints (TopicController, ProgressController)
service/      business logic: catalog, quiz grading, progress reports
repository/   Spring Data JPA repositories
model/        JPA entities: Topic, Lesson, Question, Learner, LessonCompletion, QuizAttempt
dto/          request/response records (answers are never exposed in QuestionView)
exception/    NotFound / InvalidRequest + GlobalExceptionHandler
config/       CourseSeeder: loads the curriculum on first start
```

## Run locally

```bash
mvn spring-boot:run
```

Open http://localhost:8080. Data is stored in a file-based H2 database under `./data`.

To use PostgreSQL instead:

```bash
DB_URL=jdbc:postgresql://localhost:5432/javalearn DB_USERNAME=postgres DB_PASSWORD=secret mvn spring-boot:run
```

### Docker

```bash
docker build -t javalearn .
docker run -p 8080:8080 javalearn
```

## REST API

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/topics` | List topics with lesson/question counts |
| GET | `/api/topics/{slug}` | Topic detail with lessons and code examples |
| GET | `/api/topics/{slug}/questions` | Practice questions (without answers) |
| POST | `/api/topics/{slug}/quiz` | Submit answers, get score + per-question feedback |
| POST | `/api/lessons/{id}/complete` | Mark a lesson complete for a learner |
| GET | `/api/learners/{username}/progress` | Progress report for a learner |

Example quiz submission:

```http
POST /api/topics/oop/quiz
Content-Type: application/json

{ "username": "himanshu", "answers": { "4": 1, "5": 0, "6": 1 } }
```

```json
{ "topic": "oop", "score": 3, "total": 3, "percentage": 100, "feedback": [ ... ] }
```

## Tests

```bash
mvn test
```

The integration tests cover curriculum seeding, quiz scoring (unanswered questions count as wrong, questions from other topics are rejected), progress aggregation, and API status codes. They also check that answers never leak.
