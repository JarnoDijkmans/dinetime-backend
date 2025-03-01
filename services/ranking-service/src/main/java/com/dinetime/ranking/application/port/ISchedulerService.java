package com.dinetime.ranking.application.port;

public interface ISchedulerService {
    void schedulePreSave(String leaderboardKey, int delayInSeconds);
}
