package com.himanshu.javalearn.service;

import com.himanshu.javalearn.exception.NotFoundException;
import com.himanshu.javalearn.model.Learner;
import com.himanshu.javalearn.repository.LearnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LearnerService {

    private final LearnerRepository learnerRepository;

    public LearnerService(LearnerRepository learnerRepository) {
        this.learnerRepository = learnerRepository;
    }

    /** Learners are identified by username only; the first action under a new name registers it. */
    @Transactional
    public Learner getOrCreate(String username) {
        String normalized = username.trim();
        return learnerRepository.findByUsernameIgnoreCase(normalized)
                .orElseGet(() -> learnerRepository.save(new Learner(normalized)));
    }

    @Transactional(readOnly = true)
    public Learner get(String username) {
        return learnerRepository.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new NotFoundException("Learner not found: " + username));
    }
}
