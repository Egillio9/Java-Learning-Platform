package com.himanshu.javalearn.dto;

import java.util.List;

/** A practice question as shown to the learner: the correct answer is never sent before submission. */
public record QuestionView(Long id, String prompt, List<String> options) {
}
