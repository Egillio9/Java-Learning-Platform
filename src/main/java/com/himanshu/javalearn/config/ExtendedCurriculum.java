package com.himanshu.javalearn.config;

import com.himanshu.javalearn.model.Lesson;
import com.himanshu.javalearn.model.Level;
import com.himanshu.javalearn.model.Question;
import com.himanshu.javalearn.model.Topic;

import java.util.List;

/** Topics added in v2 of the course. Positions are assigned by CourseSeeder from the syllabus order. */
final class ExtendedCurriculum {

    private ExtendedCurriculum() {
    }

    static Topic arrays() {
        Topic topic = new Topic("arrays", "Arrays", Level.BEGINNER,
                "Fixed-size arrays, 2D grids and the java.util.Arrays helpers.", 0);
        topic.addLesson(new Lesson("Declaring and looping over arrays",
                "An array holds a fixed number of values of one type, stored next to each other in memory. Its "
                        + "length is set when it is created and read with the length field (no parentheses). Indices "
                        + "run from 0 to length - 1; anything outside that range throws "
                        + "ArrayIndexOutOfBoundsException. New int arrays start filled with 0, booleans with false and "
                        + "object arrays with null.",
                """
                int[] scores = new int[3];          // [0, 0, 0]
                int[] primes = {2, 3, 5, 7, 11};    // array literal
                scores[0] = 95;

                int sum = 0;
                for (int i = 0; i < primes.length; i++) {
                    sum += primes[i];
                }
                System.out.println(sum);            // 28
                """, 1));
        topic.addLesson(new Lesson("2D arrays and java.util.Arrays",
                "A 2D array is an array of arrays, which makes it a natural fit for grids and matrices. "
                        + "grid[row][col] reads one cell, and grid[row].length gives the width of that row. The Arrays "
                        + "class provides sort, fill, copyOf, equals and toString. Printing an array directly only "
                        + "shows its type and hash code, so use Arrays.toString or Arrays.deepToString instead.",
                """
                int[][] grid = {
                    {1, 2, 3},
                    {4, 5, 6}
                };
                System.out.println(grid[1][2]);                 // 6
                System.out.println(Arrays.deepToString(grid));  // [[1, 2, 3], [4, 5, 6]]

                int[] data = {5, 1, 4};
                Arrays.sort(data);
                System.out.println(Arrays.toString(data));      // [1, 4, 5]
                """, 2));
        topic.addQuestion(new Question("What is the default value of each element in new int[5]?",
                List.of("null", "0", "undefined", "-1"), 1,
                "Numeric arrays are zero-filled; object arrays start as null."));
        topic.addQuestion(new Question("How do you get the number of elements in an array named nums?",
                List.of("nums.size()", "nums.length()", "nums.length", "nums.count"), 2,
                "Arrays expose length as a field. size() belongs to collections, length() to String."));
        topic.addQuestion(new Question("What does System.out.println(Arrays.toString(new int[]{3, 1})) print?",
                List.of("[3, 1]", "[1, 3]", "3 1", "[I@1b6d3586"), 0,
                "Arrays.toString formats the elements in their current order; it does not sort."));
        return topic;
    }

