package com.himanshu.javalearn.config;

import com.himanshu.javalearn.model.Lesson;
import com.himanshu.javalearn.model.Level;
import com.himanshu.javalearn.model.Question;
import com.himanshu.javalearn.model.Topic;
import com.himanshu.javalearn.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Loads the built-in Java curriculum on first start; an existing catalog is left untouched. */
@Component
public class CourseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CourseSeeder.class);

    private final TopicRepository topicRepository;

    public CourseSeeder(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (topicRepository.count() > 0) {
            return;
        }
        List<Topic> topics = List.of(basics(), oop(), strings(), exceptions(), collections(), streams());
        topicRepository.saveAll(topics);
        log.info("Seeded {} topics", topics.size());
    }

    private Topic basics() {
        Topic topic = new Topic("java-basics", "Java Basics", Level.BEGINNER,
                "Variables, primitive types, operators and control flow.", 1);
        topic.addLesson(new Lesson("Variables and primitive types",
                "Java is statically typed: every variable has a type fixed at compile time. "
                        + "There are eight primitives: byte, short, int, long, float, double, char and boolean. "
                        + "Integer literals are int by default and decimal literals are double, so a long needs an L "
                        + "suffix and a float needs an f suffix. Since Java 10, var lets the compiler infer the type "
                        + "of a local variable from its initializer.",
                """
                int count = 10;
                long population = 8_000_000_000L;
                double price = 99.5;
                char grade = 'A';
                boolean active = true;
                var name = "Himanshu"; // inferred as String
                """, 1));
        topic.addLesson(new Lesson("Control flow",
                "if/else chooses between branches, switch selects on a value, and for, while and do-while "
                        + "repeat a block. break exits the nearest loop and continue skips to its next iteration. "
                        + "The enhanced for loop walks arrays and collections without an index.",
                """
                int[] marks = {72, 45, 88};
                for (int mark : marks) {
                    if (mark >= 75) {
                        System.out.println(mark + " distinction");
                    } else if (mark >= 50) {
                        System.out.println(mark + " pass");
                    } else {
                        System.out.println(mark + " fail");
                    }
                }
                """, 2));
        topic.addQuestion(new Question("What is the default type of the literal 3.14 in Java?",
                List.of("float", "double", "BigDecimal", "long"), 1,
                "Decimal literals are double unless they carry an f suffix."));
        topic.addQuestion(new Question("Which of these is NOT a primitive type?",
                List.of("char", "boolean", "String", "short"), 2,
                "String is a class in java.lang; the eight primitives are byte, short, int, long, float, double, char and boolean."));
        topic.addQuestion(new Question("What does 'continue' do inside a loop?",
                List.of("Exits the loop", "Skips to the next iteration", "Restarts the loop from zero", "Throws an exception"), 1,
                "continue skips the rest of the current iteration; break leaves the loop."));
        return topic;
    }

    private Topic oop() {
        Topic topic = new Topic("oop", "Object-Oriented Programming", Level.BEGINNER,
                "Classes, objects, encapsulation, inheritance and polymorphism.", 2);
        topic.addLesson(new Lesson("Classes, objects and encapsulation",
                "A class is a blueprint; an object is an instance created with new. Encapsulation keeps fields "
                        + "private and exposes behaviour through methods, so the class controls its own invariants. "
                        + "Constructors initialise a new object and can validate their arguments.",
                """
                public class BankAccount {
                    private double balance;

                    public void deposit(double amount) {
                        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
                        balance += amount;
                    }

                    public double getBalance() { return balance; }
                }
                """, 1));
        topic.addLesson(new Lesson("Inheritance and polymorphism",
                "A subclass extends a superclass and can override its methods. Polymorphism means a variable of "
                        + "the parent type can hold any subclass, and the overridden method that runs is chosen at "
                        + "runtime from the actual object. Interfaces define a contract that unrelated classes can share.",
                """
                interface Shape { double area(); }

                record Circle(double r) implements Shape {
                    public double area() { return Math.PI * r * r; }
                }

                record Square(double side) implements Shape {
                    public double area() { return side * side; }
                }

                Shape s = new Circle(2);
                System.out.println(s.area()); // Circle's area() runs
                """, 2));
        topic.addQuestion(new Question("Which principle is about hiding fields behind methods?",
                List.of("Inheritance", "Encapsulation", "Polymorphism", "Abstraction by overloading"), 1,
                "Encapsulation keeps state private and exposes controlled operations."));
        topic.addQuestion(new Question("How many classes can a Java class directly extend?",
                List.of("One", "Two", "Any number", "None, only interfaces"), 0,
                "Java has single class inheritance; a class can implement many interfaces."));
        topic.addQuestion(new Question("When is the overridden method to call chosen?",
                List.of("At compile time from the variable type", "At runtime from the object type",
                        "By the order the classes were loaded", "Randomly"), 1,
                "Dynamic dispatch picks the implementation of the actual object at runtime."));
        return topic;
    }

    private Topic strings() {
        Topic topic = new Topic("strings", "Strings", Level.BEGINNER,
                "Immutability, comparison and efficient string building.", 3);
        topic.addLesson(new Lesson("String immutability and equality",
                "Strings are immutable: methods like toUpperCase return a new String and leave the original "
                        + "unchanged. Compare contents with equals; == only checks whether two references point at "
                        + "the same object, which can look like it works for literals because of the string pool.",
                """
                String a = "java";
                String b = new String("java");
                System.out.println(a == b);      // false: different objects
                System.out.println(a.equals(b)); // true: same characters
                """, 1));
        topic.addLesson(new Lesson("StringBuilder",
                "Concatenating with + inside a loop creates a new String every pass. StringBuilder keeps one "
                        + "mutable buffer, so building a long string in a loop stays linear.",
                """
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= 5; i++) {
                    sb.append(i).append(i < 5 ? "," : "");
                }
                System.out.println(sb); // 1,2,3,4,5
                """, 2));
        topic.addQuestion(new Question("How should two Strings be compared for equal content?",
                List.of("a == b", "a.equals(b)", "a.compareTo(b) == 1", "a.hashCode() == b"), 1,
                "equals compares characters; == compares references."));
        topic.addQuestion(new Question("What does s.toUpperCase() do to s?",
                List.of("Changes s in place", "Returns a new String; s is unchanged", "Throws if s is lowercase", "Nothing at all"), 1,
                "Strings are immutable, so every transforming method returns a new String."));
        topic.addQuestion(new Question("Which class is best for building a string in a loop?",
                List.of("String", "StringBuilder", "Character", "Scanner"), 1,
                "StringBuilder appends to one mutable buffer instead of creating new Strings."));
        return topic;
    }

    private Topic exceptions() {
        Topic topic = new Topic("exceptions", "Exception Handling", Level.INTERMEDIATE,
                "Checked vs unchecked exceptions, try/catch/finally and try-with-resources.", 4);
        topic.addLesson(new Lesson("Checked and unchecked exceptions",
                "Checked exceptions (subclasses of Exception but not RuntimeException) must be caught or declared "
                        + "with throws, for example IOException. Unchecked exceptions extend RuntimeException, such as "
                        + "NullPointerException or IllegalArgumentException, and usually signal programming errors.",
                """
                public int parseAge(String input) {
                    try {
                        int age = Integer.parseInt(input);
                        if (age < 0) throw new IllegalArgumentException("age cannot be negative");
                        return age;
                    } catch (NumberFormatException e) {
                        return -1;
                    } finally {
                        System.out.println("parse attempted");
                    }
                }
                """, 1));
        topic.addLesson(new Lesson("try-with-resources",
                "Any AutoCloseable declared in the try header is closed automatically when the block exits, even "
                        + "when an exception is thrown. It replaces manual close calls in finally blocks.",
                """
                try (BufferedReader reader = Files.newBufferedReader(Path.of("notes.txt"))) {
                    System.out.println(reader.readLine());
                } catch (IOException e) {
                    System.err.println("Could not read file: " + e.getMessage());
                }
                """, 2));
        topic.addQuestion(new Question("Which of these is a checked exception?",
                List.of("NullPointerException", "IOException", "ArithmeticException", "IllegalStateException"), 1,
                "IOException extends Exception directly; the others extend RuntimeException."));
        topic.addQuestion(new Question("When does a finally block run?",
                List.of("Only if no exception is thrown", "Only if an exception is caught",
                        "Whether or not an exception is thrown", "Never when there is a return"), 2,
                "finally runs after try/catch regardless of how the block exits (barring System.exit)."));
        topic.addQuestion(new Question("What must a resource implement to be used in try-with-resources?",
                List.of("Serializable", "Runnable", "AutoCloseable", "Comparable"), 2,
                "try-with-resources calls close() on AutoCloseable resources automatically."));
        return topic;
    }

    private Topic collections() {
        Topic topic = new Topic("collections", "Collections Framework", Level.INTERMEDIATE,
                "List, Set and Map, and choosing the right implementation.", 5);
        topic.addLesson(new Lesson("List, Set and Map",
                "List keeps insertion order and allows duplicates (ArrayList, LinkedList). Set rejects duplicates "
                        + "(HashSet, LinkedHashSet, TreeSet). Map stores key-value pairs with unique keys (HashMap, "
                        + "LinkedHashMap, TreeMap). Program to the interface type and pick the implementation for "
                        + "the access pattern you need.",
                """
                List<String> names = new ArrayList<>(List.of("Asha", "Ravi", "Asha"));
                Set<String> unique = new HashSet<>(names);   // [Asha, Ravi]
                Map<String, Integer> counts = new HashMap<>();
                for (String n : names) {
                    counts.merge(n, 1, Integer::sum);
                }
                System.out.println(counts); // {Asha=2, Ravi=1}
                """, 1));
        topic.addLesson(new Lesson("equals and hashCode",
                "Hash-based collections find objects by hashCode first and then confirm with equals. If you "
                        + "override equals you must override hashCode consistently, otherwise equal objects can land "
                        + "in different buckets and a HashSet will hold duplicates. Records generate both for you.",
                """
                record Student(String rollNo, String name) {}

                Set<Student> set = new HashSet<>();
                set.add(new Student("22IT01", "Himanshu"));
                set.add(new Student("22IT01", "Himanshu"));
                System.out.println(set.size()); // 1
                """, 2));
        topic.addQuestion(new Question("Which collection does not allow duplicate elements?",
                List.of("ArrayList", "LinkedList", "HashSet", "Array"), 2,
                "Set implementations reject elements that are equal to one already present."));
        topic.addQuestion(new Question("Which Map keeps its keys sorted?",
                List.of("HashMap", "TreeMap", "LinkedHashMap", "Hashtable"), 1,
                "TreeMap orders keys by natural order or a supplied Comparator."));
        topic.addQuestion(new Question("If you override equals, what else must you override?",
                List.of("toString", "hashCode", "compareTo", "clone"), 1,
                "Equal objects must return the same hashCode or hash-based collections misbehave."));
        return topic;
    }

    private Topic streams() {
        Topic topic = new Topic("streams", "Lambdas and Streams", Level.ADVANCED,
                "Functional interfaces, lambdas and the Stream API.", 6);
        topic.addLesson(new Lesson("Lambdas and functional interfaces",
                "A functional interface has exactly one abstract method, such as Predicate, Function, Consumer or "
                        + "Supplier. A lambda is a compact implementation of that method, and a method reference "
                        + "like String::length is shorthand for a lambda that just calls one method.",
                """
                Predicate<Integer> isEven = n -> n % 2 == 0;
                Function<String, Integer> length = String::length;
                System.out.println(isEven.test(4));        // true
                System.out.println(length.apply("spring")); // 6
                """, 1));
        topic.addLesson(new Lesson("Stream pipelines",
                "A stream pipeline has a source, zero or more intermediate operations (filter, map, sorted) and "
                        + "one terminal operation (collect, count, forEach). Intermediate operations are lazy: nothing "
                        + "runs until the terminal operation is called.",
                """
                List<Integer> marks = List.of(72, 45, 88, 91, 60);
                List<Integer> toppers = marks.stream()
                        .filter(m -> m >= 75)
                        .sorted(Comparator.reverseOrder())
                        .toList();
                System.out.println(toppers); // [91, 88]
                """, 2));
        topic.addQuestion(new Question("Which of these is a terminal stream operation?",
                List.of("filter", "map", "collect", "sorted"), 2,
                "collect produces a result and triggers the pipeline; the others are intermediate."));
        topic.addQuestion(new Question("When do intermediate stream operations execute?",
                List.of("Immediately when called", "Only when a terminal operation runs",
                        "When the JVM is idle", "At compile time"), 1,
                "Streams are lazy; intermediate steps run when the terminal operation pulls elements."));
        topic.addQuestion(new Question("What makes an interface a functional interface?",
                List.of("It has no methods", "It has exactly one abstract method",
                        "It only has default methods", "It extends Runnable"), 1,
                "A single abstract method is what a lambda implements; default and static methods are allowed."));
        return topic;
    }
}
