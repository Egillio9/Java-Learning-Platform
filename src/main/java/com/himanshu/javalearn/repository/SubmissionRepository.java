package com.himanshu.javalearn.repository;

import com.himanshu.javalearn.model.Submission;
import com.himanshu.javalearn.model.Verdict;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @EntityGraph(attributePaths = "problem")
    List<Submission> findTop8ByLearnerIdOrderByCreatedAtDesc(Long learnerId);

    List<Submission> findByLearnerIdAndProblemSlugOrderByCreatedAtDesc(Long learnerId, String slug);

    long countByLearnerId(Long learnerId);

    long countByLearnerIdAndVerdict(Long learnerId, Verdict verdict);

    boolean existsByLearnerIdAndProblemSlugAndVerdict(Long learnerId, String slug, Verdict verdict);

    @Query("select distinct s.problem.id from Submission s where s.learner.id = :learnerId and s.verdict = :verdict")
    List<Long> findProblemIdsByLearnerAndVerdict(Long learnerId, Verdict verdict);

    @Query("select distinct s.problem.id from Submission s where s.learner.id = :learnerId")
    List<Long> findAttemptedProblemIds(Long learnerId);

    /** Rows of [problemId, totalSubmissions, acceptedSubmissions]. */
    @Query("""
            select s.problem.id, count(s), sum(case when s.verdict = :accepted then 1 else 0 end)
            from Submission s group by s.problem.id
            """)
    List<Object[]> acceptanceByProblem(Verdict accepted);

    /** Rows of [learnerId, distinctProblemsSolved]. */
    @Query("""
            select s.learner.id, count(distinct s.problem.id)
            from Submission s where s.verdict = :accepted group by s.learner.id
            """)
    List<Object[]> solvedCountByLearner(Verdict accepted);

    @Query("select s.createdAt from Submission s where s.learner.id = :learnerId")
    List<Instant> findActivityTimes(Long learnerId);
}
