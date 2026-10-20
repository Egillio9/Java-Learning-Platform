import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            int[] nums = in.intArray();
            int target = in.integer();
            return JudgeRuntime.format(new Solution().search(nums, target));
        });
    }
}
