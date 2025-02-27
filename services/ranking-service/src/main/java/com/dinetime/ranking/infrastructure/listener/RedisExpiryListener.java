package com.dinetime.ranking.infrastructure.listener;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.dinetime.ranking.application.event.LeaderboardExpiredEvent;
import com.dinetime.ranking.application.port.IEventPublisher;

@Component
public class RedisExpiryListener implements MessageListener {

    private final StringRedisTemplate redisTemplate;
    private final IEventPublisher eventPublisher;

    @Autowired
    public RedisExpiryListener(StringRedisTemplate redisTemplate, IEventPublisher eventPublisher) {
        this.redisTemplate = redisTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onMessage(@SuppressWarnings("null") Message message, @SuppressWarnings("null") byte[] pattern) {
        String expiredKey = new String(message.getBody());

        if (expiredKey.startsWith("leaderboard:")) {
            long lobbyId = Long.parseLong(expiredKey.replace("leaderboard:", ""));

            // Fetch all meal IDs and scores from Redis
            Set<ZSetOperations.TypedTuple<String>> mealScores = redisTemplate.opsForZSet().rangeWithScores(expiredKey, 0, -1);

            // Publish the event
            LeaderboardExpiredEvent event = new LeaderboardExpiredEvent(lobbyId, mealScores);
            eventPublisher.publish(event);
        }
    }
}