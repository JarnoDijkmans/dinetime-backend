package com.dinetime.ranking.infrastructure.repository.jpa;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dinetime.ranking.infrastructure.entity.LeaderboardEntity;

public interface LeaderboardRepository extends JpaRepository<LeaderboardEntity, Long> {
    
}
