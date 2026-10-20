You have an unlimited supply of coins of each denomination in `coins`. Return the **fewest** coins needed to make exactly `amount`, or `-1` if it cannot be made.

**Example 1**

```text
Input:  coins = [1,2,5], amount = 11
Output: 3
```

**Example 2**

```text
Input:  coins = [2], amount = 3
Output: -1
```

**Example 3**

```text
Input:  coins = [1], amount = 0
Output: 0
```

**Constraints**

- `1 <= coins.length <= 12`
- `1 <= coins[i] <= 2^31 - 1`
- `0 <= amount <= 10^4`
