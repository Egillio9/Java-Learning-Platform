import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            int[] nums = in.intArray();
            int k = in.integer();
            return JudgeRuntime.format(new Solution().findKthLargest(nums, k));
        });
    }
}
