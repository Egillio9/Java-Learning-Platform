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
import com.himanshu.javalearn.service.LearnerService;
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
    private LearnerService learnerService;

    @Autowired
    private TopicRepository topicRepository;

    @Test
    void seedsTheCurriculumInOrder() {
        assertThat(catalogService.listTopics())
                .extracting("slug")
                .hasSize(13)
                .startsWith("java-basics", "arrays", "methods-and-recursion", "oop")
                .endsWith("multithreading", "modern-java");
    }

    @Test
    void quizCountsUnansweredQuestionsAsWrong() {
        Long asha = learnerService.signInAsGuest("Asha").getId();
        List<Question> questions = topicRepository.findBySlug("java-basics").orElseThrow().getQuestions();
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(questions.get(0).getId(), questions.get(0).getCorrectOption());
        answers.put(questions.get(1).getId(), (questions.get(1).getCorrectOption() + 1) % 4);

        QuizResult result = quizService.submit("java-basics", asha, new QuizSubmission(answers));

        assertThat(result.score()).isEqualTo(1);
        assertThat(result.total()).isEqualTo(questions.size());
        assertThat(result.percentage()).isEqualTo(33);
        assertThat(result.feedback()).extracting("correct").containsExactly(true, false, false);
    }

    @Test
    void quizRejectsQuestionsFromAnotherTopic() {
        Long asha = learnerService.signInAsGuest("Asha").getId();
        Long foreignId = topicRepository.findBySlug("streams").orElseThrow().getQuestions().get(0).getId();

        assertThatThrownBy(() -> quizService.submit("java-basics", asha, new QuizSubmission(Map.of(foreignId, 0))))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void progressTracksLessonsAndBestQuizScore() {
        Long ravi = learnerService.signInAsGuest("Ravi").getId();
        TopicDetail oop = catalogService.getTopic("oop");
        Long firstLesson = oop.lessons().get(0).id();

        progressService.completeLesson(firstLesson, ravi);
        ProgressReport afterRepeat = progressService.completeLesson(firstLesson, ravi);

        List<Question> questions = topicRepository.findBySlug("oop").orElseThrow().getQuestions();
        Map<Long, Integer> allCorrect = new HashMap<>();
        questions.forEach(q -> allCorrect.put(q.getId(), q.getCorrectOption()));
        quizService.submit("oop", ravi, new QuizSubmission(Map.of(questions.get(0).getId(), 3)));
        quizService.submit("oop", ravi, new QuizSubmission(allCorrect));

        ProgressReport report = progressService.report(ravi);
        ProgressReport.TopicProgress oopProgress = report.topics().stream()
                .filter(t -> t.slug().equals("oop")).findFirst().orElseThrow();

        assertThat(afterRepeat.lessonsCompleted()).isEqualTo(1);
        assertThat(oopProgress.lessonsCompleted()).isEqualTo(1);
        assertThat(oopProgress.completionPercentage()).isEqualTo(50);
        assertThat(oopProgress.quizAttempts()).isEqualTo(2);
        assertThat(oopProgress.bestQuizPercentage()).isEqualTo(100);
        assertThat(report.lessonsCompleted()).isEqualTo(1);
    }

    @Test
    void guestNamesAreCaseInsensitive() {
        assertThat(learnerService.signInAsGuest("Meera  K").getId())
                .isEqualTo(learnerService.signInAsGuest("meera k").getId());
    }

    @Test
    void unknownTopicIsNotFound() {
        assertThatThrownBy(() -> catalogService.getTopic("kotlin")).isInstanceOf(NotFoundException.class);
    }
}
