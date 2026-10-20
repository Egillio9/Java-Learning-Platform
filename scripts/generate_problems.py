"""Generates src/main/resources/problems/<NN-slug>/ for the JavaLearn problem bank."""
import json
import os
import sys
import textwrap

LIST_NODE_DOC = """/**
 * Definition for singly-linked list (provided by the judge):
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
"""

TREE_NODE_DOC = """/**
 * Definition for a binary tree node (provided by the judge):
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int val) { this.val = val; }
 * }
 */
"""

P = []


def problem(**kw):
    P.append(kw)


# ---------------------------------------------------------------- EASY
problem(
    slug="fizz-buzz", title="Fizz Buzz", difficulty="EASY", tags=["Math", "String", "Simulation"],
    params=["n"],
    statement="""Return a list of strings for the numbers `1` to `n` (inclusive), where the entry for number `i` is:

- `"FizzBuzz"` if `i` is divisible by both 3 and 5,
- `"Fizz"` if `i` is divisible by 3 only,
- `"Buzz"` if `i` is divisible by 5 only,
- otherwise `i` itself, written as a string.

This is the classic warm-up for loops, the modulo operator and building a `List<String>`.""",
    constraints=["`1 <= n <= 10^4`"],
    starter="""class Solution {
    public List<String> fizzBuzz(int n) {

    }
}""",
    driver="return JudgeRuntime.formatStrings(new Solution().fizzBuzz(in.integer()));",
    solution="""class Solution {
    public List<String> fizzBuzz(int n) {
        List<String> out = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            if (i % 15 == 0) out.add("FizzBuzz");
            else if (i % 3 == 0) out.add("Fizz");
            else if (i % 5 == 0) out.add("Buzz");
            else out.add(String.valueOf(i));
        }
        return out;
    }
}""",
    samples=[("3", '["1","2","Fizz"]'), ("5", '["1","2","Fizz","4","Buzz"]')],
    hidden=["1", "15", "16", "30"],
)

problem(
    slug="two-sum", title="Two Sum", difficulty="EASY", tags=["Array", "Hash Table"],
    params=["nums", "target"],
    statement="""Given an integer array `nums` and an integer `target`, return the **indices** of the two numbers that add up to `target`.

Every input has exactly one valid answer, and you may not use the same element twice. The indices may be returned in any order.""",
    constraints=["`2 <= nums.length <= 10^4`", "`-10^9 <= nums[i], target <= 10^9`", "Exactly one valid answer exists."],
    followup="The brute force checks every pair in O(n²). Can you do it in a single pass with O(n) time?",
    starter="""class Solution {
    public int[] twoSum(int[] nums, int target) {

    }
}""",
    driver="""int[] nums = in.intArray();
            int target = in.integer();
            int[] answer = new Solution().twoSum(nums, target);
            if (answer != null) Arrays.sort(answer); // any order is accepted
            return JudgeRuntime.format(answer);""",
    solution="""class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Integer j = seen.get(target - nums[i]);
            if (j != null) {
                return new int[] {j, i};
            }
            seen.put(nums[i], i);
        }
        return new int[0];
    }
}""",
    samples=[("[2,7,11,15]\n9", "[0,1]"), ("[3,2,4]\n6", "[1,2]"), ("[3,3]\n6", "[0,1]")],
    hidden=["[1,5,9,13,2]\n15", "[-3,4,3,90]\n0", "[0,4,3,0]\n0", "[5,75,25]\n100", "[-1,-2,-3,-4,-5]\n-8"],
)

problem(
    slug="valid-parentheses", title="Valid Parentheses", difficulty="EASY", tags=["String", "Stack"],
    params=["s"],
    statement="""Given a string `s` made only of the characters `(`, `)`, `{`, `}`, `[` and `]`, decide whether it is **valid**.

A string is valid when every opening bracket is closed by the same type of bracket, brackets close in the correct order, and every closing bracket has a matching opening bracket.""",
    constraints=["`1 <= s.length <= 10^4`", "`s` contains only bracket characters."],
    starter="""class Solution {
    public boolean isValid(String s) {

    }
}""",
    driver="return JudgeRuntime.format(new Solution().isValid(in.string()));",
    solution="""class Solution {
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '(' -> stack.push(')');
                case '[' -> stack.push(']');
                case '{' -> stack.push('}');
                default -> {
                    if (stack.isEmpty() || stack.pop() != c) {
                        return false;
                    }
                }
            }
        }
        return stack.isEmpty();
    }
}""",
    samples=[('"()"', "true"), ('"()[]{}"', "true"), ('"(]"', "false"), ('"([])"', "true")],
    hidden=['"([)]"', '"{[]}"', '"("', '"]"', '"((((((()))))))"', '"[{()}](){}"'],
)

