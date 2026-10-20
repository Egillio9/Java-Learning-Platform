import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            return JudgeRuntime.formatStrings(new Solution().fizzBuzz(in.integer()));
        });
    }
}
