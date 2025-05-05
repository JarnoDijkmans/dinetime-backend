package com.dinetime.ranking.adapters.web.request;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardsRequestModel {
    private Map<String, List<LeaderboardEntry>> leaderboards;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LeaderboardEntry {
        private String mealId;
        private int totalScore;
    }
}
