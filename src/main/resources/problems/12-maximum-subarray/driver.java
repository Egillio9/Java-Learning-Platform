import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            return JudgeRuntime.format(new Solution().maxSubArray(in.intArray()));
        });
    }
}
