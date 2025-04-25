package com.dinetime.matchmaker.adapters.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolMealEntity;

public interface PoolMealJpaRepository extends JpaRepository<PoolMealEntity, Long> {
    
}
