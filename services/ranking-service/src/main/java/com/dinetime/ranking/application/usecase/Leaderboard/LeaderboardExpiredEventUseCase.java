package com.dinetime.ranking.application.usecase.Leaderboard;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.dinetime.ranking.application.event.LeaderboardExpiredEvent;
import com.dinetime.ranking.infrastructure.entity.RankingEntity;

@Service
public class LeaderboardExpiredEventUseCase {

    private final StringRedisTemplate redisTemplate;
//    private final IRankingRepository rankingRepository; // Add Repository for DB Save

    public LeaderboardExpiredEventUseCase(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
//        this.rankingRepository = rankingRepository;
    }

    public void execute(LeaderboardExpiredEvent event) {
        long lobbyId = event.getLobbyId();
        Set<ZSetOperations.TypedTuple<String>> mealScores = event.getMealScores();

        List<Object> mealIds = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> mealScore : mealScores) {
            if (mealScore.getValue() != null) {
                mealIds.add(mealScore.getValue());
            }
        }

        // Fetch all user IDs in one Redis call
        List<Object> userIds = redisTemplate.opsForHash().multiGet("user:data", mealIds);

        List<RankingEntity> rankings = new ArrayList<>();
        int index = 0;
        for (ZSetOperations.TypedTuple<String> mealScore : mealScores) {
            String mealId = mealScore.getValue();
            Double score = mealScore.getScore();
            String userIdStr = userIds.get(index) != null ? userIds.get(index).toString() : null;
            index++;

            if (mealId != null && score != null && userIdStr != null) {
                rankings.add(new RankingEntity(lobbyId, Integer.parseInt(mealId), score, Long.parseLong(userIdStr)));
            }
        }

        // Batch save rankings
        if (!rankings.isEmpty()) {
//            rankingRepository.saveAll(rankings);
        }
    }
}