problem(
    slug="best-time-to-buy-and-sell-stock", title="Best Time to Buy and Sell Stock", difficulty="EASY",
    tags=["Array", "Greedy"], params=["prices"],
    statement="""`prices[i]` is the price of a stock on day `i`. You may buy once and sell once, on a **later** day than you bought.

Return the maximum profit you can make. If no profit is possible, return `0`.""",
    constraints=["`1 <= prices.length <= 10^5`", "`0 <= prices[i] <= 10^4`"],
    starter="""class Solution {
    public int maxProfit(int[] prices) {

    }
}""",
    driver="return JudgeRuntime.format(new Solution().maxProfit(in.intArray()));",
    solution="""class Solution {
    public int maxProfit(int[] prices) {
        int lowest = Integer.MAX_VALUE;
        int best = 0;
        for (int price : prices) {
            lowest = Math.min(lowest, price);
            best = Math.max(best, price - lowest);
        }
        return best;
    }
}""",
    samples=[("[7,1,5,3,6,4]", "5"), ("[7,6,4,3,1]", "0")],
    hidden=["[1]", "[2,4,1]", "[3,2,6,5,0,3]", "[1,2,3,4,5,6,7,8,9,10]", "[9,1,9,1,9]"],
)

problem(
    slug="valid-anagram", title="Valid Anagram", difficulty="EASY", tags=["String", "Hash Table", "Sorting"],
    params=["s", "t"],
    statement="""Given two strings `s` and `t`, return `true` if `t` is an **anagram** of `s` (it uses exactly the same letters, the same number of times), and `false` otherwise.""",
    constraints=["`1 <= s.length, t.length <= 5 * 10^4`", "`s` and `t` contain lowercase English letters."],
    followup="What changes if the strings may contain any Unicode characters?",
    starter="""class Solution {
    public boolean isAnagram(String s, String t) {

    }
}""",
    driver="""String s = in.string();
            String t = in.string();
            return JudgeRuntime.format(new Solution().isAnagram(s, t));""",
    solution="""class Solution {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        int[] counts = new int[26];
        for (int i = 0; i < s.length(); i++) {
            counts[s.charAt(i) - 'a']++;
            counts[t.charAt(i) - 'a']--;
        }
        for (int count : counts) {
            if (count != 0) {
                return false;
            }
        }
        return true;
    }
}""",
    samples=[('"anagram"\n"nagaram"', "true"), ('"rat"\n"car"', "false")],
    hidden=['"a"\n"ab"', '"listen"\n"silent"', '"aacc"\n"ccac"', '"x"\n"x"', '"abcdefghij"\n"jihgfedcba"'],
)

problem(
    slug="binary-search", title="Binary Search", difficulty="EASY", tags=["Array", "Binary Search"],
    params=["nums", "target"],
    statement="""Given a sorted (ascending) array of **distinct** integers `nums` and a `target`, return the index of `target`, or `-1` if it is not present.

Your algorithm must run in `O(log n)` time.""",
    constraints=["`1 <= nums.length <= 10^4`", "All values in `nums` are unique and sorted ascending."],
    starter="""class Solution {
    public int search(int[] nums, int target) {

    }
}""",
    driver="""int[] nums = in.intArray();
            int target = in.integer();
            return JudgeRuntime.format(new Solution().search(nums, target));""",
    solution="""class Solution {
    public int search(int[] nums, int target) {
        int lo = 0;
        int hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2; // avoids int overflow of (lo + hi)
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }
}""",
    samples=[("[-1,0,3,5,9,12]\n9", "4"), ("[-1,0,3,5,9,12]\n2", "-1")],
    hidden=["[5]\n5", "[5]\n-5", "[1,3,5,7,9,11,13]\n1", "[1,3,5,7,9,11,13]\n13", "[2,4]\n3"],
)

