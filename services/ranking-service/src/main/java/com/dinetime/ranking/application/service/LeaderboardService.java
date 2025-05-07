package com.dinetime.ranking.application.service;


import com.dinetime.ranking.ports.input.ILeaderboardService;

import org.springframework.stereotype.Service;

import com.dinetime.ranking.adapters.web.request.LeaderboardsRequestModel;
import com.dinetime.ranking.adapters.web.response.LeaderboardResponseModel;
import com.dinetime.ranking.application.usecase.StoreLeaderboardUseCase;

@Service
public class LeaderboardService implements ILeaderboardService {
    private final StoreLeaderboardUseCase storeLeaderboardUseCase;

     public LeaderboardService(StoreLeaderboardUseCase storeLeaderboardUseCase) {
         this.storeLeaderboardUseCase = storeLeaderboardUseCase;
     }

     @Override
     public LeaderboardResponseModel batchSaveLeaderboard(LeaderboardsRequestModel requestModel) {
         return storeLeaderboardUseCase.execute(requestModel);
     }
}
