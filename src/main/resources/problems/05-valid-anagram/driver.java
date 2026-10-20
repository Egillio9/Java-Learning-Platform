import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            String s = in.string();
            String t = in.string();
            return JudgeRuntime.format(new Solution().isAnagram(s, t));
        });
    }
}
