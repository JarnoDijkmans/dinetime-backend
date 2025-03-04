package com.dinetime.ranking.presentation.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dinetime.ranking.presentation.request.VotesRequestModel;


@RestController
@RequestMapping("/votes")
public class RankingController {
    // private final IRankingService rankingService;

    // public RankingController(IRankingService rankingService) {
    //     this.rankingService = rankingService;
    // }

    @PostMapping("/batch")
    public ResponseEntity<VotesRequestModel> batchSaveVotes(@RequestBody VotesRequestModel votes) {
        return ResponseEntity.ok(votes);
        // rankingService.batchSaveVotes(votes);
        // return ResponseEntity.ok("✅ Votes stored successfully.");
    }
}