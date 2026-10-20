package com.himanshu.javalearn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompletionRequest(@NotBlank @Size(max = 40) String username) {
}
