package com.himanshu.javalearn.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "lesson_completions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"learner_id", "lesson_id"}))
public class LessonCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "learner_id")
    private Learner learner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    private Instant completedAt;

    protected LessonCompletion() {
    }

    public LessonCompletion(Learner learner, Lesson lesson) {
        this.learner = learner;
        this.lesson = lesson;
        this.completedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Learner getLearner() { return learner; }
    public Lesson getLesson() { return lesson; }
    public Instant getCompletedAt() { return completedAt; }
}
