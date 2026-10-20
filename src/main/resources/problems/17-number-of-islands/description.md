`grid` is a map of `"1"` (land) and `"0"` (water). An **island** is a group of land cells connected horizontally or vertically, surrounded by water.

Return the number of islands. Everything outside the grid counts as water.

**Example 1**

```text
Input:  grid = [["1","1","1","1","0"],["1","1","0","1","0"],["1","1","0","0","0"],["0","0","0","0","0"]]
Output: 1
```

**Example 2**

```text
Input:  grid = [["1","1","0","0","0"],["1","1","0","0","0"],["0","0","1","0","0"],["0","0","0","1","1"]]
Output: 3
```

**Constraints**

- `1 <= rows, cols <= 300`
- `grid[i][j]` is `'0'` or `'1'`.