problem(
    slug="move-zeroes", title="Move Zeroes", difficulty="EASY", tags=["Array", "Two Pointers"],
    params=["nums"],
    statement="""Move every `0` in `nums` to the end of the array while keeping the relative order of the non-zero elements.

Do it **in place**: the method returns nothing, and the judge inspects `nums` afterwards.""",
    constraints=["`1 <= nums.length <= 10^4`", "`-2^31 <= nums[i] <= 2^31 - 1`"],
    followup="Can you minimise the total number of writes?",
    starter="""class Solution {
    public void moveZeroes(int[] nums) {

    }
}""",
    driver="""int[] nums = in.intArray();
            new Solution().moveZeroes(nums);
            return JudgeRuntime.format(nums);""",
    solution="""class Solution {
    public void moveZeroes(int[] nums) {
        int write = 0;
        for (int read = 0; read < nums.length; read++) {
            if (nums[read] != 0) {
                int tmp = nums[write];
                nums[write++] = nums[read];
                nums[read] = tmp;
            }
        }
    }
}""",
    samples=[("[0,1,0,3,12]", "[1,3,12,0,0]"), ("[0]", "[0]")],
    hidden=["[1,2,3]", "[0,0,1]", "[4,0,5,0,0,6,7]", "[0,0,0,0]"],
)

problem(
    slug="reverse-linked-list", title="Reverse Linked List", difficulty="EASY", tags=["Linked List", "Recursion"],
    params=["head"],
    statement="""Given the `head` of a singly linked list, reverse the list and return the new head.""",
    constraints=["The number of nodes is in the range `[0, 5000]`.", "`-5000 <= Node.val <= 5000`"],
    followup="Solve it both iteratively and recursively.",
    starter=LIST_NODE_DOC + """class Solution {
    public ListNode reverseList(ListNode head) {

    }
}""",
    driver="return JudgeRuntime.format(new Solution().reverseList(in.linkedList()));",
    solution="""class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode previous = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = previous;
            previous = head;
            head = next;
        }
        return previous;
    }
}""",
    samples=[("[1,2,3,4,5]", "[5,4,3,2,1]"), ("[1,2]", "[2,1]"), ("[]", "[]")],
    hidden=["[7]", "[1,1,2,3,5,8,13,21]", "[-1,0,1]"],
)

problem(
    slug="merge-two-sorted-lists", title="Merge Two Sorted Lists", difficulty="EASY",
    tags=["Linked List", "Recursion"], params=["list1", "list2"],
    statement="""You are given the heads of two linked lists, each sorted in non-decreasing order.

Splice their nodes together into **one** sorted list and return its head.""",
    constraints=["Each list has between `0` and `50` nodes.", "`-100 <= Node.val <= 100`"],
    starter=LIST_NODE_DOC + """class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {

    }
}""",
    driver="""ListNode a = in.linkedList();
            ListNode b = in.linkedList();
            return JudgeRuntime.format(new Solution().mergeTwoLists(a, b));""",
    solution="""class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                tail.next = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }
        tail.next = list1 != null ? list1 : list2;
        return dummy.next;
    }
}""",
    samples=[("[1,2,4]\n[1,3,4]", "[1,1,2,3,4,4]"), ("[]\n[]", "[]"), ("[]\n[0]", "[0]")],
    hidden=["[5]\n[1,2,4]", "[-10,-3,0,7]\n[-5,8]", "[1,1,1]\n[1,1]", "[2]\n[]"],
)

problem(
    slug="maximum-depth-of-binary-tree", title="Maximum Depth of Binary Tree", difficulty="EASY",
    tags=["Tree", "Depth-First Search", "Recursion"], params=["root"],
    statement="""Return the **maximum depth** of a binary tree: the number of nodes on the longest path from the root down to a leaf.

Trees are written in level order, with `null` marking a missing child.""",
    constraints=["The number of nodes is in the range `[0, 10^4]`."],
    starter=TREE_NODE_DOC + """class Solution {
    public int maxDepth(TreeNode root) {

    }
}""",
    driver="return JudgeRuntime.format(new Solution().maxDepth(in.tree()));",
    solution="""class Solution {
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }
}""",
    samples=[("[3,9,20,null,null,15,7]", "3"), ("[1,null,2]", "2")],
    hidden=["[]", "[1]", "[1,2,null,3,null,4,null,5]", "[1,2,3,4,5,6,7]"],
)