    static Topic methods() {
        Topic topic = new Topic("methods-and-recursion", "Methods & Recursion", Level.BEGINNER,
                "Parameters, return values, overloading, pass-by-value and recursive thinking.", 0);
        topic.addLesson(new Lesson("Writing methods",
                "A method has a return type, a name and a parameter list, and void means it returns nothing. "
                        + "Java is always pass-by-value. For primitives the value itself is copied. For objects, the "
                        + "copied value is the reference, so a method can change an object's contents but cannot make "
                        + "the caller's variable point at a different object. Overloading means several methods share "
                        + "a name but take different parameter lists.",
                """
                static int square(int x) {
                    return x * x;
                }

                static double square(double x) {    // overload: different parameter type
                    return x * x;
                }

                static void reset(int[] values) {
                    values[0] = 0;                   // visible to the caller
                    values = new int[] {99};         // only changes the local copy
                }
                """, 1));
        topic.addLesson(new Lesson("Recursion",
                "A recursive method solves a problem by calling itself on a smaller version of the same problem. "
                        + "Every recursion needs a base case that stops it. Each call takes a stack frame, so very deep "
                        + "recursion throws StackOverflowError. Recursion fits naturally with trees, divide and conquer, "
                        + "and backtracking. Caching repeated sub-results (memoization) is the first step towards "
                        + "dynamic programming.",
                """
                static long factorial(int n) {
                    if (n <= 1) return 1;            // base case
                    return n * factorial(n - 1);     // smaller sub-problem
                }

                static Map<Integer, Long> memo = new HashMap<>();

                static long fib(int n) {
                    if (n < 2) return n;
                    return memo.computeIfAbsent(n, k -> fib(k - 1) + fib(k - 2));
                }
                """, 2));
        topic.addQuestion(new Question("A method sets its int parameter to 10. What happens to the caller's variable?",
                List.of("It becomes 10", "It is unchanged", "It becomes 0", "Compile error"), 1,
                "Primitives are copied into the parameter, so the caller's variable is untouched."));
        topic.addQuestion(new Question("Which pair of methods is a valid overload?",
                List.of("int f(int a) and long f(int a)", "void f(int a) and void f(String a)",
                        "void f(int a) and void f(int b)", "static void f() and void f()"), 1,
                "Overloads must differ in parameter types or count; return type and parameter names do not count."));
        topic.addQuestion(new Question("What happens if a recursive method has no reachable base case?",
                List.of("It returns 0", "The compiler rejects it", "StackOverflowError at runtime", "It loops forever silently"), 2,
                "Each call adds a stack frame until the thread's stack is exhausted."));
        return topic;
    }

    static Topic interfaces() {
        Topic topic = new Topic("interfaces-and-abstract-classes", "Interfaces & Abstract Classes", Level.INTERMEDIATE,
                "Designing contracts, default methods, and when to choose an abstract class.", 0);
        topic.addLesson(new Lesson("Interfaces as contracts",
                "An interface lists what a type can do without saying how. A class can implement many interfaces, "
                        + "which is how Java gets the flexibility of multiple inheritance without its ambiguity. "
                        + "Interface methods are public and abstract by default. Default methods add behaviour to every "
                        + "implementation, and static methods are helpers that belong to the interface itself. Code "
                        + "that depends on interfaces such as List or Comparator is easier to test and to change.",
                """
                interface PaymentMethod {
                    boolean pay(double amount);

                    default String receipt(double amount) {
                        return "Paid " + amount + " via " + getClass().getSimpleName();
                    }
                }

                class Upi implements PaymentMethod {
                    public boolean pay(double amount) { return amount <= 100_000; }
                }
                """, 1));
        topic.addLesson(new Lesson("Abstract classes",
                "An abstract class cannot be instantiated. It may hold fields, constructors and a mix of concrete "
                        + "and abstract methods. Use one when related classes share state or a fixed algorithm with "
                        + "a few steps left open (the template method pattern). Use an interface when unrelated classes "
                        + "only need to share a capability. A class can extend only one abstract class.",
                """
                abstract class Report {
                    // the template: fixed order, customisable steps
                    final String render() {
                        return header() + "\\n" + body();
                    }

                    String header() { return "== Report =="; }

                    abstract String body();
                }

                class SalesReport extends Report {
                    String body() { return "Revenue up 12%"; }
                }
                """, 2));
        topic.addQuestion(new Question("How many interfaces can a single class implement?",
                List.of("Only one", "At most two", "Any number", "None if it extends a class"), 2,
                "A class extends at most one class but may implement any number of interfaces."));
        topic.addQuestion(new Question("Which can an abstract class have that an interface cannot?",
                List.of("Static methods", "Instance fields with state", "Default methods", "Public methods"), 1,
                "Interfaces may only declare constants; abstract classes can hold mutable instance state."));
        topic.addQuestion(new Question("What does `new Report()` do if Report is abstract?",
                List.of("Creates an empty Report", "Compile error", "Throws at runtime", "Creates a null reference"), 1,
                "Abstract classes cannot be instantiated directly, only through a concrete subclass."));
        return topic;
    }

