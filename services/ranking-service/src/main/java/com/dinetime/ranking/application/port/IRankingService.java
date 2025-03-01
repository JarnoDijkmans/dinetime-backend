package com.dinetime.ranking.application.port;

import com.dinetime.ranking.presentation.request.RankingRequestModel;

public interface IRankingService {
    void storeRankings(RankingRequestModel rankings);
}
