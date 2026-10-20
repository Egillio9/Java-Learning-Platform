import java.util.*;

class Solution {
    public int numIslands(char[][] grid) {
        int islands = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] == '1') {
                    islands++;
                    sink(grid, r, c);
                }
            }
        }
        return islands;
    }

    /** Iterative flood fill, so large islands cannot overflow the call stack. */
    private void sink(char[][] grid, int row, int col) {
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[] {row, col});
        grid[row][col] = '0';
        int[][] moves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!stack.isEmpty()) {
            int[] cell = stack.pop();
            for (int[] move : moves) {
                int r = cell[0] + move[0];
                int c = cell[1] + move[1];
                if (r >= 0 && r < grid.length && c >= 0 && c < grid[r].length && grid[r][c] == '1') {
                    grid[r][c] = '0';
                    stack.push(new int[] {r, c});
                }
            }
        }
    }
}
