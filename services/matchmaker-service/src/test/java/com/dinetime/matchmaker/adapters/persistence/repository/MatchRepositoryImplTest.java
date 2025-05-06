package com.dinetime.matchmaker.adapters.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.dinetime.matchmaker.adapters.persistence.jpa.MatchJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.PoolJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.PoolMealJpaRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.MatchEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolEntity;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.PoolMealEntity;
import com.dinetime.matchmaker.domain.model.Match;
import com.dinetime.matchmaker.domain.model.Meal;
import com.dinetime.matchmaker.ports.output.MealRepository;

import jakarta.persistence.EntityNotFoundException;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MatchRepositoryImplTest {

    @Mock private MatchJpaRepository matchJpaRepository;
    @Mock private PoolJpaRepository poolJpaRepository;
    @Mock private PoolMealJpaRepository poolMealJpaRepository;
    @Mock private MealRepository mealRepository;

    @InjectMocks
    private MatchRepositoryImpl matchRepository;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void exists_shouldReturnTrueIfExists() {
        when(matchJpaRepository.existsByGameCode("GAME123")).thenReturn(true);

        boolean exists = matchRepository.exists("GAME123");

        assertTrue(exists);
        verify(matchJpaRepository).existsByGameCode("GAME123");
    }

    @Test
    void storeMatch_shouldSaveMatchPoolAndMeals() {
        MatchEntity savedMatch = new MatchEntity("GAME123", now);
        PoolEntity savedPool = new PoolEntity(savedMatch, 1, now);
        List<String> mealIds = List.of("m1", "m2");

        when(matchJpaRepository.save(any())).thenReturn(savedMatch);
        when(poolJpaRepository.save(any())).thenReturn(savedPool);

        matchRepository.storeMatch("GAME123", mealIds, now);

        verify(matchJpaRepository).save(any(MatchEntity.class));
        verify(poolJpaRepository).save(any(PoolEntity.class));
        verify(poolMealJpaRepository, times(2)).save(any(PoolMealEntity.class));
    }

    @Test
    void getMatch_shouldReturnMatch() {
        MatchEntity matchEntity = new MatchEntity("GAME123", now);
        PoolEntity pool = new PoolEntity(matchEntity, 1, now);
        PoolMealEntity pm1 = new PoolMealEntity(pool, "m1");
        PoolMealEntity pm2 = new PoolMealEntity(pool, "m2");

        Meal meal1 = new Meal("m1", "Pizza", List.of(), List.of(), "", "");
        Meal meal2 = new Meal("m2", "Pasta", List.of(), List.of(), "", "");

        when(matchJpaRepository.findByGameCode("GAME123")).thenReturn(Optional.of(matchEntity));
        when(poolJpaRepository.findTopByMatchEntityOrderByPoolNumberDesc(matchEntity)).thenReturn(Optional.of(pool));
        when(poolMealJpaRepository.findByPool(pool)).thenReturn(List.of(pm1, pm2));
        when(mealRepository.getMealById("m1")).thenReturn(meal1);
        when(mealRepository.getMealById("m2")).thenReturn(meal2);

        Match result = matchRepository.getMatch("GAME123");

        assertEquals("GAME123", result.getGameCode());
        assertEquals(2, result.getMeals().size());
    }

    @Test
    void getMatch_shouldThrowWhenMatchNotFound() {
        when(matchJpaRepository.findByGameCode("INVALID")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            matchRepository.getMatch("INVALID");
        });

        assertTrue(ex.getMessage().contains("Match not found"));
    }

    @Test
    void getMatch_shouldThrowWhenNoPoolsFound() {
        MatchEntity matchEntity = new MatchEntity("GAME123", now);

        when(matchJpaRepository.findByGameCode("GAME123")).thenReturn(Optional.of(matchEntity));
        when(poolJpaRepository.findTopByMatchEntityOrderByPoolNumberDesc(matchEntity)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            matchRepository.getMatch("GAME123");
        });

        assertTrue(ex.getMessage().contains("No pools found"));
    }
}
