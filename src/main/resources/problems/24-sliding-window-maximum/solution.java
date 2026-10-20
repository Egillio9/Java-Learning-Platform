import java.util.*;

class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int[] result = new int[nums.length - k + 1];
        Deque<Integer> indices = new ArrayDeque<>(); // values decreasing from front to back
        for (int i = 0; i < nums.length; i++) {
            if (!indices.isEmpty() && indices.peekFirst() <= i - k) {
                indices.pollFirst(); // fell out of the window
            }
            while (!indices.isEmpty() && nums[indices.peekLast()] <= nums[i]) {
                indices.pollLast();
            }
            indices.offerLast(i);
            if (i >= k - 1) {
                result[i - k + 1] = nums[indices.peekFirst()];
            }
        }
        return result;
    }
}
