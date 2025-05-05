package com.dinetime.ranking.adapters.persistance.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dinetime.ranking.adapters.persistance.jpa.entity.LeaderboardEntity;

import java.util.Optional;

@Repository
public interface JpaLeaderboardRepo extends JpaRepository<LeaderboardEntity, Long> {
    Optional<LeaderboardEntity> findByLobbyCode(String lobbyCode);
}

