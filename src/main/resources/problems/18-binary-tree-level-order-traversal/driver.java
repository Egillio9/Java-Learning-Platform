import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JudgeRuntime.run(in -> {
            return JudgeRuntime.formatNested(new Solution().levelOrder(in.tree()));
        });
    }
}
