Given an integer array `nums` and an integer `target`, return the **indices** of the two numbers that add up to `target`.

Every input has exactly one valid answer, and you may not use the same element twice. The indices may be returned in any order.

**Example 1**

```text
Input:  nums = [2,7,11,15], target = 9
Output: [0,1]
```

**Example 2**

```text
Input:  nums = [3,2,4], target = 6
Output: [1,2]
```

**Example 3**

```text
Input:  nums = [3,3], target = 6
Output: [0,1]
```

**Constraints**

- `2 <= nums.length <= 10^4`
- `-10^9 <= nums[i], target <= 10^9`
- Exactly one valid answer exists.

**Follow-up:** The brute force checks every pair in O(n²). Can you do it in a single pass with O(n) time?
