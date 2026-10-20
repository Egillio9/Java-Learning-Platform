package com.himanshu.javalearn.controller;

import com.himanshu.javalearn.auth.AuthUser;
import com.himanshu.javalearn.dto.JudgeDtos.CodeRequest;
import com.himanshu.javalearn.dto.JudgeDtos.ProblemDetail;
import com.himanshu.javalearn.dto.JudgeDtos.ProblemSummary;
import com.himanshu.javalearn.dto.JudgeDtos.RunReport;
import com.himanshu.javalearn.dto.JudgeDtos.SolutionView;
import com.himanshu.javalearn.dto.JudgeDtos.SubmissionReport;
import com.himanshu.javalearn.dto.JudgeDtos.SubmissionView;
import com.himanshu.javalearn.service.JudgeService;
import com.himanshu.javalearn.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final JudgeService judgeService;

    public ProblemController(ProblemService problemService, JudgeService judgeService) {
        this.problemService = problemService;
        this.judgeService = judgeService;
    }

    @GetMapping
    public List<ProblemSummary> list(@AuthenticationPrincipal AuthUser user) {
        return problemService.list(user == null ? null : user.id());
    }

    @GetMapping("/{slug}")
    public ProblemDetail detail(@PathVariable String slug, @AuthenticationPrincipal AuthUser user) {
        return problemService.detail(slug, user == null ? null : user.id());
    }

    @PostMapping("/{slug}/run")
    public RunReport run(@PathVariable String slug, @Valid @RequestBody CodeRequest request) {
        return judgeService.run(slug, request.code(), request.customInputs());
    }

    @PostMapping("/{slug}/submit")
    public SubmissionReport submit(@PathVariable String slug, @AuthenticationPrincipal AuthUser user,
                                   @Valid @RequestBody CodeRequest request) {
        return judgeService.submit(slug, user.id(), request.code());
    }

    @GetMapping("/{slug}/submissions")
    public List<SubmissionView> submissions(@PathVariable String slug, @AuthenticationPrincipal AuthUser user) {
        return problemService.submissions(slug, user.id());
    }

    @GetMapping("/{slug}/solution")
    public SolutionView solution(@PathVariable String slug, @AuthenticationPrincipal AuthUser user) {
        return problemService.solution(slug, user.id());
    }
}
