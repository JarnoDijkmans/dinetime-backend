package com.dinetime.matchmaker.application.usecases;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.dinetime.matchmaker.ports.output.MatchRepository;
import com.dinetime.matchmaker.ports.output.MealRepository;

@Service
public class GenerateInitialPoolUseCase {
    private final MealRepository mealSearchRepository;
    private final MatchRepository matchRepository;

    public GenerateInitialPoolUseCase(MealRepository mealSearchRepository, MatchRepository matchRepository) {
        this.mealSearchRepository = mealSearchRepository;
        this.matchRepository = matchRepository;
    }

    public String execute(List<String> cuisine, List<String> requiredIngredients, List<String> excludedIngredients) {
        List<String> mealIds = mealSearchRepository.searchMeals(cuisine, requiredIngredients, excludedIngredients, 10);

        String gameCode = generateUniqueGameCode();
        matchRepository.storeMatch(gameCode, mealIds, LocalDateTime.now());

        return gameCode;
    }

    private String generateUniqueGameCode() {
        String code;
        do {
            code = RandomStringUtils.randomAlphanumeric(7).toUpperCase();
        } while (matchRepository.exists(code));
        return code;
    }
}
