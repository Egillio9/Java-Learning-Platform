package com.himanshu.javalearn.controller;

import com.himanshu.javalearn.auth.AuthUser;
import com.himanshu.javalearn.dto.StatsDtos.Dashboard;
import com.himanshu.javalearn.dto.StatsDtos.LeaderboardEntry;
import com.himanshu.javalearn.dto.StatsDtos.PlatformStats;
import com.himanshu.javalearn.service.StatsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StatsController {

    private static final int LEADERBOARD_SIZE = 50;

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/me/dashboard")
    public Dashboard dashboard(@AuthenticationPrincipal AuthUser user) {
        return statsService.dashboard(user.id());
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> leaderboard() {
        return statsService.leaderboard().stream().limit(LEADERBOARD_SIZE).toList();
    }

    @GetMapping("/stats")
    public PlatformStats platform() {
        return statsService.platform();
    }
}
