package com.dinetime.ranking.application.port;

import com.dinetime.ranking.presentation.response.LeaderboardResponseModel;

public interface ILeaderboardService {
    LeaderboardResponseModel getLeaderboard(long id);
}
