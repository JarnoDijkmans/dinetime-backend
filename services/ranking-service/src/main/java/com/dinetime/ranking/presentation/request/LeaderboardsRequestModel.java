package com.dinetime.ranking.presentation.request;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardsRequestModel {
    private Map<Long, List<LeaderboardEntry>> leaderboards;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LeaderboardEntry {
        private int mealId;
        private int totalScore;
    }
}
