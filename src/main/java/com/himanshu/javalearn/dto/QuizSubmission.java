package com.himanshu.javalearn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Map;

/** answers maps question id to the index of the chosen option. */
public record QuizSubmission(
        @NotBlank @Size(max = 40) String username,
        @NotEmpty Map<Long, Integer> answers) {
}
