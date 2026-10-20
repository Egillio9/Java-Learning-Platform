package com.himanshu.javalearn.service;

import com.himanshu.javalearn.dto.JudgeDtos.ProblemDetail;
import com.himanshu.javalearn.dto.JudgeDtos.ProblemStatus;
import com.himanshu.javalearn.dto.JudgeDtos.ProblemSummary;
import com.himanshu.javalearn.dto.JudgeDtos.SampleCase;
import com.himanshu.javalearn.dto.JudgeDtos.SolutionView;
import com.himanshu.javalearn.dto.JudgeDtos.SubmissionView;
import com.himanshu.javalearn.exception.ForbiddenException;
import com.himanshu.javalearn.exception.NotFoundException;
import com.himanshu.javalearn.model.Problem;
import com.himanshu.javalearn.model.Verdict;
import com.himanshu.javalearn.repository.ProblemRepository;
import com.himanshu.javalearn.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;

    public ProblemService(ProblemRepository problemRepository, SubmissionRepository submissionRepository) {
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
    }

    private record Acceptance(long submissions, int rate) {
    }

    /** learnerId may be null for visitors who have not signed in. */
    public List<ProblemSummary> list(Long learnerId) {
        Map<Long, Acceptance> acceptance = acceptanceByProblem();
        Statuses statuses = statuses(learnerId);
        return problemRepository.findAllByOrderByPositionAsc().stream()
                .map(problem -> {
                    Acceptance stats = acceptance.getOrDefault(problem.getId(), new Acceptance(0, 0));
                    return new ProblemSummary(problem.getSlug(), problem.getTitle(), problem.getDifficulty(),
                            problem.tagList(), stats.rate(), stats.submissions(), statuses.of(problem.getId()));
                })
                .toList();
    }

    public ProblemDetail detail(String slug, Long learnerId) {
        List<Problem> ordered = problemRepository.findAllByOrderByPositionAsc();
        int index = -1;
        for (int i = 0; i < ordered.size(); i++) {
            if (ordered.get(i).getSlug().equals(slug)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new NotFoundException("Problem not found: " + slug);
        }
        Problem problem = ordered.get(index);
        List<SampleCase> samples = problem.sampleCases().stream()
                .map(sample -> new SampleCase(sample.getInput(), sample.getExpectedOutput()))
                .toList();
        Acceptance stats = acceptanceByProblem().getOrDefault(problem.getId(), new Acceptance(0, 0));
        String previous = index > 0 ? ordered.get(index - 1).getSlug() : null;
        String next = index < ordered.size() - 1 ? ordered.get(index + 1).getSlug() : null;
        return new ProblemDetail(problem.getSlug(), problem.getTitle(), problem.getDifficulty(), problem.paramList(),
                problem.tagList(),
                problem.getDescription(), problem.getStarterCode(), samples, stats.rate(), stats.submissions(),
                statuses(learnerId).of(problem.getId()), previous, next);
    }

    public List<SubmissionView> submissions(String slug, Long learnerId) {
        requireProblem(slug);
        return submissionRepository.findByLearnerIdAndProblemSlugOrderByCreatedAtDesc(learnerId, slug).stream()
                .map(s -> new SubmissionView(s.getId(), s.getVerdict(), s.getPassed(), s.getTotal(), s.getRuntimeMs(),
                        s.getCreatedAt(), s.getCode()))
                .toList();
    }

    /** The reference solution unlocks after the learner's first accepted submission, like an editorial. */
    public SolutionView solution(String slug, Long learnerId) {
        Problem problem = requireProblem(slug);
        if (!submissionRepository.existsByLearnerIdAndProblemSlugAndVerdict(learnerId, slug, Verdict.ACCEPTED)) {
            throw new ForbiddenException("Solve the problem first to unlock the reference solution");
        }
        return new SolutionView(slug, problem.getReferenceSolution());
    }

    private Problem requireProblem(String slug) {
        return problemRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Problem not found: " + slug));
    }

    private Map<Long, Acceptance> acceptanceByProblem() {
        Map<Long, Acceptance> result = new HashMap<>();
        for (Object[] row : submissionRepository.acceptanceByProblem(Verdict.ACCEPTED)) {
            long total = ((Number) row[1]).longValue();
            long accepted = row[2] == null ? 0 : ((Number) row[2]).longValue();
            result.put((Long) row[0], new Acceptance(total, total == 0 ? 0 : (int) Math.round(accepted * 100.0 / total)));
        }
        return result;
    }

    private record Statuses(Set<Long> solved, Set<Long> attempted) {
        ProblemStatus of(Long problemId) {
            if (solved.contains(problemId)) return ProblemStatus.SOLVED;
            if (attempted.contains(problemId)) return ProblemStatus.ATTEMPTED;
            return ProblemStatus.TODO;
        }
    }

    private Statuses statuses(Long learnerId) {
        if (learnerId == null) {
            return new Statuses(Set.of(), Set.of());
        }
        return new Statuses(
                new HashSet<>(submissionRepository.findProblemIdsByLearnerAndVerdict(learnerId, Verdict.ACCEPTED)),
                new HashSet<>(submissionRepository.findAttemptedProblemIds(learnerId)));
    }
}
