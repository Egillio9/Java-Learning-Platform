package com.himanshu.javalearn.service;

import com.himanshu.javalearn.dto.StatsDtos.Dashboard;
import com.himanshu.javalearn.dto.StatsDtos.DifficultyCount;
import com.himanshu.javalearn.dto.StatsDtos.LeaderboardEntry;
import com.himanshu.javalearn.dto.StatsDtos.PlatformStats;
import com.himanshu.javalearn.dto.StatsDtos.ProblemRef;
import com.himanshu.javalearn.dto.StatsDtos.RecentSubmission;
import com.himanshu.javalearn.model.Difficulty;
import com.himanshu.javalearn.model.Learner;
import com.himanshu.javalearn.model.Problem;
import com.himanshu.javalearn.model.Verdict;
import com.himanshu.javalearn.repository.LearnerRepository;
import com.himanshu.javalearn.repository.LessonCompletionRepository;
import com.himanshu.javalearn.repository.LessonRepository;
import com.himanshu.javalearn.repository.ProblemRepository;
import com.himanshu.javalearn.repository.SubmissionRepository;
import com.himanshu.javalearn.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class StatsService {

    /** Streak days roll over at midnight India time, where the platform's learners are. */
    private static final ZoneId STREAK_ZONE = ZoneId.of("Asia/Kolkata");
    private static final int POINTS_PER_PROBLEM = 10;
    private static final int POINTS_PER_LESSON = 2;

    private final LearnerService learnerService;
    private final LearnerRepository learnerRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final LessonRepository lessonRepository;
    private final LessonCompletionRepository completionRepository;
    private final TopicRepository topicRepository;

    public StatsService(LearnerService learnerService, LearnerRepository learnerRepository,
                        ProblemRepository problemRepository, SubmissionRepository submissionRepository,
                        LessonRepository lessonRepository, LessonCompletionRepository completionRepository,
                        TopicRepository topicRepository) {
        this.learnerService = learnerService;
        this.learnerRepository = learnerRepository;
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
        this.lessonRepository = lessonRepository;
        this.completionRepository = completionRepository;
        this.topicRepository = topicRepository;
    }

    public Dashboard dashboard(Long learnerId) {
        Learner learner = learnerService.get(learnerId);
        List<Problem> problems = problemRepository.findAllByOrderByPositionAsc();
        Set<Long> solvedIds = new HashSet<>(
                submissionRepository.findProblemIdsByLearnerAndVerdict(learnerId, Verdict.ACCEPTED));

        DifficultyCount solved = count(problems.stream().filter(p -> solvedIds.contains(p.getId())));
        DifficultyCount available = count(problems.stream());
        long submissions = submissionRepository.countByLearnerId(learnerId);
        long accepted = submissionRepository.countByLearnerIdAndVerdict(learnerId, Verdict.ACCEPTED);
        int acceptanceRate = submissions == 0 ? 0 : (int) Math.round(accepted * 100.0 / submissions);
        int lessonsCompleted = completionRepository.findCompletedLessonIds(learnerId).size();

        List<RecentSubmission> recent = submissionRepository.findTop8ByLearnerIdOrderByCreatedAtDesc(learnerId).stream()
                .map(s -> new RecentSubmission(s.getId(), s.getProblem().getSlug(), s.getProblem().getTitle(),
                        s.getProblem().getDifficulty(), s.getVerdict(), s.getCreatedAt()))
                .toList();
        ProblemRef next = problems.stream()
                .filter(p -> !solvedIds.contains(p.getId()))
                .findFirst()
                .map(p -> new ProblemRef(p.getSlug(), p.getTitle(), p.getDifficulty()))
                .orElse(null);

        Integer rank = leaderboard().stream()
                .filter(entry -> entry.learnerId().equals(learnerId))
                .map(LeaderboardEntry::rank)
                .findFirst()
                .orElse(null);

        return new Dashboard(learner.getDisplayName(), learner.getAvatarUrl(), learner.getProvider(),
                learner.getJoinedAt(), solved, available, submissions, accepted, acceptanceRate,
                lessonsCompleted, (int) lessonRepository.count(), streak(learnerId), rank, recent, next);
    }

    /** Ranked by points (10 per solved problem, 2 per lesson); learners with no activity are left out. */
    public List<LeaderboardEntry> leaderboard() {
        Map<Long, Integer> solved = toCounts(submissionRepository.solvedCountByLearner(Verdict.ACCEPTED));
        Map<Long, Integer> lessons = toCounts(completionRepository.completedCountByLearner());
        Set<Long> active = new HashSet<>(solved.keySet());
        active.addAll(lessons.keySet());

        record Row(Learner learner, int solved, int lessons, int score) {
        }
        List<Row> rows = new ArrayList<>();
        for (Learner learner : learnerRepository.findAllById(active)) {
            int s = solved.getOrDefault(learner.getId(), 0);
            int l = lessons.getOrDefault(learner.getId(), 0);
            rows.add(new Row(learner, s, l, s * POINTS_PER_PROBLEM + l * POINTS_PER_LESSON));
        }
        rows.sort(Comparator.comparingInt(Row::score).reversed()
                .thenComparing(Comparator.comparingInt(Row::solved).reversed())
                .thenComparing(row -> row.learner().getJoinedAt()));

        List<LeaderboardEntry> entries = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            entries.add(new LeaderboardEntry(i + 1, row.learner().getId(), row.learner().getDisplayName(),
                    row.learner().getAvatarUrl(), row.solved(), row.lessons(), row.score()));
        }
        return entries;
    }

    public PlatformStats platform() {
        return new PlatformStats(problemRepository.count(), topicRepository.count(), lessonRepository.count(),
                learnerRepository.count(), submissionRepository.count());
    }

    /** Consecutive days with any submission or lesson, ending today (or yesterday, so it survives until midnight). */
    int streak(Long learnerId) {
        Set<LocalDate> days = new HashSet<>();
        Stream.concat(submissionRepository.findActivityTimes(learnerId).stream(),
                        completionRepository.findActivityTimes(learnerId).stream())
                .map(instant -> instant.atZone(STREAK_ZONE).toLocalDate())
                .forEach(days::add);
        LocalDate day = LocalDate.now(STREAK_ZONE);
        if (!days.contains(day)) {
            day = day.minusDays(1);
        }
        int streak = 0;
        while (days.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }

    private static DifficultyCount count(Stream<Problem> problems) {
        Map<Difficulty, Integer> counts = new HashMap<>();
        problems.forEach(p -> counts.merge(p.getDifficulty(), 1, Integer::sum));
        return new DifficultyCount(counts.getOrDefault(Difficulty.EASY, 0),
                counts.getOrDefault(Difficulty.MEDIUM, 0), counts.getOrDefault(Difficulty.HARD, 0));
    }

    private static Map<Long, Integer> toCounts(List<Object[]> rows) {
        Map<Long, Integer> counts = new HashMap<>();
        for (Object[] row : rows) {
            counts.put((Long) row[0], ((Number) row[1]).intValue());
        }
        return counts;
    }
}
