package com.himanshu.javalearn.dto;

import com.himanshu.javalearn.model.Difficulty;
import com.himanshu.javalearn.model.Verdict;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

/** Request and response shapes for the coding judge. */
public final class JudgeDtos {

    private JudgeDtos() {
    }

    public enum ProblemStatus { SOLVED, ATTEMPTED, TODO }

    public record ProblemSummary(String slug, String title, Difficulty difficulty, List<String> tags,
                                 int acceptanceRate, long submissions, ProblemStatus status) {
    }

    public record SampleCase(String input, String expected) {
    }

    public record ProblemDetail(String slug, String title, Difficulty difficulty, List<String> params, List<String> tags,
                                String description, String starterCode, List<SampleCase> samples,
                                int acceptanceRate, long submissions, ProblemStatus status,
                                String previousSlug, String nextSlug) {
    }

    public record CodeRequest(@NotBlank @Size(max = 20_000) String code,
                              @Size(max = 5) List<@Size(max = 2_000) String> customInputs) {
    }

    /** One executed case as shown in the console. expected is null only if the reference failed. */
    public record CaseReport(int index, String input, String expected, String output, String stdout,
                             String error, boolean passed, double timeMs) {
    }

    public record RunReport(Verdict verdict, String message, int passed, int total, Long runtimeMs,
                            List<CaseReport> cases) {
    }

    /** failedCase is the first failing case (LeetCode shows it even when hidden). */
    public record SubmissionReport(Long submissionId, Verdict verdict, String message, int passed, int total,
                                   Long runtimeMs, CaseReport failedCase, boolean firstSolve) {
    }

    public record SubmissionView(Long id, Verdict verdict, int passed, int total, Long runtimeMs,
                                 Instant createdAt, String code) {
    }

    public record SolutionView(String slug, String code) {
    }
}
