package com.dinetime.ranking.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Ranking {
    private final long lobbyId;
    private final int mealId;
    private final double score;
    private long userId;

}
