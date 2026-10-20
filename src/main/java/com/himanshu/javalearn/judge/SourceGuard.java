package com.himanshu.javalearn.judge;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Rejects submissions that reach for the file system, network, processes, threads or reflection.
 * This is a first line of defence; the runner also isolates each run in its own JVM with a clean
 * environment, a scratch directory, a memory cap and a wall-clock limit.
 */
final class SourceGuard {

    private record Rule(Pattern pattern, String reason) {
    }

    private static final List<Rule> RULES = List.of(
            rule("\\bRuntime\\s*\\.", "Runtime"),
            rule("\\bProcess(Builder|Handle)?\\b", "process APIs"),
            rule("\\bSystem\\s*\\.\\s*(exit|setOut|setErr|setIn|getenv|getProperty|setProperty|load|loadLibrary)\\b",
                    "process exit, stream redirection and environment access"),
            rule("\\bjava\\s*\\.\\s*(net|nio\\s*\\.\\s*file|lang\\s*\\.\\s*reflect|lang\\s*\\.\\s*invoke)\\b",
                    "network, file system or reflection packages"),
            rule("\\b(File|Files|Paths|FileDescriptor|FileInputStream|FileOutputStream|FileReader|FileWriter|RandomAccessFile)\\b",
                    "file system access"),
            rule("\\b(Socket|ServerSocket|URL|URI|HttpClient|DatagramSocket)\\b", "network access"),
            rule("\\b(Thread|ThreadGroup|Executors?|ExecutorService|ForkJoinPool|CompletableFuture|Timer)\\b",
                    "threads"),
            rule("\\b(ClassLoader|Class\\s*\\.\\s*forName|getDeclared\\w*|setAccessible|MethodHandles?|Unsafe)\\b",
                    "reflection"),
            rule("\\bnative\\b", "native methods"),
            rule("\\b(sun|jdk)\\s*\\.", "internal JDK packages"),
            rule("\\bJudgeRuntime\\b", "the judge runtime"),
            rule("\\bScriptEngine\\w*\\b", "script engines"));

    private SourceGuard() {
    }

    private static Rule rule(String regex, String reason) {
        return new Rule(Pattern.compile(regex), reason);
    }

    /** Returns a learner-facing message when the code uses a blocked API. */
    static Optional<String> check(String source) {
        String code = stripCommentsAndStrings(source);
        for (Rule rule : RULES) {
            var matcher = rule.pattern().matcher(code);
            if (matcher.find()) {
                return Optional.of("'" + matcher.group().trim() + "' is not allowed here (" + rule.reason()
                        + " are disabled in the judge).");
            }
        }
        return Optional.empty();
    }

    /** Comments and string literals cannot execute anything, so they should not trigger the rules. */
    static String stripCommentsAndStrings(String source) {
        return source
                .replaceAll("(?s)/\\*.*?\\*/", " ")
                .replaceAll("//[^\\n]*", " ")
                .replaceAll("\"(?:[^\"\\\\\\n]|\\\\.)*\"", "\"\"")
                .replaceAll("'(?:[^'\\\\\\n]|\\\\.)'", "' '");
    }
}
