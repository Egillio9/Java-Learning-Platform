package com.himanshu.javalearn.repository;

import com.himanshu.javalearn.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    long countByTopicId(Long topicId);
}
