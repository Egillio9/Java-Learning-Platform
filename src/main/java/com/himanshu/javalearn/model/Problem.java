package com.himanshu.javalearn.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    /** Comma-separated parameter names, e.g. "nums,target", used to label inputs. */
    private String params;

    /** Comma-separated, e.g. "Array,Hash Table". */
    private String tags;

    private int position;

    @Column(length = 10_000, nullable = false)
    private String description;

    @Column(length = 4_000, nullable = false)
    private String starterCode;

    /** Hidden Main class that parses each test input, calls the learner's Solution and prints the result. */
    @Column(length = 4_000, nullable = false)
    private String driverCode;

    @Column(length = 10_000, nullable = false)
    private String referenceSolution;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<TestCase> testCases = new ArrayList<>();

    protected Problem() {
    }

    public Problem(String slug) {
        this.slug = slug;
    }

    public void update(String title, Difficulty difficulty, List<String> params, List<String> tags, int position,
                       String description,
                       String starterCode, String driverCode, String referenceSolution) {
        this.title = title;
        this.difficulty = difficulty;
        this.params = String.join(",", params);
        this.tags = String.join(",", tags);
        this.position = position;
        this.description = description;
        this.starterCode = starterCode;
        this.driverCode = driverCode;
        this.referenceSolution = referenceSolution;
    }

    public void replaceTestCases(List<TestCase> cases) {
        testCases.clear();
        for (TestCase testCase : cases) {
            testCase.setProblem(this);
            testCases.add(testCase);
        }
    }

    public List<String> tagList() {
        return tags == null || tags.isBlank() ? List.of() : Arrays.asList(tags.split(","));
    }

    public List<String> paramList() {
        return params == null || params.isBlank() ? List.of() : Arrays.asList(params.split(","));
    }

    public List<TestCase> sampleCases() {
        return testCases.stream().filter(TestCase::isSample).toList();
    }

    public Long getId() { return id; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public Difficulty getDifficulty() { return difficulty; }
    public int getPosition() { return position; }
    public String getDescription() { return description; }
    public String getStarterCode() { return starterCode; }
    public String getDriverCode() { return driverCode; }
    public String getReferenceSolution() { return referenceSolution; }
    public List<TestCase> getTestCases() { return testCases; }
}
