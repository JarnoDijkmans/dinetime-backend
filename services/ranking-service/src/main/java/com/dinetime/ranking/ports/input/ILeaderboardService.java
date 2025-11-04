package com.dinetime.ranking.ports.input;

import com.dinetime.ranking.adapters.web.request.LeaderboardsRequestModel;
import com.dinetime.ranking.adapters.web.response.LeaderboardResponseModel;

public interface ILeaderboardService {
    LeaderboardResponseModel batchSaveLeaderboard (LeaderboardsRequestModel requestModel);
}
