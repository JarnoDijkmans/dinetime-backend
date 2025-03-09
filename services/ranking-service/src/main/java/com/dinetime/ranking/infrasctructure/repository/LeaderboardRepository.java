package com.dinetime.ranking.infrasctructure.repository;

import com.dinetime.ranking.domain.model.Leaderboard;
import com.dinetime.ranking.domain.repository.ILeaderboardRepository;
import com.dinetime.ranking.infrasctructure.entities.LeaderboardEntity;
import com.dinetime.ranking.infrasctructure.jpa.JpaLeaderboardRepo;
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
        Optional<LeaderboardEntity> existingEntity = repository.findByLobbyId(leaderboard.getLobbyId());

        if (existingEntity.isPresent()) {
            LeaderboardEntity entity = existingEntity.get();entity.replaceItem(leaderboard.getItems());
            repository.save(entity);
        } else {
            LeaderboardEntity entity = LeaderboardEntity.fromDomain(leaderboard);
            repository.save(entity);
        }
    }

//        @Override
//        public List<Leaderboard> findByLobbyId(Long lobbyId) {
//            return jpaRepository.findByLobbyId(lobbyId).stream()
//                    .map(LeaderboardEntity::toDomain)
//                    .collect(Collectors.toList());
}
