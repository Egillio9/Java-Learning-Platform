A window of size `k` slides over `nums` from left to right, one position at a time.

Return an array with the maximum value inside the window at each position.

**Example 1**

```text
Input:  nums = [1,3,-1,-3,5,3,6,7], k = 3
Output: [3,3,5,5,6,7]
```

**Example 2**

```text
Input:  nums = [1], k = 1
Output: [1]
```

**Constraints**

- `1 <= nums.length <= 10^5`
- `1 <= k <= nums.length`
- `-10^4 <= nums[i] <= 10^4`

**Follow-up:** A heap gives O(n log n). Can you reach O(n) with a deque?