problem(
    slug="climbing-stairs", title="Climbing Stairs", difficulty="EASY", tags=["Dynamic Programming", "Math"],
    params=["n"],
    statement="""A staircase has `n` steps. Each move climbs either **1** or **2** steps.

In how many distinct ways can you reach the top?""",
    constraints=["`1 <= n <= 45`"],
    starter="""class Solution {
    public int climbStairs(int n) {

    }
}""",
    driver="return JudgeRuntime.format(new Solution().climbStairs(in.integer()));",
    solution="""class Solution {
    public int climbStairs(int n) {
        int previous = 1;
        int current = 1;
        for (int step = 2; step <= n; step++) {
            int next = previous + current;
            previous = current;
            current = next;
        }
        return current;
    }
}""",
    samples=[("2", "2"), ("3", "3")],
    hidden=["1", "5", "10", "30", "45"],
)

# ---------------------------------------------------------------- MEDIUM
problem(
    slug="maximum-subarray", title="Maximum Subarray", difficulty="MEDIUM",
    tags=["Array", "Dynamic Programming", "Divide and Conquer"], params=["nums"],
    statement="""Find the contiguous, non-empty subarray of `nums` with the largest sum, and return that sum.""",
    constraints=["`1 <= nums.length <= 10^5`", "`-10^4 <= nums[i] <= 10^4`"],
    followup="Once you have the O(n) solution, try the divide-and-conquer approach.",
    starter="""class Solution {
    public int maxSubArray(int[] nums) {

    }
}""",
    driver="return JudgeRuntime.format(new Solution().maxSubArray(in.intArray()));",
    solution="""class Solution {
    public int maxSubArray(int[] nums) {
        int best = nums[0];
        int current = nums[0];
        for (int i = 1; i < nums.length; i++) {
            current = Math.max(nums[i], current + nums[i]); // Kadane: extend or restart
            best = Math.max(best, current);
        }
        return best;
    }
}""",
    samples=[("[-2,1,-3,4,-1,2,1,-5,4]", "6"), ("[1]", "1"), ("[5,4,-1,7,8]", "23")],
    hidden=["[-3,-1,-2]", "[-1]", "[8,-19,5,-4,20]", "[1,2,3,-100,4,5]"],
)

problem(
    slug="longest-substring-without-repeating-characters",
    title="Longest Substring Without Repeating Characters", difficulty="MEDIUM",
    tags=["String", "Sliding Window", "Hash Table"], params=["s"],
    statement="""Given a string `s`, return the length of the longest **substring** (a contiguous block of characters) in which no character repeats.""",
    constraints=["`0 <= s.length <= 5 * 10^4`", "`s` may contain letters, digits, symbols and spaces."],
    starter="""class Solution {
    public int lengthOfLongestSubstring(String s) {

    }
}""",
    driver="return JudgeRuntime.format(new Solution().lengthOfLongestSubstring(in.string()));",
    solution="""class Solution {
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
}""",
    samples=[('"abcabcbb"', "3"), ('"bbbbb"', "1"), ('"pwwkew"', "3")],
    hidden=['""', '"dvdf"', '" "', '"abba"', '"tmmzuxt"'],
)

problem(
    slug="merge-intervals", title="Merge Intervals", difficulty="MEDIUM", tags=["Array", "Sorting"],
    params=["intervals"],
    statement="""Given an array of `intervals` where `intervals[i] = [start, end]`, merge all overlapping intervals and return the non-overlapping intervals that cover exactly the same ranges.

Intervals that touch (one ends where the next starts) also merge.""",
    constraints=["`1 <= intervals.length <= 10^4`", "`0 <= start <= end <= 10^4`"],
    starter="""class Solution {
    public int[][] merge(int[][] intervals) {

    }
}""",
    driver="""int[][] merged = new Solution().merge(in.intMatrix());
            if (merged != null) Arrays.sort(merged, (x, y) -> Integer.compare(x[0], y[0]));
            return JudgeRuntime.format(merged);""",
    solution="""class Solution {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> merged = new ArrayList<>();
        for (int[] interval : intervals) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                merged.add(new int[] {interval[0], interval[1]});
            } else {
                int[] last = merged.get(merged.size() - 1);
                last[1] = Math.max(last[1], interval[1]);
            }
        }
        return merged.toArray(new int[0][]);
    }
}""",
    samples=[("[[1,3],[2,6],[8,10],[15,18]]", "[[1,6],[8,10],[15,18]]"), ("[[1,4],[4,5]]", "[[1,5]]"),
             ("[[4,7],[1,4]]", "[[1,7]]")],
    hidden=["[[1,4],[0,4]]", "[[1,4],[2,3]]", "[[1,2]]", "[[2,3],[4,5],[6,7],[8,9],[1,10]]", "[[5,6],[1,2],[3,4]]"],
)

