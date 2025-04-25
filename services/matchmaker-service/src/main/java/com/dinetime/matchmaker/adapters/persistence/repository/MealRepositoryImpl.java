package com.dinetime.matchmaker.adapters.persistence.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
    public List<String> searchMeals(List<String> cuisine, List<String> requiredIngredients, List<String> excludedIngredients, int limit) {
        List<String> safeCuisine = Optional.ofNullable(cuisine).orElse(Collections.emptyList());
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
            if (meal.getCuisine().toLowerCase().contains(cui.toLowerCase())) {
                score++;
            }
        }
        for (String req : requiredIngredients) {
            for (String ing : meal.getIngredients()) {
                if (ing.toLowerCase().contains(req.toLowerCase())) {
                    score++;
                    break;
                }
            }
        }
        return score;
    }
}
