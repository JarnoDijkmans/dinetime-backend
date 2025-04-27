package com.dinetime.matchmaker.adapters.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolMealEntity;

@Repository
public interface PoolMealJpaRepository extends JpaRepository<PoolMealEntity, Long> {
    List<PoolMealEntity> findByPool(PoolEntity poolEntity);
}
