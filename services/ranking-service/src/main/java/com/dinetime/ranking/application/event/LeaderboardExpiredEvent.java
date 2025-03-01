package com.dinetime.ranking.application.event;

import java.util.Set;
import org.springframework.data.redis.core.ZSetOperations;

public class LeaderboardExpiredEvent {
    private final long lobbyId;
    private final Set<ZSetOperations.TypedTuple<String>> mealScores;

    public LeaderboardExpiredEvent(long lobbyId, Set<ZSetOperations.TypedTuple<String>> mealScores) {
        this.lobbyId = lobbyId;
        this.mealScores = mealScores;
    }

    public long getLobbyId() {
        return lobbyId;
    }

    public Set<ZSetOperations.TypedTuple<String>> getMealScores() {
        return mealScores;
    }
}