    static Topic generics() {
        Topic topic = new Topic("generics", "Generics", Level.INTERMEDIATE,
                "Type parameters, bounded types and wildcards for type-safe reusable code.", 0);
        topic.addLesson(new Lesson("Generic classes and methods",
                "Generics let one class or method work with many types and still be checked by the compiler. A "
                        + "List<String> rejects an Integer at compile time and needs no casts when you read from it. "
                        + "Type parameters such as <T> are declared on a class or before a method's return type. "
                        + "Bounds like <T extends Comparable<T>> limit T to types that support the operations you need.",
                """
                class Box<T> {
                    private final T value;
                    Box(T value) { this.value = value; }
                    T get() { return value; }
                }

                static <T extends Comparable<T>> T max(List<T> items) {
                    T best = items.get(0);
                    for (T item : items) {
                        if (item.compareTo(best) > 0) best = item;
                    }
                    return best;
                }

                Box<String> box = new Box<>("hello");   // diamond infers <String>
                """, 1));
        topic.addLesson(new Lesson("Wildcards and type erasure",
                "List<? extends Number> accepts a list of Integer or Double and lets you read Numbers out of it, but "
                        + "not add to it. List<? super Integer> lets you add Integers. A common mnemonic is PECS: "
                        + "Producer Extends, Consumer Super. Generic types are erased at runtime, so new T(), "
                        + "T.class and instanceof List<String> are not allowed. Primitives cannot be type arguments, "
                        + "so use wrapper types such as Integer instead.",
                """
                static double sum(List<? extends Number> numbers) {
                    double total = 0;
                    for (Number n : numbers) total += n.doubleValue();
                    return total;
                }

                static void addDefaults(List<? super Integer> target) {
                    target.add(0);
                    target.add(1);
                }

                sum(List.of(1, 2, 3));        // works with List<Integer>
                sum(List.of(1.5, 2.5));       // and List<Double>
                """, 2));
        topic.addQuestion(new Question("Why does `List<int>` fail to compile?",
                List.of("Lists cannot hold numbers", "Type arguments must be reference types",
                        "int is reserved", "You must write List<Int>"), 1,
                "Generics work only with objects, so use List<Integer> and let autoboxing convert values."));
        topic.addQuestion(new Question("Which signature lets a method read Numbers from a List<Integer> or a List<Double>?",
                List.of("List<Number>", "List<? super Number>", "List<? extends Number>", "List<Object>"), 2,
                "List<Integer> is not a List<Number>; the extends-wildcard accepts any subtype of Number."));
        topic.addQuestion(new Question("What does type erasure mean?",
                List.of("Generic type information is removed after compilation", "Unused types are deleted",
                        "Generics are slower at runtime", "Casts are forbidden"), 0,
                "The compiler checks types and inserts casts, then erases type parameters from the bytecode."));
        return topic;
    }

