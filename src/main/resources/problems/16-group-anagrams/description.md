Group the strings in `strs` so that words which are anagrams of each other end up in the same group.

You may return the groups, and the words inside each group, in any order.

**Example 1**

```text
Input:  strs = ["eat","tea","tan","ate","nat","bat"]
Output: [["ate","eat","tea"],["bat"],["nat","tan"]]
```

**Example 2**

```text
Input:  strs = [""]
Output: [[""]]
```

**Example 3**

```text
Input:  strs = ["a"]
Output: [["a"]]
```

**Constraints**

- `1 <= strs.length <= 10^4`
- `0 <= strs[i].length <= 100`
- Only lowercase English letters.
