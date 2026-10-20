import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            int[] coins = in.intArray();
            int amount = in.integer();
            return JudgeRuntime.format(new Solution().coinChange(coins, amount));
        });
    }
}
