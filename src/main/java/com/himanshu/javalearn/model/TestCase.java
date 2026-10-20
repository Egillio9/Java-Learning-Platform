package com.himanshu.javalearn.model;

import jakarta.persistence.*;

@Entity
@Table(name = "test_cases")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private int position;

    /** One argument per line, in LeetCode notation: [2,7,11,15] / 9 / "abc". */
    @Column(length = 20_000, nullable = false)
    private String input;

    /** Filled from the reference solution the first time it is needed when the problem file omits it. */
    @Column(length = 20_000)
    private String expectedOutput;

    private boolean sample;

    protected TestCase() {
    }

    public TestCase(int position, String input, String expectedOutput, boolean sample) {
        this.position = position;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.sample = sample;
    }

    void setProblem(Problem problem) { this.problem = problem; }

    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }

    public Long getId() { return id; }
    public Problem getProblem() { return problem; }
    public int getPosition() { return position; }
    public String getInput() { return input; }
    public String getExpectedOutput() { return expectedOutput; }
    public boolean isSample() { return sample; }
}
