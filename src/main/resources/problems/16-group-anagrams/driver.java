import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            List<List<String>> groups = new Solution().groupAnagrams(in.stringArray());
            if (groups == null) return "null";
            List<List<String>> sorted = new ArrayList<>();
            for (List<String> group : groups) {
                List<String> copy = new ArrayList<>(group);
                Collections.sort(copy);
                sorted.add(copy);
            }
            sorted.sort((x, y) -> String.join(",", x).compareTo(String.join(",", y)));
            StringBuilder out = new StringBuilder("[");
            for (int i = 0; i < sorted.size(); i++) {
                if (i > 0) out.append(',');
                out.append(JudgeRuntime.formatStrings(sorted.get(i)));
            }
            return out.append(']').toString();
        });
    }
}
