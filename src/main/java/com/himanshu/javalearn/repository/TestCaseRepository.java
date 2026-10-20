package com.himanshu.javalearn.repository;

import com.himanshu.javalearn.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
}
