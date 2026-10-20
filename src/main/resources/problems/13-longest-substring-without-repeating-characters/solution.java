import java.util.*;

class Solution {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastSeen = new HashMap<>();
        int best = 0;
        int start = 0;
        for (int end = 0; end < s.length(); end++) {
            Integer previous = lastSeen.put(s.charAt(end), end);
            if (previous != null && previous >= start) {
                start = previous + 1;
            }
            best = Math.max(best, end - start + 1);
        }
        return best;
    }
}
