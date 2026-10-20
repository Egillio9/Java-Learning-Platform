package com.himanshu.javalearn.repository;

import com.himanshu.javalearn.model.LessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {

    boolean existsByLearnerIdAndLessonId(Long learnerId, Long lessonId);

    @Query("select c.lesson.id from LessonCompletion c where c.learner.id = :learnerId")
    List<Long> findCompletedLessonIds(Long learnerId);

    /** Rows of [learnerId, lessonsCompleted]. */
    @Query("select c.learner.id, count(c) from LessonCompletion c group by c.learner.id")
    List<Object[]> completedCountByLearner();

    @Query("select c.completedAt from LessonCompletion c where c.learner.id = :learnerId")
    List<Instant> findActivityTimes(Long learnerId);
}
