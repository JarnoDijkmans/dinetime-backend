package com.dinetime.matchmaker.adapters.persistence.repository;

import com.dinetime.matchmaker.adapters.persistence.jpa.MatchJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.PoolJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.PoolMealJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.MatchEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolMealEntity;
import com.dinetime.matchmaker.domain.model.Match;
import com.dinetime.matchmaker.domain.model.Meal;
import com.dinetime.matchmaker.ports.output.MatchRepository;
import com.dinetime.matchmaker.ports.output.MealRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MatchRepositoryImpl implements MatchRepository {

    private final MatchJpaRepository matchJpaRepository;
    private final PoolJpaRepository poolJpaRepository;
    private final PoolMealJpaRepository poolMealJpaRepository;
    private final MealRepository mealRepository;

    public MatchRepositoryImpl(MatchJpaRepository matchJpaRepository, PoolJpaRepository poolJpaRepository, PoolMealJpaRepository poolMealJpaRepository, MealRepository mealRepository) {
        this.matchJpaRepository = matchJpaRepository;
        this.poolJpaRepository = poolJpaRepository;
        this.poolMealJpaRepository = poolMealJpaRepository;
        this.mealRepository = mealRepository;
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

    @Override
    @Transactional(readOnly = true)
    public Match getMatch(String gameCode) {
        MatchEntity matchEntity = matchJpaRepository.findByGameCode(gameCode)
                .orElseThrow(() -> new EntityNotFoundException("Match not found with game code: " + gameCode));

        PoolEntity poolEntity = poolJpaRepository.findTopByMatchEntityOrderByPoolNumberDesc(matchEntity)
                .orElseThrow(() -> new EntityNotFoundException("No pools found for match: " + gameCode));

        List<PoolMealEntity> poolMeals = poolMealJpaRepository.findByPool(poolEntity);

        List<Meal> meals = poolMeals.stream()
                .map(pm -> mealRepository.getMealById(pm.getMealId())) 
                .collect(Collectors.toList());

        return new Match(
            matchEntity.getId(),
            matchEntity.getGameCode(),
            poolEntity.getPoolNumber(), 
            meals
        );
    }

}
