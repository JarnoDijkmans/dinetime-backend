package com.dinetime.ranking.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class LeaderboardItem {
    private final int mealId;
    private final int totalScore;
}
