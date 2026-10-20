package com.himanshu.javalearn.dto;

import com.himanshu.javalearn.model.AuthProvider;
import com.himanshu.javalearn.model.Difficulty;
import com.himanshu.javalearn.model.Verdict;

import java.time.Instant;
import java.util.List;

public final class StatsDtos {

    private StatsDtos() {
    }

    public record DifficultyCount(int easy, int medium, int hard) {
        public int total() {
            return easy + medium + hard;
        }
    }

    public record RecentSubmission(Long id, String slug, String title, Difficulty difficulty, Verdict verdict,
                                   Instant createdAt) {
    }

    public record ProblemRef(String slug, String title, Difficulty difficulty) {
    }

    public record Dashboard(String displayName, String avatarUrl, AuthProvider provider, Instant joinedAt,
                            DifficultyCount solved, DifficultyCount available,
                            long submissions, long acceptedSubmissions, int acceptanceRate,
                            int lessonsCompleted, int totalLessons, int streakDays, Integer rank,
                            List<RecentSubmission> recent, ProblemRef nextProblem) {
    }

    public record LeaderboardEntry(int rank, Long learnerId, String displayName, String avatarUrl,
                                   int solved, int lessonsCompleted, int score) {
    }

    public record PlatformStats(long problems, long topics, long lessons, long learners, long submissions) {
    }
}
