Given an array of `intervals` where `intervals[i] = [start, end]`, merge all overlapping intervals and return the non-overlapping intervals that cover exactly the same ranges.

Intervals that touch (one ends where the next starts) also merge.

**Example 1**

```text
Input:  intervals = [[1,3],[2,6],[8,10],[15,18]]
Output: [[1,6],[8,10],[15,18]]
```

**Example 2**

```text
Input:  intervals = [[1,4],[4,5]]
Output: [[1,5]]
```

**Example 3**

```text
Input:  intervals = [[4,7],[1,4]]
Output: [[1,7]]
```

**Constraints**

- `1 <= intervals.length <= 10^4`
- `0 <= start <= end <= 10^4`
