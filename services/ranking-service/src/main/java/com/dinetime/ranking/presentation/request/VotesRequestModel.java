package com.dinetime.ranking.presentation.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VotesRequestModel {
    private List<VoteRequest> votes;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor 
    public static class VoteRequest {
        private long userId;
        private int mealId;
        private long lobbyId;
        private int score;
    }
}
