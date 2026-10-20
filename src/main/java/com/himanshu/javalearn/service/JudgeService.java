package com.himanshu.javalearn.service;

import com.himanshu.javalearn.dto.JudgeDtos.CaseReport;
import com.himanshu.javalearn.dto.JudgeDtos.RunReport;
import com.himanshu.javalearn.dto.JudgeDtos.SubmissionReport;
import com.himanshu.javalearn.exception.InvalidRequestException;
import com.himanshu.javalearn.exception.NotFoundException;
import com.himanshu.javalearn.judge.CodeRunner;
import com.himanshu.javalearn.judge.Execution;
import com.himanshu.javalearn.judge.Execution.CaseOutcome;
import com.himanshu.javalearn.model.Problem;
import com.himanshu.javalearn.model.Submission;
import com.himanshu.javalearn.model.TestCase;
import com.himanshu.javalearn.model.Verdict;
import com.himanshu.javalearn.repository.LearnerRepository;
import com.himanshu.javalearn.repository.ProblemRepository;
import com.himanshu.javalearn.repository.SubmissionRepository;
import com.himanshu.javalearn.repository.TestCaseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns raw executions into LeetCode-style verdicts. Deliberately not @Transactional: a run can take
 * several seconds and should not hold a database transaction open while the child JVM works.
 */
@Service
public class JudgeService {

    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final SubmissionRepository submissionRepository;
    private final LearnerRepository learnerRepository;
    private final CodeRunner codeRunner;

    public JudgeService(ProblemRepository problemRepository, TestCaseRepository testCaseRepository,
                        SubmissionRepository submissionRepository, LearnerRepository learnerRepository,
                        CodeRunner codeRunner) {
        this.problemRepository = problemRepository;
        this.testCaseRepository = testCaseRepository;
        this.submissionRepository = submissionRepository;
        this.learnerRepository = learnerRepository;
        this.codeRunner = codeRunner;
    }

    /** "Run": sample cases, or the learner's own inputs checked against the reference solution. */
    public RunReport run(String slug, String code, List<String> customInputs) {
        Problem problem = load(slug);
        List<String> inputs = new ArrayList<>();
        List<String> expected = new ArrayList<>();

        List<String> custom = customInputs == null ? List.of()
                : customInputs.stream().filter(input -> input != null && !input.isBlank()).toList();
        if (!custom.isEmpty()) {
            Execution reference = codeRunner.execute(problem.getReferenceSolution(), problem.getDriverCode(), custom);
            for (int i = 0; i < custom.size(); i++) {
                CaseOutcome outcome = i < reference.cases().size() ? reference.cases().get(i) : null;
                if (outcome == null || outcome.threw()) {
                    throw new InvalidRequestException("Test case " + (i + 1) + " is not valid input for this problem"
                            + (outcome != null ? ": " + outcome.error() : ""));
                }
                inputs.add(custom.get(i));
                expected.add(outcome.output());
            }
        } else {
            ensureExpectedOutputs(problem);
            for (TestCase sample : problem.sampleCases()) {
                inputs.add(sample.getInput());
                expected.add(sample.getExpectedOutput());
            }
        }

        Execution execution = codeRunner.execute(code, problem.getDriverCode(), inputs);
        Grade grade = grade(execution, inputs, expected);
        List<CaseReport> cases = new ArrayList<>();
        for (int i = 0; i < execution.cases().size(); i++) {
            cases.add(report(i, inputs.get(i), expected.get(i), execution.cases().get(i)));
        }
        return new RunReport(grade.verdict(), grade.message(), grade.passed(), inputs.size(), grade.runtimeMs(), cases);
    }

    /** "Submit": every test case, hidden ones included; the attempt is recorded. */
    public SubmissionReport submit(String slug, Long learnerId, String code) {
        Problem problem = load(slug);
        ensureExpectedOutputs(problem);
        List<String> inputs = problem.getTestCases().stream().map(TestCase::getInput).toList();
        List<String> expected = problem.getTestCases().stream().map(TestCase::getExpectedOutput).toList();
        boolean solvedBefore = submissionRepository.existsByLearnerIdAndProblemSlugAndVerdict(
                learnerId, slug, Verdict.ACCEPTED);

        Execution execution = codeRunner.execute(code, problem.getDriverCode(), inputs);
        Grade grade = grade(execution, inputs, expected);

        Submission saved = submissionRepository.save(new Submission(learnerRepository.getReferenceById(learnerId),
                problem, code, grade.verdict(), grade.passed(), inputs.size(), grade.runtimeMs()));
        boolean accepted = grade.verdict() == Verdict.ACCEPTED;
        return new SubmissionReport(saved.getId(), grade.verdict(), grade.message(), grade.passed(), inputs.size(),
                grade.runtimeMs(), grade.failedCase(), accepted && !solvedBefore);
    }

