package com.dinetime.ranking.presentation.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RankingRequestModel {
    private List<RankingRequest> rankings;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor 
    public static class RankingRequest {
        private long lobbyId;
        private int mealId;
        private double score;
        private long userId;
    }
}
