package com.dinetime.ranking.presentation.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinetime.ranking.application.port.IRankingService;
import com.dinetime.ranking.presentation.request.RankingRequestModel;


@RestController
@RequestMapping("/ranking")
public class RankingController {
    private final IRankingService rankingService;

    @Autowired
    public RankingController(IRankingService rankingService) {
        this.rankingService = rankingService;
    }

    @PostMapping("store")
    public ResponseEntity<Object> storeRanking(@RequestBody RankingRequestModel requestModel){
        rankingService.storeRankings(requestModel);
        //Receive a response
        return ResponseEntity.ok("Ranking stored!");
    }
    
}