problem(
    slug="product-of-array-except-self", title="Product of Array Except Self", difficulty="MEDIUM",
    tags=["Array", "Prefix Sum"], params=["nums"],
    statement="""Return an array `answer` where `answer[i]` is the product of every element of `nums` **except** `nums[i]`.

Solve it in `O(n)` time **without using division**. All products fit in a 32-bit integer.""",
    constraints=["`2 <= nums.length <= 10^5`", "`-30 <= nums[i] <= 30`"],
    followup="Can you use only O(1) extra space, not counting the output array?",
    starter="""class Solution {
    public int[] productExceptSelf(int[] nums) {

    }
}""",
    driver="return JudgeRuntime.format(new Solution().productExceptSelf(in.intArray()));",
    solution="""class Solution {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];
        answer[0] = 1;
        for (int i = 1; i < n; i++) {
            answer[i] = answer[i - 1] * nums[i - 1]; // product of everything to the left
        }
        int right = 1;
        for (int i = n - 1; i >= 0; i--) {
            answer[i] *= right;
            right *= nums[i];
        }
        return answer;
    }
}""",
    samples=[("[1,2,3,4]", "[24,12,8,6]"), ("[-1,1,0,-3,3]", "[0,0,9,0,0]")],
    hidden=["[2,3]", "[0,0]", "[5,1,1,1]", "[-2,-3,4,1]"],
)

problem(
    slug="group-anagrams", title="Group Anagrams", difficulty="MEDIUM", tags=["String", "Hash Table", "Sorting"],
    params=["strs"],
    statement="""Group the strings in `strs` so that words which are anagrams of each other end up in the same group.

You may return the groups, and the words inside each group, in any order.""",
    constraints=["`1 <= strs.length <= 10^4`", "`0 <= strs[i].length <= 100`", "Only lowercase English letters."],
    starter="""class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {

    }
}""",
    driver="""List<List<String>> groups = new Solution().groupAnagrams(in.stringArray());
            if (groups == null) return "null";
            List<List<String>> sorted = new ArrayList<>();
            for (List<String> group : groups) {
                List<String> copy = new ArrayList<>(group);
                Collections.sort(copy);
                sorted.add(copy);
            }
            sorted.sort((x, y) -> String.join(",", x).compareTo(String.join(",", y)));
            StringBuilder out = new StringBuilder("[");
            for (int i = 0; i < sorted.size(); i++) {
                if (i > 0) out.append(',');
                out.append(JudgeRuntime.formatStrings(sorted.get(i)));
            }
            return out.append(']').toString();""",
    solution="""class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String word : strs) {
            char[] letters = word.toCharArray();
            Arrays.sort(letters);
            groups.computeIfAbsent(new String(letters), key -> new ArrayList<>()).add(word);
        }
        return new ArrayList<>(groups.values());
    }
}""",
    samples=[('["eat","tea","tan","ate","nat","bat"]', '[["ate","eat","tea"],["bat"],["nat","tan"]]'),
             ('[""]', '[[""]]'), ('["a"]', '[["a"]]')],
    hidden=['["abc","bca","cab","xyz","zyx","yxz","foo"]', '["ab","ba","abc"]', '["",""]', '["listen","silent","enlist","google"]'],
)

