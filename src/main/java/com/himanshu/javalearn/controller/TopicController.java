package com.himanshu.javalearn.controller;

import com.himanshu.javalearn.dto.QuestionView;
import com.himanshu.javalearn.dto.QuizResult;
import com.himanshu.javalearn.dto.QuizSubmission;
import com.himanshu.javalearn.dto.TopicDetail;
import com.himanshu.javalearn.dto.TopicSummary;
import com.himanshu.javalearn.service.CatalogService;
import com.himanshu.javalearn.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final CatalogService catalogService;
    private final QuizService quizService;

    public TopicController(CatalogService catalogService, QuizService quizService) {
        this.catalogService = catalogService;
        this.quizService = quizService;
    }

    @GetMapping
    public List<TopicSummary> listTopics() {
        return catalogService.listTopics();
    }

    @GetMapping("/{slug}")
    public TopicDetail getTopic(@PathVariable String slug) {
        return catalogService.getTopic(slug);
    }

    @GetMapping("/{slug}/questions")
    public List<QuestionView> getQuestions(@PathVariable String slug) {
        return catalogService.getQuestions(slug);
    }

    @PostMapping("/{slug}/quiz")
    public QuizResult submitQuiz(@PathVariable String slug, @Valid @RequestBody QuizSubmission submission) {
        return quizService.submit(slug, submission);
    }
}
