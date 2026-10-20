import java.util.*;

class Solution {
    public int maxProfit(int[] prices) {
        int lowest = Integer.MAX_VALUE;
        int best = 0;
        for (int price : prices) {
            lowest = Math.min(lowest, price);
            best = Math.max(best, price - lowest);
        }
        return best;
    }
}
