package com.himanshu.javalearn.dto;

import com.himanshu.javalearn.model.Level;

import java.util.List;

public record TopicDetail(String slug, String title, Level level, String summary, List<LessonView> lessons) {
}
