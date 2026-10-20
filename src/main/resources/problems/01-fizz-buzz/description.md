Return a list of strings for the numbers `1` to `n` (inclusive), where the entry for number `i` is:

- `"FizzBuzz"` if `i` is divisible by both 3 and 5,
- `"Fizz"` if `i` is divisible by 3 only,
- `"Buzz"` if `i` is divisible by 5 only,
- otherwise `i` itself, written as a string.

This is the classic warm-up for loops, the modulo operator and building a `List<String>`.

**Example 1**

```text
Input:  n = 3
Output: ["1","2","Fizz"]
```

**Example 2**

```text
Input:  n = 5
Output: ["1","2","Fizz","4","Buzz"]
```

**Constraints**

- `1 <= n <= 10^4`
