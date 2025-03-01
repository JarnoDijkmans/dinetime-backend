package com.dinetime.ranking.infrastructure.listener;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.dinetime.ranking.application.event.LeaderboardExpiredEvent;

@Component
public class RedisExpiryListener implements MessageListener {

    private final StringRedisTemplate redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    private static final String LEADERBOARD_PREFIX = "leaderboard:";

    public RedisExpiryListener(StringRedisTemplate redisTemplate, ApplicationEventPublisher eventPublisher) {
        this.redisTemplate = redisTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);

        if (expiredKey.startsWith("preexpiry:")) {
            try {
                // ✅ Extract the original leaderboard key
                String leaderboardKey = expiredKey.replace("preexpiry:", "");

                // ✅ Fetch the leaderboard data before expiration
                Set<ZSetOperations.TypedTuple<String>> mealScores = redisTemplate.opsForZSet().rangeWithScores(leaderboardKey, 0, -1);

                if (mealScores != null && !mealScores.isEmpty()) {
                    // ✅ Extract lobbyId from the leaderboard key
                    long lobbyId = Long.parseLong(leaderboardKey.replace(LEADERBOARD_PREFIX, ""));

                    // ✅ Directly publish Spring Event (No Pub/Sub, No JSON parsing)
                    eventPublisher.publishEvent(new LeaderboardExpiredEvent(lobbyId, mealScores));
                }
            } catch (Exception e) {
                System.err.println("Error processing leaderboard expiration: " + e.getMessage());
            }
        }
    }
}