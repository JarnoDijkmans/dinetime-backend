package com.dinetime.ranking.domain.repository;

import com.dinetime.ranking.infrastructure.entity.RankingEntity;

public interface IRankingRedisRepository {
    void storeRanking(RankingEntity ranking);
}
