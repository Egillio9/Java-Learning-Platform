package com.himanshu.javalearn.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.himanshu.javalearn.model.Difficulty;
import com.himanshu.javalearn.model.Problem;
import com.himanshu.javalearn.model.TestCase;
import com.himanshu.javalearn.repository.ProblemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Syncs the problem bank from src/main/resources/problems/* on every start. Each folder holds
 * problem.json (metadata + tests), description.md, starter.java, driver.java and solution.java.
 */
@Component
public class ProblemSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProblemSeeder.class);

    record TestSpec(String input, String expected, boolean sample) {
    }

    record ProblemSpec(String slug, String title, Difficulty difficulty, List<String> params, List<String> tags,
                       int position,
                       List<TestSpec> tests) {
    }

    private final ProblemRepository problemRepository;
    private final ObjectMapper objectMapper;

    public ProblemSeeder(ProblemRepository problemRepository, ObjectMapper objectMapper) {
        this.problemRepository = problemRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws IOException {
        Resource[] specs = new PathMatchingResourcePatternResolver().getResources("classpath*:problems/*/problem.json");
        for (Resource specFile : specs) {
            ProblemSpec spec;
            try (InputStream in = specFile.getInputStream()) {
                spec = objectMapper.readValue(in, ProblemSpec.class);
            }
            Problem problem = problemRepository.findBySlug(spec.slug()).orElseGet(() -> new Problem(spec.slug()));
            problem.update(spec.title(), spec.difficulty(), spec.params(), spec.tags(), spec.position(),
                    read(specFile, "description.md"), read(specFile, "starter.java"),
                    read(specFile, "driver.java"), read(specFile, "solution.java"));
            if (!sameTests(problem, spec.tests())) {
                List<TestCase> cases = new ArrayList<>();
                for (int i = 0; i < spec.tests().size(); i++) {
                    TestSpec test = spec.tests().get(i);
                    cases.add(new TestCase(i + 1, test.input(), test.expected(), test.sample()));
                }
                problem.replaceTestCases(cases);
            }
            problemRepository.save(problem);
        }
        log.info("Problem bank synced: {} problems", specs.length);
    }

    /** Keeps stored test cases (and their cached expected outputs) when the file has not changed. */
    private static boolean sameTests(Problem problem, List<TestSpec> tests) {
        List<TestCase> existing = problem.getTestCases();
        if (existing.size() != tests.size()) {
            return false;
        }
        for (int i = 0; i < tests.size(); i++) {
            TestCase stored = existing.get(i);
            TestSpec spec = tests.get(i);
            if (!stored.getInput().equals(spec.input()) || stored.isSample() != spec.sample()
                    || (spec.expected() != null && !spec.expected().equals(stored.getExpectedOutput()))) {
                return false;
            }
        }
        return true;
    }

    private static String read(Resource specFile, String name) throws IOException {
        Resource file = specFile.createRelative(name);
        try (InputStream in = file.getInputStream()) {
            return Objects.requireNonNull(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }
    }
}
