import java.util.*;

class Solution {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> smallestOnTop = new PriorityQueue<>();
        for (int num : nums) {
            smallestOnTop.offer(num);
            if (smallestOnTop.size() > k) {
                smallestOnTop.poll(); // keep only the k largest seen so far
            }
        }
        return smallestOnTop.peek();
    }
}
