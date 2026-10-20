package com.himanshu.javalearn.service;

import com.himanshu.javalearn.dto.ProgressReport;
import com.himanshu.javalearn.dto.ProgressReport.TopicProgress;
import com.himanshu.javalearn.exception.NotFoundException;
import com.himanshu.javalearn.model.Learner;
import com.himanshu.javalearn.model.Lesson;
import com.himanshu.javalearn.model.LessonCompletion;
import com.himanshu.javalearn.model.QuizAttempt;
import com.himanshu.javalearn.model.Topic;
import com.himanshu.javalearn.repository.LessonCompletionRepository;
import com.himanshu.javalearn.repository.LessonRepository;
import com.himanshu.javalearn.repository.QuizAttemptRepository;
import com.himanshu.javalearn.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final LearnerService learnerService;
    private final LessonRepository lessonRepository;
    private final LessonCompletionRepository completionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final TopicRepository topicRepository;

    public ProgressService(LearnerService learnerService, LessonRepository lessonRepository,
                           LessonCompletionRepository completionRepository,
                           QuizAttemptRepository attemptRepository, TopicRepository topicRepository) {
        this.learnerService = learnerService;
        this.lessonRepository = lessonRepository;
        this.completionRepository = completionRepository;
        this.attemptRepository = attemptRepository;
        this.topicRepository = topicRepository;
    }

    /** Marking a lesson complete twice is a no-op, so the client can safely retry. */
    @Transactional
    public ProgressReport completeLesson(Long lessonId, String username) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found: " + lessonId));
        Learner learner = learnerService.getOrCreate(username);
        if (!completionRepository.existsByLearnerIdAndLessonId(learner.getId(), lesson.getId())) {
            completionRepository.save(new LessonCompletion(learner, lesson));
        }
        return buildReport(learner);
    }

    @Transactional(readOnly = true)
    public ProgressReport report(String username) {
        return buildReport(learnerService.get(username));
    }

    private ProgressReport buildReport(Learner learner) {
        Set<Long> completed = new HashSet<>(completionRepository.findCompletedLessonIds(learner.getId()));
        Map<Long, List<QuizAttempt>> attemptsByTopic = attemptRepository
                .findByLearnerIdOrderByAttemptedAtDesc(learner.getId()).stream()
                .collect(Collectors.groupingBy(attempt -> attempt.getTopic().getId()));

        int totalLessons = 0;
        int totalCompleted = 0;
        List<TopicProgress> topics = new ArrayList<>();
        for (Topic topic : topicRepository.findAllByOrderByPositionAsc()) {
            int lessonCount = topic.getLessons().size();
            int done = (int) topic.getLessons().stream().filter(l -> completed.contains(l.getId())).count();
            List<QuizAttempt> attempts = attemptsByTopic.getOrDefault(topic.getId(), List.of());
            Integer best = attempts.stream().map(QuizAttempt::percentage).max(Integer::compare).orElse(null);

            topics.add(new TopicProgress(topic.getSlug(), topic.getTitle(), done, lessonCount,
                    percent(done, lessonCount), best, attempts.size()));
            totalLessons += lessonCount;
            totalCompleted += done;
        }

        return new ProgressReport(learner.getUsername(), totalCompleted, totalLessons,
                percent(totalCompleted, totalLessons), completed.stream().sorted().toList(), topics);
    }

    private static int percent(int part, int whole) {
        return whole == 0 ? 0 : Math.round(part * 100f / whole);
    }
}
