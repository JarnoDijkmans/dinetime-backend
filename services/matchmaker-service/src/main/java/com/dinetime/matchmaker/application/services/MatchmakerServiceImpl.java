package com.dinetime.matchmaker.application.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dinetime.matchmaker.adapters.web.request.InitialPoolRequest;
import com.dinetime.matchmaker.adapters.web.response.CreatedMatchResponse;
import com.dinetime.matchmaker.adapters.web.response.MatchResponse;
import com.dinetime.matchmaker.adapters.web.response.MealResponse;
import com.dinetime.matchmaker.application.usecases.GenerateInitialPoolUseCase;
import com.dinetime.matchmaker.application.usecases.GetPoolUseCase;
import com.dinetime.matchmaker.domain.model.Match;
import com.dinetime.matchmaker.ports.input.MatchmakerService;

@Service
public class MatchmakerServiceImpl implements MatchmakerService {
    private final GetPoolUseCase getPoolUseCase;
    private final GenerateInitialPoolUseCase generateInitialPoolUseCase;

    public MatchmakerServiceImpl(GenerateInitialPoolUseCase generateInitialPoolUseCase, GetPoolUseCase getPoolUseCase) {
        this.generateInitialPoolUseCase = generateInitialPoolUseCase;
        this.getPoolUseCase = getPoolUseCase;
    }

    @Override
    public CreatedMatchResponse generateInitialPool(InitialPoolRequest request) {
        String gameCode = generateInitialPoolUseCase.execute(
            request.getCuisine(),
            request.getRequiredIngredients(),
            request.getExcludedIngredients()
        );
        return new CreatedMatchResponse(gameCode);
    }

    @Override
public MatchResponse getPool(String gameCode) {
    Match match = getPoolUseCase.execute(gameCode);

    List<MealResponse> mealResponses = match.getMeals().stream()
        .map(meal -> new MealResponse(
            meal.getId(),
            meal.getTitle(),
            meal.getCuisines(),
            meal.getIngredients(),
            meal.getInstructions(),
            meal.getImageUrl()
        ))
        .collect(Collectors.toList());

    return new MatchResponse(match.getGameCode(), match.getPoolNumber(), mealResponses);
}

}
