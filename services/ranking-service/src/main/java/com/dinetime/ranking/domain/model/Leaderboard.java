package com.dinetime.ranking.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Leaderboard {
    private final long lobbyId;
    private final List<Ranking> rankings;
}