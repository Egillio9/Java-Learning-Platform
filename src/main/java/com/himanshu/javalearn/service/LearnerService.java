package com.himanshu.javalearn.service;

import com.himanshu.javalearn.exception.InvalidRequestException;
import com.himanshu.javalearn.exception.NotFoundException;
import com.himanshu.javalearn.model.AuthProvider;
import com.himanshu.javalearn.model.Learner;
import com.himanshu.javalearn.repository.LearnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class LearnerService {

    private final LearnerRepository learnerRepository;

    public LearnerService(LearnerRepository learnerRepository) {
        this.learnerRepository = learnerRepository;
    }

    /** Guests are keyed by name, so signing in again with the same name resumes the same progress. */
    @Transactional
    public Learner signInAsGuest(String name) {
        String displayName = name.trim().replaceAll("\\s+", " ");
        String username = "guest:" + displayName.toLowerCase(Locale.ROOT);
        Learner learner = learnerRepository.findByUsername(username)
                .orElseGet(() -> learnerRepository.save(new Learner(username, displayName, AuthProvider.GUEST, null)));
        learner.touch();
        return learner;
    }

    @Transactional
    public Learner signInWithGoogle(String email, String name, String pictureUrl) {
        if (email == null || email.isBlank()) {
            throw new InvalidRequestException("Google account has no email address");
        }
        String username = "google:" + email.toLowerCase(Locale.ROOT);
        String displayName = name == null || name.isBlank() ? email : name;
        Learner learner = learnerRepository.findByUsername(username)
                .orElseGet(() -> learnerRepository.save(new Learner(username, displayName, AuthProvider.GOOGLE, pictureUrl)));
        learner.refreshProfile(displayName, pictureUrl);
        return learner;
    }

    @Transactional(readOnly = true)
    public Learner get(Long id) {
        return learnerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Learner not found: " + id));
    }
}
