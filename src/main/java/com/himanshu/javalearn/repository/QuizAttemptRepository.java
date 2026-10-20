package com.himanshu.javalearn.repository;

import com.himanshu.javalearn.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByLearnerIdOrderByAttemptedAtDesc(Long learnerId);
}
