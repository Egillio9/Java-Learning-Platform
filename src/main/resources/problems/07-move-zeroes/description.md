Move every `0` in `nums` to the end of the array while keeping the relative order of the non-zero elements.

Do it **in place**: the method returns nothing, and the judge inspects `nums` afterwards.

**Example 1**

```text
Input:  nums = [0,1,0,3,12]
Output: [1,3,12,0,0]
```

**Example 2**

```text
Input:  nums = [0]
Output: [0]
```

**Constraints**

- `1 <= nums.length <= 10^4`
- `-2^31 <= nums[i] <= 2^31 - 1`

**Follow-up:** Can you minimise the total number of writes?
