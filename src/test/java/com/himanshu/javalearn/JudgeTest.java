package com.himanshu.javalearn;

import com.himanshu.javalearn.dto.JudgeDtos.RunReport;
import com.himanshu.javalearn.dto.JudgeDtos.SubmissionReport;
import com.himanshu.javalearn.exception.InvalidRequestException;
import com.himanshu.javalearn.model.Problem;
import com.himanshu.javalearn.model.TestCase;
import com.himanshu.javalearn.model.Verdict;
import com.himanshu.javalearn.repository.ProblemRepository;
import com.himanshu.javalearn.service.JudgeService;
import com.himanshu.javalearn.service.LearnerService;
import com.himanshu.javalearn.service.ProblemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Spawns real child JVMs, so these are the slowest tests in the suite. */
@SpringBootTest
class JudgeTest {

    private static final String TWO_SUM_BRUTE_FORCE = """
            import java.util.*;

            class Solution {
                public int[] twoSum(int[] nums, int target) {
                    System.out.println("checking " + nums.length + " numbers");
                    for (int i = 0; i < nums.length; i++)
                        for (int j = i + 1; j < nums.length; j++)
                            if (nums[i] + nums[j] == target) return new int[] {j, i};
                    return new int[0];
                }
            }
            """;

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private LearnerService learnerService;

    @Test
    void everyReferenceSolutionPassesItsOwnExamplesAndHiddenTests() {
        Long tester = learnerService.signInAsGuest("Reference Bot").getId();
        List<Problem> problems = problemRepository.findAllByOrderByPositionAsc();
        assertThat(problems).hasSizeGreaterThanOrEqualTo(20);

        for (Problem problem : problems) {
            RunReport samples = judgeService.run(problem.getSlug(), problem.getReferenceSolution(), null);
            assertThat(samples.verdict()).as("samples of %s", problem.getSlug()).isEqualTo(Verdict.ACCEPTED);

            SubmissionReport submission = judgeService.submit(problem.getSlug(), tester, problem.getReferenceSolution());
            assertThat(submission.verdict()).as("submission of %s", problem.getSlug()).isEqualTo(Verdict.ACCEPTED);
            assertThat(submission.passed()).isEqualTo(submission.total());
        }

        Problem twoSum = problemRepository.findWithTestCasesBySlug("two-sum").orElseThrow();
        assertThat(twoSum.getTestCases()).extracting(TestCase::getExpectedOutput).doesNotContainNull();
    }

    @Test
    void acceptsAnyAnswerOrderAndCapturesStdout() {
        RunReport report = judgeService.run("two-sum", TWO_SUM_BRUTE_FORCE, null);

        assertThat(report.verdict()).isEqualTo(Verdict.ACCEPTED);
        assertThat(report.cases()).hasSize(3);
        assertThat(report.cases().get(0).stdout()).contains("checking 4 numbers");
    }

    @Test
    void wrongAnswerReportsTheFailingCase() {
        Long learner = learnerService.signInAsGuest("Wrong Answer").getId();
        String wrong = """
                class Solution {
                    public int climbStairs(int n) { return n; }
                }
                """;

        SubmissionReport report = judgeService.submit("climbing-stairs", learner, wrong);

        assertThat(report.verdict()).isEqualTo(Verdict.WRONG_ANSWER);
        // n = 2, 3 and 1 happen to pass; n = 5 is the first real failure (8 ways, not 5).
        assertThat(report.failedCase().input()).isEqualTo("5");
        assertThat(report.failedCase().expected()).isEqualTo("8");
        assertThat(report.failedCase().output()).isEqualTo("5");
        assertThat(report.passed()).isLessThan(report.total());
        assertThat(problemService.submissions("climbing-stairs", learner)).hasSize(1);
    }

    @Test
    void compileErrorsPointAtTheLine() {
        String broken = """
                class Solution {
                    public int climbStairs(int n) {
                        return n
                    }
                }
                """;

        RunReport report = judgeService.run("climbing-stairs", broken, null);

        assertThat(report.verdict()).isEqualTo(Verdict.COMPILE_ERROR);
        assertThat(report.message()).contains("Line 3");
    }

    @Test
    void runtimeErrorsAndTimeouts() {
        String throwing = """
                class Solution {
                    public int climbStairs(int n) { int[] a = new int[1]; return a[n]; }
                }
                """;
        String looping = """
                class Solution {
                    public int climbStairs(int n) { while (true) { n++; } }
                }
                """;

        RunReport crashed = judgeService.run("climbing-stairs", throwing, null);
        assertThat(crashed.verdict()).isEqualTo(Verdict.RUNTIME_ERROR);
        assertThat(crashed.message()).contains("ArrayIndexOutOfBoundsException").contains("line 2");

        assertThat(judgeService.run("climbing-stairs", looping, null).verdict())
                .isEqualTo(Verdict.TIME_LIMIT_EXCEEDED);
    }

    @Test
    void blockedApisNeverRun() {
        String escape = """
                class Solution {
                    public int climbStairs(int n) {
                        try { Runtime.getRuntime().exec("rm -rf /"); } catch (Exception e) { }
                        return 0;
                    }
                }
                """;

        RunReport report = judgeService.run("climbing-stairs", escape, null);

        assertThat(report.verdict()).isEqualTo(Verdict.COMPILE_ERROR);
        assertThat(report.message()).contains("not allowed");
    }

    @Test
    void customInputsAreCheckedAgainstTheReference() {
        String reference = problemRepository.findBySlug("climbing-stairs").orElseThrow().getReferenceSolution();

        RunReport report = judgeService.run("climbing-stairs", reference, List.of("6", "7"));

        assertThat(report.cases()).extracting("expected").containsExactly("13", "21");
        assertThatThrownBy(() -> judgeService.run("climbing-stairs", reference, List.of("not a number")))
                .isInstanceOf(InvalidRequestException.class);
    }
}