problem(
    slug="number-of-islands", title="Number of Islands", difficulty="MEDIUM",
    tags=["Graph", "Breadth-First Search", "Depth-First Search", "Matrix"], params=["grid"],
    statement="""`grid` is a map of `"1"` (land) and `"0"` (water). An **island** is a group of land cells connected horizontally or vertically, surrounded by water.

Return the number of islands. Everything outside the grid counts as water.""",
    constraints=["`1 <= rows, cols <= 300`", "`grid[i][j]` is `'0'` or `'1'`."],
    starter="""class Solution {
    public int numIslands(char[][] grid) {

    }
}""",
    driver="return JudgeRuntime.format(new Solution().numIslands(in.charMatrix()));",
    solution="""class Solution {
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
}""",
    samples=[('[["1","1","1","1","0"],["1","1","0","1","0"],["1","1","0","0","0"],["0","0","0","0","0"]]', "1"),
             ('[["1","1","0","0","0"],["1","1","0","0","0"],["0","0","1","0","0"],["0","0","0","1","1"]]', "3")],
    hidden=['[["0"]]', '[["1"]]', '[["1","0","1","0","1"]]', '[["1","0","1"],["0","1","0"],["1","0","1"]]',
            '[["1","1","1"],["0","1","0"],["1","1","1"]]'],
)

problem(
    slug="binary-tree-level-order-traversal", title="Binary Tree Level Order Traversal", difficulty="MEDIUM",
    tags=["Tree", "Breadth-First Search", "Queue"], params=["root"],
    statement="""Return the values of a binary tree level by level: a list per level, left to right within each level, starting from the root.""",
    constraints=["The number of nodes is in the range `[0, 2000]`."],
    starter=TREE_NODE_DOC + """class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {

    }
}""",
    driver="return JudgeRuntime.formatNested(new Solution().levelOrder(in.tree()));",
    solution="""class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> levels = new ArrayList<>();
        if (root == null) {
            return levels;
        }
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            levels.add(level);
        }
        return levels;
    }
}""",
    samples=[("[3,9,20,null,null,15,7]", "[[3],[9,20],[15,7]]"), ("[1]", "[[1]]"), ("[]", "[]")],
    hidden=["[1,2,3,4,5,6,7]", "[1,2,null,3,null,4]", "[5,4,8,11,null,13,4,7,2,null,null,null,1]"],
)

problem(
    slug="kth-largest-element-in-an-array", title="Kth Largest Element in an Array", difficulty="MEDIUM",
    tags=["Array", "Heap (Priority Queue)", "Sorting"], params=["nums", "k"],
    statement="""Return the `k`-th largest element of `nums`: the element that would be at position `k` if the array were sorted in **descending** order (duplicates count separately).

Try to beat a full sort.""",
    constraints=["`1 <= k <= nums.length <= 10^5`", "`-10^4 <= nums[i] <= 10^4`"],
    starter="""class Solution {
    public int findKthLargest(int[] nums, int k) {

    }
}""",
    driver="""int[] nums = in.intArray();
            int k = in.integer();
            return JudgeRuntime.format(new Solution().findKthLargest(nums, k));""",
    solution="""class Solution {
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
}""",
    samples=[("[3,2,1,5,6,4]\n2", "5"), ("[3,2,3,1,2,4,5,5,6]\n4", "4")],
    hidden=["[1]\n1", "[7,7,7]\n2", "[-1,-5,3,9,0]\n5", "[2,1]\n1"],
)

problem(
    slug="coin-change", title="Coin Change", difficulty="MEDIUM",
    tags=["Array", "Dynamic Programming", "Breadth-First Search"], params=["coins", "amount"],
    statement="""You have an unlimited supply of coins of each denomination in `coins`. Return the **fewest** coins needed to make exactly `amount`, or `-1` if it cannot be made.""",
    constraints=["`1 <= coins.length <= 12`", "`1 <= coins[i] <= 2^31 - 1`", "`0 <= amount <= 10^4`"],
    starter="""class Solution {
    public int coinChange(int[] coins, int amount) {

    }
}""",
    driver="""int[] coins = in.intArray();
            int amount = in.integer();
            return JudgeRuntime.format(new Solution().coinChange(coins, amount));""",
    solution="""class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] fewest = new int[amount + 1];
        Arrays.fill(fewest, amount + 1); // larger than any real answer
        fewest[0] = 0;
        for (int value = 1; value <= amount; value++) {
            for (int coin : coins) {
                if (coin <= value) {
                    fewest[value] = Math.min(fewest[value], fewest[value - coin] + 1);
                }
            }
        }
        return fewest[amount] > amount ? -1 : fewest[amount];
    }
}""",
    samples=[("[1,2,5]\n11", "3"), ("[2]\n3", "-1"), ("[1]\n0", "0")],
    hidden=["[186,419,83,408]\n6249", "[2,5,10,1]\n27", "[3,7]\n5", "[1]\n10000"],
)

