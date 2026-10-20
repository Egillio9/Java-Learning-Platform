package com.himanshu.javalearn.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000, nullable = false)
    private String content;

    @Column(length = 4000)
    private String codeExample;

    private int position;

    protected Lesson() {
    }

    public Lesson(String title, String content, String codeExample, int position) {
        this.title = title;
        this.content = content;
        this.codeExample = codeExample;
        this.position = position;
    }

    void setTopic(Topic topic) { this.topic = topic; }

    public Long getId() { return id; }
    public Topic getTopic() { return topic; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCodeExample() { return codeExample; }
    public int getPosition() { return position; }
}
