package com.himanshu.javalearn.service;

import com.himanshu.javalearn.dto.LessonView;
import com.himanshu.javalearn.dto.QuestionView;
import com.himanshu.javalearn.dto.TopicDetail;
import com.himanshu.javalearn.dto.TopicSummary;
import com.himanshu.javalearn.exception.NotFoundException;
import com.himanshu.javalearn.model.Topic;
import com.himanshu.javalearn.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogService {

    private final TopicRepository topicRepository;

    public CatalogService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<TopicSummary> listTopics() {
        return topicRepository.findAllByOrderByPositionAsc().stream()
                .map(topic -> new TopicSummary(topic.getSlug(), topic.getTitle(), topic.getLevel(),
                        topic.getSummary(), topic.getLessons().size(), topic.getQuestions().size()))
                .toList();
    }

    public TopicDetail getTopic(String slug) {
        Topic topic = findTopic(slug);
        List<LessonView> lessons = topic.getLessons().stream()
                .map(lesson -> new LessonView(lesson.getId(), lesson.getTitle(),
                        lesson.getContent(), lesson.getCodeExample()))
                .toList();
        return new TopicDetail(topic.getSlug(), topic.getTitle(), topic.getLevel(), topic.getSummary(), lessons);
    }

    public List<QuestionView> getQuestions(String slug) {
        return findTopic(slug).getQuestions().stream()
                .map(question -> new QuestionView(question.getId(), question.getPrompt(), question.getOptions()))
                .toList();
    }

    Topic findTopic(String slug) {
        return topicRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Topic not found: " + slug));
    }
}
