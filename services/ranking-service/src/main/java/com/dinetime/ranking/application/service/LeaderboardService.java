package com.dinetime.ranking.application.service;


import com.dinetime.ranking.application.usecase.Leaderboard.StoreLeaderboardUseCase;
import com.dinetime.ranking.presentation.request.LeaderboardsRequestModel;
import com.dinetime.ranking.presentation.response.LeaderboardResponseModel;
import org.springframework.stereotype.Service;

import com.dinetime.ranking.application.port.ILeaderboardService;

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
