package com.himanshu.javalearn.repository;

import com.himanshu.javalearn.model.LessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {

    boolean existsByLearnerIdAndLessonId(Long learnerId, Long lessonId);

    @Query("select c.lesson.id from LessonCompletion c where c.learner.id = :learnerId")
    List<Long> findCompletedLessonIds(Long learnerId);
}
