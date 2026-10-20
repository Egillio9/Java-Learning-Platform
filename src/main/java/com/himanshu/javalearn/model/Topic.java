package com.himanshu.javalearn.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Column(length = 1000)
    private String summary;

    private int position;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Question> questions = new ArrayList<>();

    protected Topic() {
    }

    public Topic(String slug, String title, Level level, String summary, int position) {
        this.slug = slug;
        this.title = title;
        this.level = level;
        this.summary = summary;
        this.position = position;
    }

    public void reposition(int position) {
        this.position = position;
    }

    public void addLesson(Lesson lesson) {
        lesson.setTopic(this);
        lessons.add(lesson);
    }

    public void addQuestion(Question question) {
        question.setTopic(this);
        questions.add(question);
    }

    public Long getId() { return id; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public Level getLevel() { return level; }
    public String getSummary() { return summary; }
    public int getPosition() { return position; }
    public List<Lesson> getLessons() { return lessons; }
    public List<Question> getQuestions() { return questions; }
}
