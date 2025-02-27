package com.dinetime.ranking.domain.repository;

import com.dinetime.ranking.infrastructure.entity.LeaderboardEntity;

public interface ILeaderboardRedisRepository {
    LeaderboardEntity getLeaderboard(long id);
}
