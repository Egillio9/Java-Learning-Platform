package com.himanshu.javalearn.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "learners")
public class Learner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private Instant joinedAt;

    protected Learner() {
    }

    public Learner(String username) {
        this.username = username;
        this.joinedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public Instant getJoinedAt() { return joinedAt; }
}
