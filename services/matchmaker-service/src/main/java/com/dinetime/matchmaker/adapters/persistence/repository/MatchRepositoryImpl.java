package com.dinetime.matchmaker.adapters.persistence.repository;

import com.dinetime.matchmaker.adapters.persistence.jpa.MatchJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.PoolJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.PoolMealJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.MatchEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolMealEntity;
import com.dinetime.matchmaker.ports.output.MatchRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MatchRepositoryImpl implements MatchRepository {

    private final MatchJpaRepository matchJpaRepository;
    private final PoolJpaRepository poolJpaRepository;
    private final PoolMealJpaRepository poolMealJpaRepository;

    public MatchRepositoryImpl(MatchJpaRepository matchJpaRepository, PoolJpaRepository poolJpaRepository, PoolMealJpaRepository poolMealJpaRepository) {
        this.matchJpaRepository = matchJpaRepository;
        this.poolJpaRepository = poolJpaRepository;
        this.poolMealJpaRepository = poolMealJpaRepository;
    }

    @Override
    public boolean exists(String code) {
        return matchJpaRepository.existsByGameCode(code);
    }

    @Override
    @Transactional
    public void storeMatch(String gameCode, List<String> mealIds, LocalDateTime now) {
        MatchEntity match = new MatchEntity(gameCode, now);
        match = matchJpaRepository.save(match);

        PoolEntity pool = new PoolEntity(match, 1, now);
        pool = poolJpaRepository.save(pool);

        for (String mealId : mealIds) {
            PoolMealEntity meal = new PoolMealEntity(pool, mealId);
            poolMealJpaRepository.save(meal);
        }
    }
}
