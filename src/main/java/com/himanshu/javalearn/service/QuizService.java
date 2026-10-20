package com.himanshu.javalearn.service;

import com.himanshu.javalearn.dto.QuizResult;
import com.himanshu.javalearn.dto.QuizResult.AnswerFeedback;
import com.himanshu.javalearn.dto.QuizSubmission;
import com.himanshu.javalearn.exception.InvalidRequestException;
import com.himanshu.javalearn.model.Learner;
import com.himanshu.javalearn.model.Question;
import com.himanshu.javalearn.model.QuizAttempt;
import com.himanshu.javalearn.model.Topic;
import com.himanshu.javalearn.repository.QuizAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final CatalogService catalogService;
    private final LearnerService learnerService;
    private final QuizAttemptRepository attemptRepository;

    public QuizService(CatalogService catalogService, LearnerService learnerService,
                       QuizAttemptRepository attemptRepository) {
        this.catalogService = catalogService;
        this.learnerService = learnerService;
        this.attemptRepository = attemptRepository;
    }

    @Transactional
    public QuizResult submit(String slug, QuizSubmission submission) {
        Topic topic = catalogService.findTopic(slug);
        List<Question> questions = topic.getQuestions();
        Map<Long, Integer> answers = submission.answers();

        Set<Long> topicQuestionIds = questions.stream().map(Question::getId).collect(Collectors.toSet());
        List<Long> foreignIds = answers.keySet().stream().filter(id -> !topicQuestionIds.contains(id)).toList();
        if (!foreignIds.isEmpty()) {
            throw new InvalidRequestException("Questions " + foreignIds + " do not belong to topic " + slug);
        }

        // Unanswered questions count as wrong, so the score is always out of the full quiz.
        List<AnswerFeedback> feedback = questions.stream()
                .map(question -> {
                    Integer chosen = answers.get(question.getId());
                    return new AnswerFeedback(question.getId(), chosen, question.getCorrectOption(),
                            question.isCorrect(chosen), question.getExplanation());
                })
                .toList();
        int score = (int) feedback.stream().filter(AnswerFeedback::correct).count();

        Learner learner = learnerService.getOrCreate(submission.username());
        QuizAttempt attempt = attemptRepository.save(new QuizAttempt(learner, topic, score, questions.size()));

        return new QuizResult(topic.getSlug(), score, questions.size(), attempt.percentage(), feedback);
    }
}
