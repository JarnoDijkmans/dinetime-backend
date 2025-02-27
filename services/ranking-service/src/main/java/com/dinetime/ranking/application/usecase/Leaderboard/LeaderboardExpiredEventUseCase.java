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

    public LeaderboardExpiredEventUseCase(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void execute(LeaderboardExpiredEvent event) {
        long lobbyId = event.getLobbyId();
        Set<ZSetOperations.TypedTuple<String>> mealScores = event.getMealScores();

        List<RankingEntity> rankings = new ArrayList<>();

        for (ZSetOperations.TypedTuple<String> mealScore : mealScores) {
            String mealId = mealScore.getValue();
            Double score = mealScore.getScore();

            if (score != null) {
                // Retrieve user ID associated with this meal ID
                String userKey = "user:data:" + mealId;
                String userId = (String) redisTemplate.opsForHash().get(userKey, "userId");

                if (userId != null) {
                    // Create a RankingEntity and add to the list
                    RankingEntity ranking = new RankingEntity(
                            lobbyId,
                            Integer.parseInt(mealId),
                            score,
                            Long.parseLong(userId));
                    rankings.add(ranking);
                }
            }
        }

        // Create and save the LeaderboardEntity with the list of rankings
        // if (!rankings.isEmpty()) {
        //     LeaderboardEntity leaderboard = new LeaderboardEntity(lobbyId, rankings);
        //     repository.saveLeaderboard(leaderboard);
        // }
    }
}
