package com.himanshu.javalearn.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(length = 1000, nullable = false)
    private String prompt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "option_index")
    @Column(name = "option_text", length = 500)
    private List<String> options = new ArrayList<>();

    private int correctOption;

    @Column(length = 1000)
    private String explanation;

    protected Question() {
    }

    public Question(String prompt, List<String> options, int correctOption, String explanation) {
        if (correctOption < 0 || correctOption >= options.size()) {
            throw new IllegalArgumentException("correctOption must point at one of the options");
        }
        this.prompt = prompt;
        this.options = new ArrayList<>(options);
        this.correctOption = correctOption;
        this.explanation = explanation;
    }

    public boolean isCorrect(Integer chosenOption) {
        return chosenOption != null && chosenOption == correctOption;
    }

    void setTopic(Topic topic) { this.topic = topic; }

    public Long getId() { return id; }
    public Topic getTopic() { return topic; }
    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return options; }
    public int getCorrectOption() { return correctOption; }
    public String getExplanation() { return explanation; }
}
