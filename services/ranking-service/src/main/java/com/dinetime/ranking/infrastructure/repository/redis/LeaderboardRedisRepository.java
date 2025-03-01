package com.dinetime.ranking.infrastructure.repository.redis;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import com.dinetime.ranking.domain.repository.ILeaderboardRedisRepository;
import com.dinetime.ranking.infrastructure.entity.LeaderboardEntity;
import com.dinetime.ranking.infrastructure.entity.RankingEntity;

@Repository
public class LeaderboardRedisRepository implements ILeaderboardRedisRepository {

    private static final String LEADERBOARD_KEY_PREFIX = "leaderboard:";

    private final StringRedisTemplate redisTemplate;

    public LeaderboardRedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public LeaderboardEntity getLeaderboard(long lobbyId) {
        String leaderboardKey = LEADERBOARD_KEY_PREFIX + lobbyId;
    
        // Fetch meal IDs and their aggregated scores in descending order
        Set<ZSetOperations.TypedTuple<String>> mealScores = redisTemplate.opsForZSet()
                .reverseRangeWithScores(leaderboardKey, 0, -1);
    
        if (mealScores == null || mealScores.isEmpty()) {
            return new LeaderboardEntity(lobbyId, Collections.emptyList());
        }
    
        @SuppressWarnings("null")
        List<RankingEntity> rankings = mealScores.stream()
                .map(mealScore -> new RankingEntity(
                        lobbyId, 
                        Integer.parseInt(mealScore.getValue()), 
                        mealScore.getScore() != null ? mealScore.getScore() : 0.0
                ))
                .toList();
    
        return new LeaderboardEntity(lobbyId, rankings);
    }

}
