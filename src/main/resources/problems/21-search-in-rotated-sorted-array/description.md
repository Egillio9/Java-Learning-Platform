An ascending array of distinct integers was **rotated** at an unknown pivot, e.g. `[0,1,2,4,5,6,7]` may have become `[4,5,6,7,0,1,2]`.

Given the rotated array `nums` and a `target`, return its index or `-1`, in `O(log n)` time.

**Example 1**

```text
Input:  nums = [4,5,6,7,0,1,2], target = 0
Output: 4
```

**Example 2**

```text
Input:  nums = [4,5,6,7,0,1,2], target = 3
Output: -1
```

**Example 3**

```text
Input:  nums = [1], target = 0
Output: -1
```

**Constraints**

- `1 <= nums.length <= 5000`
- All values are unique.
