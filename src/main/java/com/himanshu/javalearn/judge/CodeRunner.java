package com.himanshu.javalearn.judge;

import com.himanshu.javalearn.judge.Execution.CaseOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Compiles a learner's Solution together with a problem driver and runs every test input in one
 * short-lived child JVM. Isolation: blocked-API check, empty environment, private temp directory,
 * capped heap, capped output, wall-clock timeout, and a limit on concurrent runs.
 */
@Component
public class CodeRunner {

    private static final Logger log = LoggerFactory.getLogger(CodeRunner.class);
    private static final List<String> SUPPORT_FILES = List.of("JudgeRuntime.java", "ListNode.java", "TreeNode.java");

    private final JudgeProperties properties;
    private final Semaphore slots;
    private final Map<String, String> supportSources;
    private final JavaCompiler compiler;

    public CodeRunner(JudgeProperties properties) {
        this.properties = properties;
        this.slots = new Semaphore(properties.maxConcurrent(), true);
        this.supportSources = SUPPORT_FILES.stream()
                .collect(Collectors.toMap(name -> name, name -> readResource("judge/" + name)));
        this.compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            log.warn("No Java compiler available: run the app on a JDK, not a JRE, or submissions will fail");
        }
    }

    public Execution execute(String solutionSource, String driverSource, List<String> inputs) {
        if (solutionSource.length() > properties.maxSourceChars()) {
            return Execution.compileError("Code is longer than " + properties.maxSourceChars() + " characters.");
        }
        var blocked = SourceGuard.check(solutionSource);
        if (blocked.isPresent()) {
            return Execution.compileError(blocked.get());
        }
        if (compiler == null) {
            throw new IllegalStateException("The judge needs a JDK at runtime");
        }

        acquireSlot();
        Path workDir = null;
        try {
            workDir = Files.createTempDirectory("judge-");
            Files.writeString(workDir.resolve("Solution.java"), solutionSource);
            Files.writeString(workDir.resolve("Main.java"), driverSource);
            for (var support : supportSources.entrySet()) {
                Files.writeString(workDir.resolve(support.getKey()), support.getValue());
            }

            String compileErrors = compile(workDir);
            if (compileErrors != null) {
                return Execution.compileError(compileErrors);
            }
            return run(workDir, inputs);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            slots.release();
            deleteQuietly(workDir);
        }
    }

    private void acquireSlot() {
        try {
            if (!slots.tryAcquire(30, TimeUnit.SECONDS)) {
                throw new JudgeBusyException();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JudgeBusyException();
        }
    }

    /** Returns null on success, otherwise the learner-facing compiler output. */
    private String compile(Path workDir) throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager files = compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, StandardCharsets.UTF_8)) {
            List<Path> sources;
            try (Stream<Path> listing = Files.list(workDir)) {
                sources = listing.filter(p -> p.toString().endsWith(".java")).toList();
            }
            var units = files.getJavaFileObjectsFromPaths(sources);
            List<String> options = List.of("-d", workDir.toString(), "-proc:none", "-nowarn", "-encoding", "UTF-8");
            boolean ok = compiler.getTask(null, files, diagnostics, options, null, units).call();
            if (ok) {
                return null;
            }
        }
        return diagnostics.getDiagnostics().stream()
                .filter(d -> d.getKind() == Diagnostic.Kind.ERROR)
                .map(CodeRunner::describe)
                .distinct()
                .limit(10)
                .collect(Collectors.joining("\n"));
    }

    private static String describe(Diagnostic<? extends JavaFileObject> diagnostic) {
        String file = diagnostic.getSource() == null ? "" : Path.of(diagnostic.getSource().getName()).getFileName().toString();
        String message = diagnostic.getMessage(Locale.ENGLISH);
        if (file.equals("Solution.java")) {
            return "Line " + diagnostic.getLineNumber() + ": " + message;
        }
        // Errors inside the hidden driver almost always mean the method signature was changed.
        return "Your Solution no longer matches the expected method signature: " + message;
    }

    private Execution run(Path workDir, List<String> inputs) throws IOException {
        String javaBin = Path.of(System.getProperty("java.home"), "bin",
                System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win") ? "java.exe" : "java").toString();
        ProcessBuilder builder = new ProcessBuilder(javaBin,
                "-Xmx" + properties.memoryMb() + "m", "-Xss64m",
                "-XX:+UseSerialGC", "-XX:TieredStopAtLevel=1", "-XX:-UsePerfData", "-Xshare:auto",
                "-Dfile.encoding=UTF-8", "-cp", workDir.toString(), "Main")
                .directory(workDir.toFile());
        String systemRoot = System.getenv("SystemRoot");
        builder.environment().clear();
        if (systemRoot != null) {
            builder.environment().put("SystemRoot", systemRoot); // the JVM cannot start on Windows without it
        }

        Process process = builder.start();
        CompletableFuture<byte[]> stdout = readBounded(process.getInputStream(), properties.maxOutputBytes(), process);
        CompletableFuture<byte[]> stderr = readBounded(process.getErrorStream(), 20_000, process);
        try (OutputStream stdin = process.getOutputStream()) {
            stdin.write(batchInput(inputs).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
            // The child may exit before reading everything (e.g. crash on start-up); its stderr explains why.
        }

        boolean finished;
        try {
            finished = process.waitFor(properties.timeLimit().toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            finished = false;
        }
        if (!finished) {
            process.destroyForcibly();
        }

        List<CaseOutcome> cases = parse(new String(stdout.join(), StandardCharsets.UTF_8));
        String errors = new String(stderr.join(), StandardCharsets.UTF_8).trim();
        if (!finished) {
            return Execution.timeLimit(cases);
        }
        if (stdout.join().length >= properties.maxOutputBytes()) {
            return Execution.crashed("Output limit exceeded", cases);
        }
        if (cases.size() < inputs.size()) {
            String reason = errors.contains("OutOfMemoryError") ? "Memory limit exceeded"
                    : errors.isEmpty() ? "Process exited with code " + process.exitValue() : firstLines(errors, 6);
            return Execution.crashed(reason, cases);
        }
        return new Execution(Execution.Status.COMPLETED, null, cases);
    }

    /** Wire format: case count, then for each case its line count followed by the lines. */
    static String batchInput(List<String> inputs) {
        StringBuilder batch = new StringBuilder().append(inputs.size()).append('\n');
        for (String input : inputs) {
            String[] lines = input.strip().split("\\R", -1);
            batch.append(lines.length).append('\n');
            for (String line : lines) {
                batch.append(line).append('\n');
            }
        }
        return batch.toString();
    }

    static List<CaseOutcome> parse(String stdout) {
        List<CaseOutcome> cases = new ArrayList<>();
        Long micros = null;
        String printed = "";
        for (String line : stdout.split("\\R")) {
            if (line.startsWith("@@CASE ")) {
                micros = Long.parseLong(line.substring(7).trim());
                printed = "";
            } else if (line.startsWith("@@STDOUT ") && micros != null) {
                printed = unescape(line.substring(9));
            } else if (line.startsWith("@@RESULT ") && micros != null) {
                cases.add(new CaseOutcome(unescape(line.substring(9)), printed, null, micros));
                micros = null;
            } else if (line.startsWith("@@ERROR ") && micros != null) {
                cases.add(new CaseOutcome(null, printed, unescape(line.substring(8)), micros));
                micros = null;
            }
        }
        return cases;
    }

    private static String unescape(String text) {
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < text.length()) {
                char next = text.charAt(++i);
                out.append(next == 'n' ? '\n' : next);
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private static CompletableFuture<byte[]> readBounded(InputStream stream, int limit, Process process) {
        return CompletableFuture.supplyAsync(() -> {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            try (stream) {
                int read;
                while ((read = stream.read(chunk)) != -1) {
                    int room = limit - buffer.size();
                    buffer.write(chunk, 0, Math.min(read, room));
                    if (buffer.size() >= limit) {
                        process.destroyForcibly();
                        break;
                    }
                }
            } catch (IOException ignored) {
                // Stream closes when the process is killed.
            }
            return buffer.toByteArray();
        });
    }

    private static String firstLines(String text, int count) {
        return text.lines().limit(count).collect(Collectors.joining("\n"));
    }

    private static String readResource(String path) {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Missing judge resource " + path, e);
        }
    }

    private static void deleteQuietly(Path dir) {
        if (dir == null) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
        } catch (IOException e) {
            log.debug("Could not clean {}", dir, e);
        }
    }
}
