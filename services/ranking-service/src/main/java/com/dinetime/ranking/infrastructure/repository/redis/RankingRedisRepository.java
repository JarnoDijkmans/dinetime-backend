package com.dinetime.ranking.infrastructure.repository.redis;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.dinetime.ranking.domain.repository.IRankingRedisRepository;
import com.dinetime.ranking.infrastructure.entity.RankingEntity;

@Repository
public class RankingRedisRepository implements IRankingRedisRepository {

    private static final String LEADERBOARD_KEY_PREFIX = "leaderboard:";

    private final RedisTemplate<String, Object> redisTemplate;

    public RankingRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeRanking(RankingEntity entity) {
        String leaderboardKey = LEADERBOARD_KEY_PREFIX + entity.getLobbyId();
        String preBackupKey = leaderboardKey + ":prebackup";
        String userVoteKey = "user:votes:" + entity.getUserId();

        String mealId = String.valueOf(entity.getMealId());

        Object previousVoteObj = redisTemplate.opsForHash().get(userVoteKey, mealId);
        double previousVote = previousVoteObj != null ? Double.parseDouble(previousVoteObj.toString()) : 0.0;

        double voteDifference = entity.getScore() - previousVote;

        if (voteDifference != 0) {
            redisTemplate.opsForZSet().incrementScore(leaderboardKey, mealId, voteDifference);
            redisTemplate.opsForHash().put(userVoteKey, mealId, String.valueOf(entity.getScore()));

            Long ttl = redisTemplate.getExpire(leaderboardKey);
            if (ttl == -1) { // -1 means no expiration is set
                redisTemplate.expire(leaderboardKey, Duration.ofMinutes(1));
                redisTemplate.expire(preBackupKey, Duration.ofMinutes(705));
            }
        }
    }

}