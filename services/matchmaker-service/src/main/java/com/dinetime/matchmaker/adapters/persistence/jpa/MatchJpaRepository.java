package com.dinetime.matchmaker.adapters.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dinetime.matchmaker.adapters.persistence.jpa.entity.MatchEntity;

@Repository
public interface MatchJpaRepository extends JpaRepository<MatchEntity, Long> {
    public boolean existsByGameCode(String code);
}