    private Problem load(String slug) {
        return problemRepository.findWithTestCasesBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Problem not found: " + slug));
    }

    /** Hidden cases may ship without answers; the reference solution produces them once and they are stored. */
    void ensureExpectedOutputs(Problem problem) {
        List<TestCase> missing = problem.getTestCases().stream().filter(tc -> tc.getExpectedOutput() == null).toList();
        if (missing.isEmpty()) {
            return;
        }
        Execution reference = codeRunner.execute(problem.getReferenceSolution(), problem.getDriverCode(),
                missing.stream().map(TestCase::getInput).toList());
        if (reference.status() != Execution.Status.COMPLETED) {
            throw new IllegalStateException("Reference solution for " + problem.getSlug() + " failed: " + reference.message());
        }
        for (int i = 0; i < missing.size(); i++) {
            CaseOutcome outcome = reference.cases().get(i);
            if (outcome.threw()) {
                throw new IllegalStateException("Reference solution for " + problem.getSlug()
                        + " threw on test " + missing.get(i).getPosition() + ": " + outcome.error());
            }
            missing.get(i).setExpectedOutput(outcome.output());
        }
        testCaseRepository.saveAll(missing);
    }

    private record Grade(Verdict verdict, String message, int passed, Long runtimeMs, CaseReport failedCase) {
    }

    static Grade grade(Execution execution, List<String> inputs, List<String> expected) {
        if (execution.status() == Execution.Status.COMPILE_ERROR) {
            return new Grade(Verdict.COMPILE_ERROR, execution.message(), 0, null, null);
        }

        int passed = 0;
        long slowestMicros = 0;
        Verdict verdict = null;
        String message = null;
        CaseReport failed = null;
        List<CaseOutcome> cases = execution.cases();
        for (int i = 0; i < cases.size(); i++) {
            CaseOutcome outcome = cases.get(i);
            slowestMicros = Math.max(slowestMicros, outcome.timeMicros());
            boolean ok = !outcome.threw() && matches(outcome.output(), expected.get(i));
            if (ok) {
                passed++;
            } else if (verdict == null) {
                verdict = outcome.threw() ? Verdict.RUNTIME_ERROR : Verdict.WRONG_ANSWER;
                message = outcome.threw() ? outcome.error() : null;
                failed = report(i, inputs.get(i), expected.get(i), outcome);
            }
        }

        if (verdict == null && execution.status() != Execution.Status.COMPLETED) {
            int stuckAt = cases.size();
            verdict = execution.status() == Execution.Status.TIME_LIMIT_EXCEEDED
                    ? Verdict.TIME_LIMIT_EXCEEDED : Verdict.RUNTIME_ERROR;
            message = execution.message();
            if (stuckAt < inputs.size()) {
                failed = new CaseReport(stuckAt, inputs.get(stuckAt), expected.get(stuckAt), null, "", message, false, 0);
            }
        }
        long runtimeMs = Math.max(1, Math.round(slowestMicros / 1000.0));
        return new Grade(verdict == null ? Verdict.ACCEPTED : verdict, message, passed,
                cases.isEmpty() ? null : runtimeMs, failed);
    }

    private static CaseReport report(int index, String input, String expected, CaseOutcome outcome) {
        boolean passed = !outcome.threw() && matches(outcome.output(), expected);
        return new CaseReport(index, input, expected, outcome.output(), outcome.stdout(), outcome.error(), passed,
                outcome.timeMicros() / 1000.0);
    }

    /** Whitespace outside string literals is not significant, so "[1, 2]" equals "[1,2]". */
    static boolean matches(String actual, String expected) {
        return actual != null && expected != null && normalize(actual).equals(normalize(expected));
    }

    private static String normalize(String value) {
        String trimmed = value.trim();
        return trimmed.indexOf('"') >= 0 ? trimmed : trimmed.replaceAll("\\s+", "");
    }
}
