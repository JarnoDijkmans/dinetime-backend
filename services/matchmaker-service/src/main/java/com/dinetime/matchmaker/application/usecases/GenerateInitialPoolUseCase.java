package com.dinetime.matchmaker.application.usecases;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dinetime.matchmaker.ports.output.MatchRepository;
import com.dinetime.matchmaker.ports.output.MealRepository;

@Service
public class GenerateInitialPoolUseCase {
    private final MealRepository mealSearchRepository;
    private final MatchRepository matchRepository;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

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
        code = SECURE_RANDOM.ints(7, 0, CHARACTERS.length())
                .mapToObj(i -> String.valueOf(CHARACTERS.charAt(i)))
                .collect(Collectors.joining());
    } while (matchRepository.exists(code));
    return code;
    }
}
