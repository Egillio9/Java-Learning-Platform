package com.himanshu.javalearn.dto;

import java.util.List;

public record QuizResult(String topic, int score, int total, int percentage, List<AnswerFeedback> feedback) {

    public record AnswerFeedback(Long questionId, Integer chosenOption, int correctOption,
                                 boolean correct, String explanation) {
    }
}
