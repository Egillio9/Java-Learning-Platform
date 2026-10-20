package com.himanshu.javalearn.repository;

import com.himanshu.javalearn.model.Learner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LearnerRepository extends JpaRepository<Learner, Long> {

    Optional<Learner> findByUsername(String username);
}
