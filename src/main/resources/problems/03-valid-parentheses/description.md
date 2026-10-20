Given a string `s` made only of the characters `(`, `)`, `{`, `}`, `[` and `]`, decide whether it is **valid**.

A string is valid when every opening bracket is closed by the same type of bracket, brackets close in the correct order, and every closing bracket has a matching opening bracket.

**Example 1**

```text
Input:  s = "()"
Output: true
```

**Example 2**

```text
Input:  s = "()[]{}"
Output: true
```

**Example 3**

```text
Input:  s = "(]"
Output: false
```

**Example 4**

```text
Input:  s = "([])"
Output: true
```

**Constraints**

- `1 <= s.length <= 10^4`
- `s` contains only bracket characters.
