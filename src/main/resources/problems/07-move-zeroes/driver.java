import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            int[] nums = in.intArray();
            new Solution().moveZeroes(nums);
            return JudgeRuntime.format(nums);
        });
    }
}
