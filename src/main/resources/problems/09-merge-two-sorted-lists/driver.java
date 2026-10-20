import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            ListNode a = in.linkedList();
            ListNode b = in.linkedList();
            return JudgeRuntime.format(new Solution().mergeTwoLists(a, b));
        });
    }
}
