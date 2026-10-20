import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            int[][] merged = new Solution().merge(in.intMatrix());
            if (merged != null) Arrays.sort(merged, (x, y) -> Integer.compare(x[0], y[0]));
            return JudgeRuntime.format(merged);
        });
    }
}
