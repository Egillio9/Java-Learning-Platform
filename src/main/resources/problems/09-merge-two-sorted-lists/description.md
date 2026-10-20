You are given the heads of two linked lists, each sorted in non-decreasing order.

Splice their nodes together into **one** sorted list and return its head.

**Example 1**

```text
Input:  list1 = [1,2,4], list2 = [1,3,4]
Output: [1,1,2,3,4,4]
```

**Example 2**

```text
Input:  list1 = [], list2 = []
Output: []
```

**Example 3**

```text
Input:  list1 = [], list2 = [0]
Output: [0]
```

**Constraints**

- Each list has between `0` and `50` nodes.
- `-100 <= Node.val <= 100`
