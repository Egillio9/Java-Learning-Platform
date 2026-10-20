package com.himanshu.javalearn.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "learner_id")
    private Learner learner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private int score;

    private int total;

    private Instant attemptedAt;

    protected QuizAttempt() {
    }

    public QuizAttempt(Learner learner, Topic topic, int score, int total) {
        this.learner = learner;
        this.topic = topic;
        this.score = score;
        this.total = total;
        this.attemptedAt = Instant.now();
    }

    public int percentage() {
        return total == 0 ? 0 : Math.round(score * 100f / total);
    }

    public Long getId() { return id; }
    public Learner getLearner() { return learner; }
    public Topic getTopic() { return topic; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public Instant getAttemptedAt() { return attemptedAt; }
}
