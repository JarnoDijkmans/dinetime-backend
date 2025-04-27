package com.dinetime.matchmaker.ports.output;

import java.util.List;

import com.dinetime.matchmaker.domain.model.Meal;

public interface MealRepository {
    List<String> searchMeals(List<String> cuisine, List<String> requiredIngredients, List<String> excludedIngredients, int limit);

    Meal getMealById(String mealId);
}