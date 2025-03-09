package com.dinetime.ranking.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class LeaderboardItems {
    private final int mealId;
    private final double totalScore;
}
