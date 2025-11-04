package com.dinetime.ranking.adapters.persistance.repository;

import com.dinetime.ranking.adapters.persistance.jpa.JpaLeaderboardRepo;
import com.dinetime.ranking.adapters.persistance.jpa.entity.LeaderboardEntity;
import com.dinetime.ranking.domain.model.Leaderboard;
import com.dinetime.ranking.ports.output.ILeaderboardRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class LeaderboardRepository implements ILeaderboardRepository {
    private final JpaLeaderboardRepo repository;

    public LeaderboardRepository(JpaLeaderboardRepo repository) {
        this.repository = repository;
    }


    @Override
    public void save(Leaderboard leaderboard) {
        Optional<LeaderboardEntity> existingEntity = repository.findByLobbyCode(leaderboard.getLobbyCode());

        if (existingEntity.isPresent()) {
            LeaderboardEntity entity = existingEntity.get();entity.replaceItem(leaderboard.getItems());
            repository.save(entity);
        } else {
            LeaderboardEntity entity = LeaderboardEntity.fromDomain(leaderboard);
            repository.save(entity);
        }
    }
}