problem(
    slug="search-in-rotated-sorted-array", title="Search in Rotated Sorted Array", difficulty="MEDIUM",
    tags=["Array", "Binary Search"], params=["nums", "target"],
    statement="""An ascending array of distinct integers was **rotated** at an unknown pivot, e.g. `[0,1,2,4,5,6,7]` may have become `[4,5,6,7,0,1,2]`.

Given the rotated array `nums` and a `target`, return its index or `-1`, in `O(log n)` time.""",
    constraints=["`1 <= nums.length <= 5000`", "All values are unique."],
    starter="""class Solution {
    public int search(int[] nums, int target) {

    }
}""",
    driver="""int[] nums = in.intArray();
            int target = in.integer();
            return JudgeRuntime.format(new Solution().search(nums, target));""",
    solution="""class Solution {
    public int search(int[] nums, int target) {
        int lo = 0;
        int hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[lo] <= nums[mid]) { // left half is sorted
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else lo = mid + 1;
            } else { // right half is sorted
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else hi = mid - 1;
            }
        }
        return -1;
    }
}""",
    samples=[("[4,5,6,7,0,1,2]\n0", "4"), ("[4,5,6,7,0,1,2]\n3", "-1"), ("[1]\n0", "-1")],
    hidden=["[1]\n1", "[3,1]\n1", "[5,1,3]\n3", "[6,7,8,1,2,3,4,5]\n8", "[6,7,8,1,2,3,4,5]\n5"],
)

# ---------------------------------------------------------------- HARD
problem(
    slug="trapping-rain-water", title="Trapping Rain Water", difficulty="HARD",
    tags=["Array", "Two Pointers", "Stack"], params=["height"],
    statement="""`height` describes an elevation map where each bar is 1 unit wide. Compute how many units of rain water are trapped between the bars after it rains.""",
    constraints=["`1 <= height.length <= 2 * 10^4`", "`0 <= height[i] <= 10^5`"],
    starter="""class Solution {
    public int trap(int[] height) {

    }
}""",
    driver="return JudgeRuntime.format(new Solution().trap(in.intArray()));",
    solution="""class Solution {
    public int trap(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int water = 0;
        while (left < right) {
            if (height[left] < height[right]) {
                leftMax = Math.max(leftMax, height[left]);
                water += leftMax - height[left++];
            } else {
                rightMax = Math.max(rightMax, height[right]);
                water += rightMax - height[right--];
            }
        }
        return water;
    }
}""",
    samples=[("[0,1,0,2,1,0,1,3,2,1,2,1]", "6"), ("[4,2,0,3,2,5]", "9")],
    hidden=["[1]", "[5,4,1,2]", "[2,0,2]", "[0,0,0]", "[5,2,1,2,1,5]"],
)

problem(
    slug="median-of-two-sorted-arrays", title="Median of Two Sorted Arrays", difficulty="HARD",
    tags=["Array", "Binary Search", "Divide and Conquer"], params=["nums1", "nums2"],
    statement="""Given two sorted arrays `nums1` and `nums2`, return the **median** of all their elements combined.

The answer is checked to 5 decimal places. The intended solution runs in `O(log(m + n))`.""",
    constraints=["`0 <= m, n <= 1000`", "`1 <= m + n <= 2000`", "`-10^6 <= nums1[i], nums2[i] <= 10^6`"],
    starter="""class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {

    }
}""",
    driver="""int[] a = in.intArray();
            int[] b = in.intArray();
            return JudgeRuntime.format(new Solution().findMedianSortedArrays(a, b));""",
    solution="""class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            return findMedianSortedArrays(nums2, nums1); // binary search the shorter array
        }
        int m = nums1.length;
        int n = nums2.length;
        int half = (m + n + 1) / 2;
        int lo = 0;
        int hi = m;
        while (lo <= hi) {
            int cut1 = (lo + hi) / 2;
            int cut2 = half - cut1;
            int left1 = cut1 == 0 ? Integer.MIN_VALUE : nums1[cut1 - 1];
            int right1 = cut1 == m ? Integer.MAX_VALUE : nums1[cut1];
            int left2 = cut2 == 0 ? Integer.MIN_VALUE : nums2[cut2 - 1];
            int right2 = cut2 == n ? Integer.MAX_VALUE : nums2[cut2];
            if (left1 <= right2 && left2 <= right1) {
                if ((m + n) % 2 == 1) {
                    return Math.max(left1, left2);
                }
                return (Math.max(left1, left2) + (double) Math.min(right1, right2)) / 2.0;
            } else if (left1 > right2) {
                hi = cut1 - 1;
            } else {
                lo = cut1 + 1;
            }
        }
        throw new IllegalArgumentException("Input arrays are not sorted");
    }
}""",
    samples=[("[1,3]\n[2]", "2.00000"), ("[1,2]\n[3,4]", "2.50000")],
    hidden=["[]\n[1]", "[0,0]\n[0,0]", "[1,3,5,7]\n[2,4,6]", "[-5,3,6,12,15]\n[-12,-10,-6,-3,4,10]", "[2]\n[]"],
)

