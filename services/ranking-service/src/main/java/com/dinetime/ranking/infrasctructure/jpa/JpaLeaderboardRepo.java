package com.dinetime.ranking.infrasctructure.jpa;

import com.dinetime.ranking.infrasctructure.entities.LeaderboardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaLeaderboardRepo extends JpaRepository<LeaderboardEntity, Long> {
    Optional<LeaderboardEntity> findByLobbyId(long lobbyId);
}

