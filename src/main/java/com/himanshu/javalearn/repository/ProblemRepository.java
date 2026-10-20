package com.himanshu.javalearn.repository;

import com.himanshu.javalearn.model.Problem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Optional<Problem> findBySlug(String slug);

    /** Loads the test cases eagerly so judging can run outside a transaction. */
    @EntityGraph(attributePaths = "testCases")
    Optional<Problem> findWithTestCasesBySlug(String slug);

    List<Problem> findAllByOrderByPositionAsc();
}
