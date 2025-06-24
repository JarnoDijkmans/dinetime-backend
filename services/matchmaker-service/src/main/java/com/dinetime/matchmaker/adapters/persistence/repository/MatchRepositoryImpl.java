package com.dinetime.matchmaker.adapters.persistence.repository;

import com.dinetime.matchmaker.adapters.persistence.jpa.MatchJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.OutboxEventRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.PoolJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.PoolMealJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.MatchEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.OutboxEventEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolMealEntity;
import com.dinetime.matchmaker.adapters.persistence.publisher.OutboxEventPublisher;
import com.dinetime.matchmaker.domain.model.Match;
import com.dinetime.matchmaker.domain.model.Meal;
import com.dinetime.matchmaker.ports.output.MatchRepository;
import com.dinetime.matchmaker.ports.output.MealRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MatchRepositoryImpl implements MatchRepository {

    private final MatchJpaRepository matchJpaRepository;
    private final PoolJpaRepository poolJpaRepository;
    private final PoolMealJpaRepository poolMealJpaRepository;
    private final MealRepository mealRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    public MatchRepositoryImpl(MatchJpaRepository matchJpaRepository, PoolJpaRepository poolJpaRepository, PoolMealJpaRepository poolMealJpaRepository, MealRepository mealRepository, OutboxEventRepository outboxEventRepository, OutboxEventPublisher outboxEventPublisher) {
        this.matchJpaRepository = matchJpaRepository;
        this.poolJpaRepository = poolJpaRepository;
        this.poolMealJpaRepository = poolMealJpaRepository;
        this.mealRepository = mealRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.outboxEventPublisher = outboxEventPublisher;
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

    @Override
    public Optional<Match> findByGameCode(String gameCode) {
        return matchJpaRepository.findByGameCode(gameCode)
                .map(matchEntity -> {
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
                });
    }

    @Transactional
    @Override
    public void delete(String gameCode) {
        MatchEntity match = matchJpaRepository.findByGameCode(gameCode)
            .orElseThrow(() -> new EntityNotFoundException("Match not found: " + gameCode));

        List<PoolEntity> pools = poolJpaRepository.findByMatchEntity(match);
        for (PoolEntity pool : pools) {
            poolMealJpaRepository.deleteByPool(pool);
        }
        poolJpaRepository.deleteAll(pools);
        matchJpaRepository.delete(match);

        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> payloadMap = Map.of(
            "event", "lobby_deleted",
            "lobbyCode", gameCode
        );

        String payload;
        try {
            payload = mapper.writeValueAsString(payloadMap);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }

        OutboxEventEntity event = OutboxEventEntity.builder()
        .eventType("lobby_deleted")
        .payload(payload)
        .createdAt(LocalDateTime.now())
        .published(false)
        .build();

        event = outboxEventRepository.save(event);

        if (!outboxEventPublisher.tryPublishNow(event)) {
            outboxEventPublisher.scheduleLazyRetry(event.getId());
        }
    }
}
