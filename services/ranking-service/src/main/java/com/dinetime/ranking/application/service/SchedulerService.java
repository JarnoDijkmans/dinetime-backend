package com.dinetime.ranking.application.service;

import com.dinetime.ranking.application.port.ISchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Scope("singleton")
public class SchedulerService implements ISchedulerService {

    private final TaskScheduler taskScheduler;
    private final StringRedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);
    private final Set<String> scheduledTasks = ConcurrentHashMap.newKeySet();

    @Autowired
    public SchedulerService(TaskScheduler taskScheduler, StringRedisTemplate redisTemplate) {
        this.taskScheduler = taskScheduler;
        this.redisTemplate = redisTemplate;
    }

    public void schedulePreSave(String leaderboardKey, int delayInSeconds) {
        if (scheduledTasks.contains(leaderboardKey)) {
            logger.info("🚨 Pre-save already scheduled for leaderboard: {}", leaderboardKey);
            return; // ✅ Prevent duplicate scheduling
        }

        scheduledTasks.add(leaderboardKey);
        logger.info("✅ Scheduling pre-save in {} seconds for leaderboard: {}", delayInSeconds, leaderboardKey);

        taskScheduler.schedule(() -> {
            try {
                mockSave(leaderboardKey);
            } finally {
                scheduledTasks.remove(leaderboardKey); // ✅ Remove after execution
            }
        }, Instant.now().plusSeconds(delayInSeconds));
    }

    // ✅ Mocked save function
    private void mockSave(String leaderboardKey) {
        Set<ZSetOperations.TypedTuple<String>> mealScores = redisTemplate.opsForZSet().rangeWithScores(leaderboardKey, 0, -1);
        logger.info("✅ Scheduling pre-save for leaderboard: {}", leaderboardKey);
        logger.info("🔹 [Mock] Scheduled pre-save triggered for leaderboard: {}", mealScores);

    }
}
