package com.himanshu.javalearn.judge;

public class JudgeBusyException extends RuntimeException {

    public JudgeBusyException() {
        super("The judge is busy with other submissions. Please try again in a few seconds.");
    }
}
