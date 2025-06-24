package com.dinetime.matchmaker.adapters.persistence.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.dinetime.matchmaker.domain.model.Meal;
import com.dinetime.matchmaker.ports.output.MealRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class MealRepositoryImpl implements MealRepository {
    private List<Meal> meals;

    public MealRepositoryImpl(ObjectMapper objectMapper) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("meals.json")) {
            if (inputStream == null) {
                throw new IOException("meals.json not found in resources");
            }
            this.meals = objectMapper.readValue(inputStream, new TypeReference<List<Meal>>() {});
        }
    }

    @Override
    public List<String> searchMeals(List<String> cuisines, List<String> requiredIngredients, List<String> excludedIngredients, int limit) {

        List<String> safeCuisine = Optional.ofNullable(cuisines).orElse(Collections.emptyList());
        List<String> safeRequired = Optional.ofNullable(requiredIngredients).orElse(Collections.emptyList());
        List<String> safeExcluded = Optional.ofNullable(excludedIngredients).orElse(Collections.emptyList());

        return meals.stream()
            .filter(meal -> isValidMeal(meal, safeExcluded))
            .sorted(Comparator.comparingInt((Meal m) -> scoreMeal(m, safeCuisine, safeRequired)).reversed())
            .limit(limit)
            .map(Meal::getId)
            .collect(Collectors.toList());
    }


    private boolean isValidMeal(Meal meal, List<String> excludedTerms) {
        for (String ex : excludedTerms) {
            for (String ing : meal.getIngredients()) {
                if (ing.toLowerCase().contains(ex.toLowerCase())) {
                    return false;
                }
            }
        }
        return true;
    }

    private int scoreMeal(Meal meal, List<String> cuisine, List<String> requiredIngredients) {
        int score = 0;
    
        for (String cui : cuisine) {
            List<String> mealCuisines = Optional.ofNullable(meal.getCuisines())
                    .orElse(Collections.emptyList());
        
            for (String mealCui : mealCuisines) {
                if (mealCui.toLowerCase().contains(cui.toLowerCase())) {
                    score++;
                    break;
                }
            }
        }
    
        for (String req : requiredIngredients) {
            boolean found = meal.getIngredients().stream()
                .anyMatch(ing -> ing.toLowerCase().contains(req.toLowerCase()));
    
            if (found) {
                score++;
            }
        }
    
        return score;
    }

    @Override
    public Meal getMealById(String mealId) {
        return meals.stream()
            .filter(meal -> meal.getId().equals(mealId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Meal not found with id: " + mealId));
    }
}
