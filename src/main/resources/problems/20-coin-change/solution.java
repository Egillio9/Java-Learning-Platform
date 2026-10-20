import java.util.*;

class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] fewest = new int[amount + 1];
        Arrays.fill(fewest, amount + 1); // larger than any real answer
        fewest[0] = 0;
        for (int value = 1; value <= amount; value++) {
            for (int coin : coins) {
                if (coin <= value) {
                    fewest[value] = Math.min(fewest[value], fewest[value - coin] + 1);
                }
            }
        }
        return fewest[amount] > amount ? -1 : fewest[amount];
    }
}
