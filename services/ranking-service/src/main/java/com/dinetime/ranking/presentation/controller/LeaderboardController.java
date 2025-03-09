package com.dinetime.ranking.presentation.controller;
import com.dinetime.ranking.application.port.ILeaderboardService;
import com.dinetime.ranking.presentation.response.LeaderboardResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dinetime.ranking.presentation.request.LeaderboardsRequestModel;


@RestController
@RequestMapping("/leaderboards")
public class LeaderboardController {
     private final ILeaderboardService leaderboardService;

     public LeaderboardController(ILeaderboardService leaderboardService) {
         this.leaderboardService = leaderboardService;
     }

    @PostMapping("/batch")
    public ResponseEntity<String> batchSaveVotes(@RequestBody LeaderboardsRequestModel leaderboards) {
        LeaderboardResponseModel response = leaderboardService.batchSaveLeaderboard(leaderboards);
         return ResponseEntity.ok(response.getMessage());
    }
}