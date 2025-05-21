package com.dinetime.ranking.adapters.web.controller;
import com.dinetime.ranking.adapters.web.request.LeaderboardsRequestModel;
import com.dinetime.ranking.adapters.web.response.LeaderboardResponseModel;
import com.dinetime.ranking.ports.input.ILeaderboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ranking")
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