package com.dinetime.matchmaker.adapters.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dinetime.matchmaker.adapters.persistence.jpa.entity.MatchEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolEntity;

@Repository
public interface PoolJpaRepository extends JpaRepository<PoolEntity, Long>{
    Optional<PoolEntity> findTopByMatchEntityOrderByPoolNumberDesc(MatchEntity matchEntity);
    List<PoolEntity> findByMatchEntity(MatchEntity matchEntity);
}
