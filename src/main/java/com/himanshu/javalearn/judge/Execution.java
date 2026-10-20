package com.himanshu.javalearn.judge;

import java.util.List;

/** Raw outcome of compiling and running one batch of inputs. Verdicts are decided by JudgeService. */
public record Execution(Status status, String message, List<CaseOutcome> cases) {

    public enum Status {
        COMPLETED,
        COMPILE_ERROR,
        TIME_LIMIT_EXCEEDED,
        CRASHED
    }

    /** output is the formatted return value; error is set instead when the case threw. */
    public record CaseOutcome(String output, String stdout, String error, long timeMicros) {

        public boolean threw() {
            return error != null;
        }
    }

    static Execution compileError(String message) {
        return new Execution(Status.COMPILE_ERROR, message, List.of());
    }

    static Execution timeLimit(List<CaseOutcome> finished) {
        return new Execution(Status.TIME_LIMIT_EXCEEDED, "Time limit exceeded", finished);
    }

    static Execution crashed(String message, List<CaseOutcome> finished) {
        return new Execution(Status.CRASHED, message, finished);
    }
}
