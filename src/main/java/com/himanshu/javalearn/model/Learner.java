package com.himanshu.javalearn.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "learners")
public class Learner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stable login key: "guest:{name}" or "google:{email}". */
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Column(length = 500)
    private String avatarUrl;

    private Instant joinedAt;

    private Instant lastSeenAt;

    protected Learner() {
    }

    public Learner(String username, String displayName, AuthProvider provider, String avatarUrl) {
        this.username = username;
        this.displayName = displayName;
        this.provider = provider;
        this.avatarUrl = avatarUrl;
        this.joinedAt = Instant.now();
        this.lastSeenAt = joinedAt;
    }

    public void refreshProfile(String displayName, String avatarUrl) {
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.lastSeenAt = Instant.now();
    }

    public void touch() {
        this.lastSeenAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public AuthProvider getProvider() { return provider; }
    public String getAvatarUrl() { return avatarUrl; }
    public Instant getJoinedAt() { return joinedAt; }
    public Instant getLastSeenAt() { return lastSeenAt; }
}
