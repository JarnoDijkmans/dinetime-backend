package com.dinetime.ranking.presentation.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LeaderboardResponseModel {
    private final long lobbyId;
    private final List<RankingOutput> rankings;

@Getter
@AllArgsConstructor
public static class RankingOutput {
    private final long lobbyId;
    private final int mealId;
    private final double score;
}
}