problem(
    slug="sliding-window-maximum", title="Sliding Window Maximum", difficulty="HARD",
    tags=["Array", "Sliding Window", "Monotonic Queue"], params=["nums", "k"],
    statement="""A window of size `k` slides over `nums` from left to right, one position at a time.

Return an array with the maximum value inside the window at each position.""",
    constraints=["`1 <= nums.length <= 10^5`", "`1 <= k <= nums.length`", "`-10^4 <= nums[i] <= 10^4`"],
    followup="A heap gives O(n log n). Can you reach O(n) with a deque?",
    starter="""class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {

    }
}""",
    driver="""int[] nums = in.intArray();
            int k = in.integer();
            return JudgeRuntime.format(new Solution().maxSlidingWindow(nums, k));""",
    solution="""class Solution {
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
}""",
    samples=[("[1,3,-1,-3,5,3,6,7]\n3", "[3,3,5,5,6,7]"), ("[1]\n1", "[1]")],
    hidden=["[9,11]\n2", "[4,-2]\n2", "[1,3,1,2,0,5]\n3", "[7,2,4]\n2", "[1,2,3,4,5]\n1"],
)


def example_block(params, inp, out, n):
    values = inp.split("\n")
    shown = ", ".join(f"{name} = {value}" for name, value in zip(params, values))
    return f"**Example {n}**\n\n```text\nInput:  {shown}\nOutput: {out}\n```\n"


IMPORTS = "import java.util.*;\n\n"


def main(root):
    for index, p in enumerate(P, start=1):
        folder = os.path.join(root, f"{index:02d}-{p['slug']}")
        os.makedirs(folder, exist_ok=True)
        desc = [p["statement"].strip(), ""]
        for n, (inp, out) in enumerate(p["samples"], start=1):
            desc.append(example_block(p["params"], inp, out, n))
        desc.append("**Constraints**\n")
        desc.extend(f"- {c}" for c in p["constraints"])
        if p.get("followup"):
            desc.append(f"\n**Follow-up:** {p['followup']}")
        files = {
            "description.md": "\n".join(desc) + "\n",
            "starter.java": IMPORTS + p["starter"] + "\n",
            "solution.java": IMPORTS + p["solution"] + "\n",
            "driver.java": IMPORTS + "public class Main {\n    public static void main(String[] args) throws Exception {\n"
                           "        JudgeRuntime.run(in -> {\n            " + p["driver"] + "\n        });\n    }\n}\n",
            "problem.json": json.dumps({
                "slug": p["slug"], "title": p["title"], "difficulty": p["difficulty"], "params": p["params"],
                "tags": p["tags"], "position": index,
                "tests": [{"input": i, "expected": o, "sample": True} for i, o in p["samples"]]
                         + [{"input": i, "sample": False} for i in p["hidden"]],
            }, indent=2) + "\n",
        }
        for name, content in files.items():
            with open(os.path.join(folder, name), "w", encoding="utf-8", newline="\n") as fh:
                fh.write(content)
    print(f"wrote {len(P)} problems to {root}")


if __name__ == "__main__":
    main(sys.argv[1])
