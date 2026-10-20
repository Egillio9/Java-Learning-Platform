package com.himanshu.javalearn.dto;

import java.util.List;

public record ProgressReport(String username, int lessonsCompleted, int totalLessons,
                             int overallPercentage, List<Long> completedLessonIds,
                             List<TopicProgress> topics) {

    public record TopicProgress(String slug, String title, int lessonsCompleted, int totalLessons,
                                int completionPercentage, Integer bestQuizPercentage, int quizAttempts) {
    }
}
