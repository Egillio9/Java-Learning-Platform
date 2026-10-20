import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            int[] a = in.intArray();
            int[] b = in.intArray();
            return JudgeRuntime.format(new Solution().findMedianSortedArrays(a, b));
        });
    }
}