    static Topic fileIo() {
        Topic topic = new Topic("file-io", "File Handling & I/O", Level.INTERMEDIATE,
                "Reading and writing files with java.nio.file, buffering and character encodings.", 0);
        topic.addLesson(new Lesson("Reading and writing files",
                "The modern API lives in java.nio.file. Path represents a location and Files does the work. "
                        + "Files.readString and Files.writeString suit small files. Files.lines streams a large file "
                        + "lazily, and it must be closed, so wrap it in try-with-resources. Always state the charset, "
                        + "usually UTF-8, so the program behaves the same on every machine.",
                """
                Path notes = Path.of("notes.txt");
                Files.writeString(notes, "first line\\nsecond line\\n", StandardCharsets.UTF_8);

                String all = Files.readString(notes, StandardCharsets.UTF_8);

                try (Stream<String> lines = Files.lines(notes, StandardCharsets.UTF_8)) {
                    long count = lines.filter(line -> !line.isBlank()).count();
                    System.out.println(count);   // 2
                }
                """, 1));
        topic.addLesson(new Lesson("Streams, readers and buffering",
                "Byte streams (InputStream / OutputStream) move raw bytes, and readers and writers move characters. "
                        + "Wrapping them in BufferedReader or BufferedWriter cuts down the number of slow system calls, "
                        + "which matters for large inputs. Competitive programmers use a BufferedReader on System.in "
                        + "because Scanner is much slower on big test data. Every stream holds an OS handle, so close it "
                        + "in try-with-resources.",
                """
                try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
                    int n = Integer.parseInt(in.readLine().trim());
                    StringTokenizer tokens = new StringTokenizer(in.readLine());
                    long sum = 0;
                    for (int i = 0; i < n; i++) {
                        sum += Long.parseLong(tokens.nextToken());
                    }
                    System.out.println(sum);
                }
                """, 2));
        topic.addQuestion(new Question("Why should Files.lines(...) be used inside try-with-resources?",
                List.of("It is faster", "The returned stream holds an open file handle", "It throws otherwise", "It sorts the lines"), 1,
                "The lazy stream keeps the file open until it is closed."));
        topic.addQuestion(new Question("What is the main benefit of BufferedReader over reading one char at a time?",
                List.of("It encrypts data", "Fewer expensive system calls", "It skips blank lines", "It parses numbers"), 1,
                "It reads large chunks into memory and serves characters from that buffer."));
        topic.addQuestion(new Question("Which class represents a file system location in java.nio.file?",
                List.of("File", "Path", "Location", "Directory"), 1,
                "Path (created with Path.of) is the modern replacement for java.io.File."));
        return topic;
    }

    static Topic multithreading() {
        Topic topic = new Topic("multithreading", "Multithreading & Concurrency", Level.ADVANCED,
                "Threads, executors, race conditions and thread-safe building blocks.", 0);
        topic.addLesson(new Lesson("Threads and executors",
                "A thread is an independent path of execution inside the same process, sharing the heap with other "
                        + "threads. Creating raw Thread objects by hand does not scale. An ExecutorService keeps a pool of "
                        + "worker threads and runs submitted tasks on them. submit returns a Future, and get() waits for "
                        + "its result. Always shut an executor down, or its threads keep the JVM alive.",
                """
                ExecutorService pool = Executors.newFixedThreadPool(4);
                try {
                    Future<Integer> answer = pool.submit(() -> {
                        Thread.sleep(100);           // simulate work
                        return 42;
                    });
                    System.out.println(answer.get()); // blocks until ready: 42
                } finally {
                    pool.shutdown();
                }
                """, 1));
        topic.addLesson(new Lesson("Race conditions and thread safety",
                "count++ looks like one step but is really three: read, add, write. Two threads can interleave those "
                        + "steps and lose updates, which is a race condition. Fixes include synchronized blocks (one thread "
                        + "at a time), atomic classes like AtomicInteger (lock-free), and concurrent collections such as "
                        + "ConcurrentHashMap. Immutable objects are thread-safe automatically, which is one more reason "
                        + "to prefer them.",
                """
                AtomicInteger safe = new AtomicInteger();
                int[] unsafe = {0};

                Runnable work = () -> {
                    for (int i = 0; i < 100_000; i++) {
                        safe.incrementAndGet();
                        unsafe[0]++;                 // data race
                    }
                };
                Thread a = new Thread(work), b = new Thread(work);
                a.start(); b.start();
                a.join(); b.join();
                System.out.println(safe.get());      // always 200000
                System.out.println(unsafe[0]);       // often less
                """, 2));
        topic.addQuestion(new Question("Why can two threads running count++ lose updates?",
                List.of("++ is not allowed in threads", "It is a read-modify-write that can interleave",
                        "Threads have separate heaps", "The JVM rounds numbers"), 1,
                "Both threads can read the same old value before either writes back."));
        topic.addQuestion(new Question("What does Future.get() do?",
                List.of("Cancels the task", "Returns immediately with null", "Waits for and returns the task's result", "Starts the task"), 2,
                "get() blocks the calling thread until the result (or exception) is available."));
        topic.addQuestion(new Question("Which is a thread-safe map designed for concurrent access?",
                List.of("HashMap", "TreeMap", "ConcurrentHashMap", "LinkedHashMap"), 2,
                "ConcurrentHashMap allows safe concurrent reads and updates without locking the whole map."));
        return topic;
    }

