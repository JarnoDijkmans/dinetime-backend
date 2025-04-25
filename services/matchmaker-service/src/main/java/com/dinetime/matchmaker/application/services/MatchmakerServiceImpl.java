package com.dinetime.matchmaker.application.services;

import org.springframework.stereotype.Service;

import com.dinetime.matchmaker.adapters.web.request.InitialPoolRequest;
import com.dinetime.matchmaker.adapters.web.response.CreatedMatchResponse;
import com.dinetime.matchmaker.application.usecases.GenerateInitialPoolUseCase;
import com.dinetime.matchmaker.ports.input.MatchmakerService;

@Service
public class MatchmakerServiceImpl implements MatchmakerService {

    private final GenerateInitialPoolUseCase generateInitialPoolUseCase;

    public MatchmakerServiceImpl(GenerateInitialPoolUseCase generateInitialPoolUseCase) {
        this.generateInitialPoolUseCase = generateInitialPoolUseCase;
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
}
