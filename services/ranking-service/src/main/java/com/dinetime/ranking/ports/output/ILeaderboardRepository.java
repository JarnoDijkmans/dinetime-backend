 package com.dinetime.ranking.ports.output;

 import com.dinetime.ranking.domain.model.Leaderboard;

 public interface ILeaderboardRepository {
     void save(Leaderboard leaderboard);
 }
