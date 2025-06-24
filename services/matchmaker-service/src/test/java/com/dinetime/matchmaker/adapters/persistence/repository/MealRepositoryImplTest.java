package com.dinetime.matchmaker.adapters.persistence.repository;

import com.dinetime.matchmaker.domain.model.Meal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MealRepositoryImplTest {

    private MealRepositoryImpl mealRepository;

    @BeforeEach
    void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        mealRepository = new MealRepositoryImpl(objectMapper);
    }


    @Test
    void searchMeals_shouldReturnCorrectIds() {
        List<String> result = mealRepository.searchMeals(
                List.of("Italian"),
                List.of("Tomato"),
                List.of("Cheese"),
                5
        );

        assertFalse(result.isEmpty());
        assertTrue(result.contains("meal1")); 
    }

    @Test
    void getMealById_shouldReturnCorrectMeal() {
        Meal meal = mealRepository.getMealById("meal1");

        assertEquals("meal1", meal.getId());
        assertNotNull(meal.getIngredients());
    }

    @Test
    void getMealById_shouldThrowIfNotFound() {
        assertThrows(NoSuchElementException.class, () -> {
            mealRepository.getMealById("non-existent-id");
        });
    }

    @Test
    void searchMeals_shouldExcludeMealsWithExcludedIngredients() {
        List<String> result = mealRepository.searchMeals(
                List.of(),
                List.of(),
                List.of("Bacon"),
                10
        );

        assertTrue(result.stream()
                .map(mealRepository::getMealById)
                .noneMatch(meal -> meal.getIngredients().stream().anyMatch(ing -> ing.toLowerCase().contains("bacon")))
        );
    }
}
