package com.dinetime.ranking.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LeaderboardItem {
    private final String mealId;
    private final int totalScore;
}
