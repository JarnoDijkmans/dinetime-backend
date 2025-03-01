package com.dinetime.ranking.infrastructure.repository.redis;


import java.util.concurrent.TimeUnit;

import com.dinetime.ranking.application.port.ISchedulerService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.dinetime.ranking.domain.repository.IRankingRedisRepository;
import com.dinetime.ranking.infrastructure.entity.RankingEntity;

@Repository
public class RankingRedisRepository implements IRankingRedisRepository {

    private static final String LEADERBOARD_KEY_PREFIX = "leaderboard:";


    private final RedisTemplate<String, Object> redisTemplate;
    private final ISchedulerService schedulerService; // ✅ Injected dependency

    public RankingRedisRepository(RedisTemplate<String, Object> redisTemplate, ISchedulerService schedulerService) {
        this.redisTemplate = redisTemplate;
        this.schedulerService = schedulerService; // ✅ Use instance, not static
    }

    public void storeRanking(RankingEntity entity) {
        String leaderboardKey = LEADERBOARD_KEY_PREFIX + entity.getLobbyId();
        String userVoteKey = "user:votes:" + entity.getUserId();
        String mealId = String.valueOf(entity.getMealId());

        Object previousVoteObj = redisTemplate.opsForHash().get(userVoteKey, mealId);
        double previousVote = previousVoteObj != null ? Double.parseDouble(previousVoteObj.toString()) : 0.0;

        double voteDifference = entity.getScore() - previousVote;
        if (voteDifference != 0) {
            redisTemplate.opsForZSet().incrementScore(leaderboardKey, mealId, voteDifference);
            redisTemplate.opsForHash().put(userVoteKey, mealId, String.valueOf(entity.getScore()));

            // ✅ Set actual expiration (30 seconds)
            redisTemplate.expire(leaderboardKey, 30, TimeUnit.SECONDS);
            redisTemplate.expire(userVoteKey, 30, TimeUnit.SECONDS);

            // ✅ Schedule pre-save 5 seconds before expiry
            schedulerService.schedulePreSave(leaderboardKey,  20);
        }

    }


}