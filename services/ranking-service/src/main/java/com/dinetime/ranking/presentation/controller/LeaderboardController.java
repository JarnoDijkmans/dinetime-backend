package com.dinetime.ranking.presentation.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dinetime.ranking.application.port.ILeaderboardService;
import com.dinetime.ranking.presentation.response.LeaderboardResponseModel;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {
    private final ILeaderboardService leaderboardService;

    @Autowired
    public LeaderboardController(ILeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getLeaderboard(@PathVariable("id") long id){
        LeaderboardResponseModel leaderboard = leaderboardService.getLeaderboard(id);
        return ResponseEntity.ok(leaderboard);
    }
}
