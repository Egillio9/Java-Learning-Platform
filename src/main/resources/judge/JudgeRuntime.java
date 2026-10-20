import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compiled next to every submission. Reads the batch of test cases from stdin, runs each one through the
 * problem's driver, and reports results on the real stdout using a line protocol the server parses:
 *
 *   @@CASE {micros}
 *   @@STDOUT {escaped text the solution printed}
 *   @@RESULT {escaped formatted return value}   or   @@ERROR {escaped exception}
 */
public final class JudgeRuntime {

    private static final int MAX_CAPTURED_STDOUT = 4_000;

    public interface Case {
        String solve(Input in) throws Exception;
    }

    private JudgeRuntime() {
    }

    public static void run(Case solver) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintStream judgeOut = System.out;
        int cases = Integer.parseInt(reader.readLine().trim());

        for (int i = 0; i < cases; i++) {
            int lineCount = Integer.parseInt(reader.readLine().trim());
            List<String> lines = new ArrayList<>(lineCount);
            for (int j = 0; j < lineCount; j++) {
                lines.add(reader.readLine());
            }

            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
            String result = null;
            String error = null;
            long start = System.nanoTime();
            try {
                result = solver.solve(new Input(lines));
            } catch (Throwable t) {
                error = describe(t);
            }
            long micros = (System.nanoTime() - start) / 1_000;
            System.setOut(judgeOut);

            String printed = captured.toString(StandardCharsets.UTF_8);
            if (printed.length() > MAX_CAPTURED_STDOUT) {
                printed = printed.substring(0, MAX_CAPTURED_STDOUT) + "\n... (output truncated)";
            }
            judgeOut.println("@@CASE " + micros);
            judgeOut.println("@@STDOUT " + escape(printed));
            judgeOut.println(error != null ? "@@ERROR " + escape(error) : "@@RESULT " + escape(result));
        }
        judgeOut.flush();
    }

    private static String describe(Throwable t) {
        StringBuilder message = new StringBuilder(t.getClass().getSimpleName());
        if (t.getMessage() != null) {
            message.append(": ").append(t.getMessage());
        }
        for (StackTraceElement frame : t.getStackTrace()) {
            if ("Solution.java".equals(frame.getFileName())) {
                message.append(" (Solution.java line ").append(frame.getLineNumber()).append(')');
                break;
            }
        }
        return message.toString();
    }

    private static String escape(String text) {
        if (text == null) {
            return "null";
        }
        return text.replace("\\", "\\\\").replace("\r", "").replace("\n", "\\n");
    }

    // ---- input parsing (LeetCode notation, one argument per line) ----

    public static final class Input {
        private final List<String> lines;
        private int next;

        Input(List<String> lines) {
            this.lines = lines;
        }

        private String line() {
            if (next >= lines.size()) {
                throw new IllegalArgumentException("Test input has fewer lines than the function has arguments");
            }
            return lines.get(next++).trim();
        }

        public int integer() {
            return Integer.parseInt(line());
        }

        public long longValue() {
            return Long.parseLong(line());
        }

        public String string() {
            return parseString(line());
        }

        public int[] intArray() {
            return parseIntArray(line());
        }

        public int[][] intMatrix() {
            return parseIntMatrix(line());
        }

        public String[] stringArray() {
            return parseStringArray(line());
        }

        public char[][] charMatrix() {
            String[][] rows = parseStringMatrix(line());
            char[][] grid = new char[rows.length][];
            for (int r = 0; r < rows.length; r++) {
                grid[r] = new char[rows[r].length];
                for (int c = 0; c < rows[r].length; c++) {
                    grid[r][c] = rows[r][c].charAt(0);
                }
            }
            return grid;
        }

        public ListNode linkedList() {
            int[] values = parseIntArray(line());
            ListNode dummy = new ListNode(0);
            ListNode tail = dummy;
            for (int value : values) {
                tail.next = new ListNode(value);
                tail = tail.next;
            }
            return dummy.next;
        }

        /** Level-order with nulls, e.g. [3,9,20,null,null,15,7]. */
        public TreeNode tree() {
            String body = stripBrackets(line());
            if (body.isEmpty()) {
                return null;
            }
            String[] tokens = body.split(",");
            TreeNode root = node(tokens[0]);
            List<TreeNode> queue = new ArrayList<>();
            queue.add(root);
            int head = 0;
            int i = 1;
            while (i < tokens.length && head < queue.size()) {
                TreeNode current = queue.get(head++);
                if (current == null) {
                    continue;
                }
                current.left = node(tokens[i++]);
                queue.add(current.left);
                if (i < tokens.length) {
                    current.right = node(tokens[i++]);
                    queue.add(current.right);
                }
            }
            return root;
        }

        private static TreeNode node(String token) {
            String value = token.trim();
            return value.equals("null") ? null : new TreeNode(Integer.parseInt(value));
        }
    }

    static String stripBrackets(String text) {
        String trimmed = text.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            throw new IllegalArgumentException("Expected a [ ... ] list but got: " + text);
        }
        return trimmed.substring(1, trimmed.length() - 1).trim();
    }

    static int[] parseIntArray(String text) {
        String body = stripBrackets(text);
        if (body.isEmpty()) {
            return new int[0];
        }
        String[] parts = body.split(",");
        int[] values = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            values[i] = Integer.parseInt(parts[i].trim());
        }
        return values;
    }

    static int[][] parseIntMatrix(String text) {
        String body = stripBrackets(text);
        List<int[]> rows = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\[[^\\[\\]]*\\]").matcher(body);
        while (matcher.find()) {
            rows.add(parseIntArray(matcher.group()));
        }
        return rows.toArray(new int[0][]);
    }

    static String parseString(String text) {
        String trimmed = text.trim();
        if (trimmed.length() < 2 || trimmed.charAt(0) != '"' || trimmed.charAt(trimmed.length() - 1) != '"') {
            throw new IllegalArgumentException("Expected a quoted string but got: " + text);
        }
        return unquote(trimmed.substring(1, trimmed.length() - 1));
    }

    static String[] parseStringArray(String text) {
        String body = stripBrackets(text);
        List<String> values = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(body);
        while (matcher.find()) {
            values.add(unquote(matcher.group(1)));
        }
        return values.toArray(new String[0]);
    }

    static String[][] parseStringMatrix(String text) {
        String body = stripBrackets(text);
        List<String[]> rows = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\[[^\\[\\]]*\\]").matcher(body);
        while (matcher.find()) {
            rows.add(parseStringArray(matcher.group()));
        }
        return rows.toArray(new String[0][]);
    }

    private static String unquote(String body) {
        return body.replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
    }

    // ---- output formatting (matches the notation used in expected outputs) ----

    public static String format(int value) {
        return Integer.toString(value);
    }

    public static String format(long value) {
        return Long.toString(value);
    }

    public static String format(boolean value) {
        return Boolean.toString(value);
    }

    public static String format(double value) {
        return String.format(java.util.Locale.ROOT, "%.5f", value);
    }

    public static String format(String value) {
        return value == null ? "null" : "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    public static String format(int[] values) {
        if (values == null) {
            return "null";
        }
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) out.append(',');
            out.append(values[i]);
        }
        return out.append(']').toString();
    }

    public static String format(int[][] rows) {
        if (rows == null) {
            return "null";
        }
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < rows.length; i++) {
            if (i > 0) out.append(',');
            out.append(format(rows[i]));
        }
        return out.append(']').toString();
    }

    public static String formatIntegers(List<Integer> values) {
        if (values == null) {
            return "null";
        }
        int[] array = new int[values.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = values.get(i);
        }
        return format(array);
    }

    public static String formatNested(List<List<Integer>> rows) {
        if (rows == null) {
            return "null";
        }
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) out.append(',');
            out.append(formatIntegers(rows.get(i)));
        }
        return out.append(']').toString();
    }

    public static String formatStrings(List<String> values) {
        if (values == null) {
            return "null";
        }
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) out.append(',');
            out.append(format(values.get(i)));
        }
        return out.append(']').toString();
    }

    public static String format(ListNode head) {
        StringBuilder out = new StringBuilder("[");
        int guard = 0;
        for (ListNode node = head; node != null; node = node.next) {
            if (guard++ > 100_000) {
                throw new IllegalStateException("Returned list contains a cycle");
            }
            if (out.length() > 1) out.append(',');
            out.append(node.val);
        }
        return out.append(']').toString();
    }

    /** Level-order with trailing nulls trimmed, same as LeetCode. */
    public static String format(TreeNode root) {
        List<String> tokens = new ArrayList<>();
        List<TreeNode> queue = new ArrayList<>();
        queue.add(root);
        for (int head = 0; head < queue.size(); head++) {
            TreeNode node = queue.get(head);
            if (node == null) {
                tokens.add("null");
                continue;
            }
            tokens.add(Integer.toString(node.val));
            queue.add(node.left);
            queue.add(node.right);
        }
        int end = tokens.size();
        while (end > 0 && tokens.get(end - 1).equals("null")) {
            end--;
        }
        return "[" + String.join(",", tokens.subList(0, end)) + "]";
    }
}
