package com.dinetime.matchmaker.adapters.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolEntity;

public interface PoolJpaRepository extends JpaRepository<PoolEntity, Long>{
    
}
