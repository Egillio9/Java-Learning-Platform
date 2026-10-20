package com.himanshu.javalearn.dto;

import com.himanshu.javalearn.model.Level;

public record TopicSummary(String slug, String title, Level level, String summary,
                           int lessonCount, int questionCount) {
}
