package com.dinetime.matchmaker.ports.output;

import java.util.List;

public interface MealRepository {
    List<String> searchMeals(List<String> cuisine, List<String> requiredIngredients, List<String> excludedIngredients, int limit);
}
