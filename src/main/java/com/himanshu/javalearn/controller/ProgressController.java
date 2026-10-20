package com.himanshu.javalearn.controller;

import com.himanshu.javalearn.dto.CompletionRequest;
import com.himanshu.javalearn.dto.ProgressReport;
import com.himanshu.javalearn.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public ProgressReport completeLesson(@PathVariable Long lessonId, @Valid @RequestBody CompletionRequest request) {
        return progressService.completeLesson(lessonId, request.username());
    }

    @GetMapping("/learners/{username}/progress")
    public ProgressReport getProgress(@PathVariable String username) {
        return progressService.report(username);
    }
}
