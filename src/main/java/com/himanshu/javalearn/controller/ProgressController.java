package com.himanshu.javalearn.controller;

import com.himanshu.javalearn.auth.AuthUser;
import com.himanshu.javalearn.dto.ProgressReport;
import com.himanshu.javalearn.service.ProgressService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public ProgressReport completeLesson(@PathVariable Long lessonId, @AuthenticationPrincipal AuthUser user) {
        return progressService.completeLesson(lessonId, user.id());
    }

    @GetMapping("/me/progress")
    public ProgressReport myProgress(@AuthenticationPrincipal AuthUser user) {
        return progressService.report(user.id());
    }
}
