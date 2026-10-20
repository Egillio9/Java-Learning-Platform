package com.himanshu.javalearn;

import com.himanshu.javalearn.dto.ProgressReport;
import com.himanshu.javalearn.dto.QuizResult;
import com.himanshu.javalearn.dto.QuizSubmission;
import com.himanshu.javalearn.dto.TopicDetail;
import com.himanshu.javalearn.exception.InvalidRequestException;
import com.himanshu.javalearn.exception.NotFoundException;
import com.himanshu.javalearn.model.Question;
import com.himanshu.javalearn.repository.TopicRepository;
import com.himanshu.javalearn.service.CatalogService;
import com.himanshu.javalearn.service.ProgressService;
import com.himanshu.javalearn.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class LearningFlowTest {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private ProgressService progressService;

    @Autowired
    private TopicRepository topicRepository;

    @Test
    void seedsTheCurriculumInOrder() {
        assertThat(catalogService.listTopics())
                .extracting("slug")
                .containsExactly("java-basics", "oop", "strings", "exceptions", "collections", "streams");
    }

    @Test
    void quizCountsUnansweredQuestionsAsWrong() {
        List<Question> questions = topicRepository.findBySlug("java-basics").orElseThrow().getQuestions();
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(questions.get(0).getId(), questions.get(0).getCorrectOption());
        answers.put(questions.get(1).getId(), (questions.get(1).getCorrectOption() + 1) % 4);

        QuizResult result = quizService.submit("java-basics", new QuizSubmission("asha", answers));

        assertThat(result.score()).isEqualTo(1);
        assertThat(result.total()).isEqualTo(questions.size());
        assertThat(result.percentage()).isEqualTo(33);
        assertThat(result.feedback()).extracting("correct").containsExactly(true, false, false);
    }

    @Test
    void quizRejectsQuestionsFromAnotherTopic() {
        Long foreignId = topicRepository.findBySlug("streams").orElseThrow().getQuestions().get(0).getId();

        assertThatThrownBy(() -> quizService.submit("java-basics", new QuizSubmission("asha", Map.of(foreignId, 0))))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void progressTracksLessonsAndBestQuizScore() {
        TopicDetail oop = catalogService.getTopic("oop");
        Long firstLesson = oop.lessons().get(0).id();

        progressService.completeLesson(firstLesson, "ravi");
        ProgressReport afterRepeat = progressService.completeLesson(firstLesson, "Ravi");

        List<Question> questions = topicRepository.findBySlug("oop").orElseThrow().getQuestions();
        Map<Long, Integer> allCorrect = new HashMap<>();
        questions.forEach(q -> allCorrect.put(q.getId(), q.getCorrectOption()));
        quizService.submit("oop", new QuizSubmission("ravi", Map.of(questions.get(0).getId(), 3)));
        quizService.submit("oop", new QuizSubmission("ravi", allCorrect));

        ProgressReport report = progressService.report("ravi");
        ProgressReport.TopicProgress oopProgress = report.topics().stream()
                .filter(t -> t.slug().equals("oop")).findFirst().orElseThrow();

        assertThat(afterRepeat.lessonsCompleted()).isEqualTo(1);
        assertThat(oopProgress.lessonsCompleted()).isEqualTo(1);
        assertThat(oopProgress.completionPercentage()).isEqualTo(50);
        assertThat(oopProgress.quizAttempts()).isEqualTo(2);
        assertThat(oopProgress.bestQuizPercentage()).isEqualTo(100);
        assertThat(report.overallPercentage()).isEqualTo(8);
    }

    @Test
    void unknownTopicAndLearnerAreNotFound() {
        assertThatThrownBy(() -> catalogService.getTopic("kotlin")).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> progressService.report("nobody")).isInstanceOf(NotFoundException.class);
    }
}
