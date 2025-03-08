package com.dinetime.ranking.presentation.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dinetime.ranking.presentation.request.LeaderboardsRequestModel;


@RestController
@RequestMapping("/votes")
public class LeaderboardController {
    // private final IRankingService rankingService;

    // public RankingController(IRankingService rankingService) {
    //     this.rankingService = rankingService;
    // }

    @PostMapping("/batch")
    public ResponseEntity<LeaderboardsRequestModel> batchSaveVotes(@RequestBody LeaderboardsRequestModel leaderboards) {
        return ResponseEntity.ok(leaderboards);
        // rankingService.batchSaveVotes(votes);
        // return ResponseEntity.ok("✅ Votes stored successfully.");
    }
}