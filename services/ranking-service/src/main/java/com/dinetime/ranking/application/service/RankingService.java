package com.dinetime.ranking.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinetime.ranking.application.port.IRankingService;
import com.dinetime.ranking.application.usecase.Ranking.StoreRankingUseCase;
import com.dinetime.ranking.presentation.request.RankingRequestModel;

@Service
public class RankingService implements IRankingService {

   private final StoreRankingUseCase storeRankingUseCase;

   @Autowired
   public RankingService(StoreRankingUseCase storeRankingUseCase) {
       this.storeRankingUseCase = storeRankingUseCase;
   }

    public void storeRankings(RankingRequestModel rankings) {
        storeRankingUseCase.execute(rankings);
       
    }
    
}
