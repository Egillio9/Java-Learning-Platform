package com.himanshu.javalearn.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

/** answers maps question id to the index of the chosen option. */
public record QuizSubmission(@NotEmpty Map<Long, Integer> answers) {
}
