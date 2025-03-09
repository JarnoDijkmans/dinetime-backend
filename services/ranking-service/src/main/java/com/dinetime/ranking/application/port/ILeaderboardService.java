package com.dinetime.ranking.application.port;

import com.dinetime.ranking.presentation.request.LeaderboardsRequestModel;
import com.dinetime.ranking.presentation.response.LeaderboardResponseModel;

public interface ILeaderboardService {
    LeaderboardResponseModel batchSaveLeaderboard (LeaderboardsRequestModel requestModel);
}
