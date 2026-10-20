package com.himanshu.javalearn.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "submissions", indexes = {
        @Index(name = "idx_submission_learner", columnList = "learner_id"),
        @Index(name = "idx_submission_problem", columnList = "problem_id")
})
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "learner_id")
    private Learner learner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(length = 20_000, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Verdict verdict;

    private int passed;

    private int total;

    /** Slowest test case in milliseconds; null when the code never ran. */
    private Long runtimeMs;

    private Instant createdAt;

    protected Submission() {
    }

    public Submission(Learner learner, Problem problem, String code, Verdict verdict,
                      int passed, int total, Long runtimeMs) {
        this.learner = learner;
        this.problem = problem;
        this.code = code;
        this.verdict = verdict;
        this.passed = passed;
        this.total = total;
        this.runtimeMs = runtimeMs;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Learner getLearner() { return learner; }
    public Problem getProblem() { return problem; }
    public String getCode() { return code; }
    public Verdict getVerdict() { return verdict; }
    public int getPassed() { return passed; }
    public int getTotal() { return total; }
    public Long getRuntimeMs() { return runtimeMs; }
    public Instant getCreatedAt() { return createdAt; }
}
