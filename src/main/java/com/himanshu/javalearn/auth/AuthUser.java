package com.himanshu.javalearn.auth;

import com.himanshu.javalearn.model.AuthProvider;
import com.himanshu.javalearn.model.Learner;

import java.io.Serializable;

/** The signed-in learner as stored in the HTTP session and injected with @AuthenticationPrincipal. */
public record AuthUser(Long id, String displayName, AuthProvider provider, String avatarUrl) implements Serializable {

    public static AuthUser of(Learner learner) {
        return new AuthUser(learner.getId(), learner.getDisplayName(), learner.getProvider(), learner.getAvatarUrl());
    }
}
