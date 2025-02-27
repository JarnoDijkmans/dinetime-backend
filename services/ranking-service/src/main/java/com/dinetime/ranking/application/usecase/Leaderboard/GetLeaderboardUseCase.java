package com.dinetime.ranking.application.usecase.Leaderboard;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dinetime.ranking.domain.model.Leaderboard;
import com.dinetime.ranking.domain.model.Ranking;
import com.dinetime.ranking.domain.repository.ILeaderboardRedisRepository;
import com.dinetime.ranking.infrastructure.entity.LeaderboardEntity;
import com.dinetime.ranking.presentation.response.LeaderboardResponseModel.RankingOutput;
import com.dinetime.ranking.presentation.response.LeaderboardResponseModel;

@Service
public class GetLeaderboardUseCase {
    private final ILeaderboardRedisRepository repository;

    public GetLeaderboardUseCase(ILeaderboardRedisRepository repository) {
        this.repository = repository;
    }

    public LeaderboardResponseModel execute(long lobbyId) {
        LeaderboardEntity leaderboardEntity = repository.getLeaderboard(lobbyId);
        
        // Step 1: Entity → Domain Model
        List<Ranking> rankings = leaderboardEntity.getRankings().stream()
            .map(rankingEntity -> new Ranking(
                rankingEntity.getLobbyId(),
                rankingEntity.getMealId(),
                rankingEntity.getScore()
            ))
            .toList();
    
        // Step 2: Create a domain model for the leaderboard
        Leaderboard leaderboard = new Leaderboard(leaderboardEntity.getLobbyId(), rankings);
        
        // Step 3: Domain Model → Response Model
        List<RankingOutput> rankingEntries = leaderboard.getRankings().stream()
            .map(ranking -> new RankingOutput(
                ranking.getLobbyId(),
                ranking.getMealId(),
                ranking.getScore()
            ))
            .toList();
    
        return new LeaderboardResponseModel(leaderboard.getLobbyId(), rankingEntries);
    }
}