    static Topic modernJava() {
        Topic topic = new Topic("modern-java", "Modern Java (11–21)", Level.ADVANCED,
                "Records, switch expressions, text blocks, var, Optional and pattern matching.", 0);
        topic.addLesson(new Lesson("Records, var and text blocks",
                "A record is a compact, immutable data carrier. Java generates its constructor, accessors, equals, "
                        + "hashCode and toString. var infers a local variable's type from its initializer, and the "
                        + "variable is still statically typed. Text blocks (triple quotes) hold multi-line strings such as "
                        + "JSON or SQL without escape noise.",
                """
                record Point(int x, int y) {
                    Point {                                   // compact constructor validates
                        if (x < 0 || y < 0) throw new IllegalArgumentException("negative");
                    }
                    int distanceFromOrigin() { return x + y; }
                }

                var p = new Point(3, 4);                      // type is Point
                System.out.println(p);                        // Point[x=3, y=4]

                String json = \"""
                    { "x": 3, "y": 4 }
                    \""";
                """, 1));
        topic.addLesson(new Lesson("Switch expressions, pattern matching and Optional",
                "Switch can now be an expression. Arrow labels never fall through, and the compiler checks that "
                        + "enums and sealed types are fully covered. Pattern matching for instanceof binds a typed "
                        + "variable in one step. Optional<T> makes \"maybe no value\" explicit in a return type, so use "
                        + "map, filter and orElse instead of returning null.",
                """
                enum Day { MON, TUE, SAT, SUN }

                static String kind(Day day) {
                    return switch (day) {
                        case SAT, SUN -> "weekend";
                        case MON, TUE -> "weekday";
                    };
                }

                static String describe(Object o) {
                    if (o instanceof String s && !s.isEmpty()) return "text of length " + s.length();
                    return "something else";
                }

                Optional<String> email = Optional.of("a@b.com");
                String domain = email.map(e -> e.substring(e.indexOf('@') + 1)).orElse("unknown");
                """, 2));
        topic.addQuestion(new Question("Which methods does a record generate automatically?",
                List.of("Only getters", "Constructor, accessors, equals, hashCode and toString",
                        "Setters for every field", "Nothing; it is just a marker"), 1,
                "Records are immutable data carriers with all of those generated for you."));
        topic.addQuestion(new Question("With `var list = new ArrayList<String>();`, what is the type of list?",
                List.of("Object", "var", "ArrayList<String>", "It is dynamically typed"), 2,
                "var is inferred at compile time from the initializer; Java stays statically typed."));
        topic.addQuestion(new Question("What is a key advantage of arrow-style switch labels (case X ->)?",
                List.of("They run faster", "No accidental fall-through", "They allow duplicate labels", "They work only with strings"), 1,
                "Arrow cases execute just their own branch, so break statements are unnecessary."));
        return topic;
    }
}
