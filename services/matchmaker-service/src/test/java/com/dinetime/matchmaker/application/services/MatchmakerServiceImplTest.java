package com.dinetime.matchmaker.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dinetime.matchmaker.adapters.web.request.InitialPoolRequest;
import com.dinetime.matchmaker.adapters.web.response.CreatedMatchResponse;
import com.dinetime.matchmaker.adapters.web.response.MatchResponse;
import com.dinetime.matchmaker.application.usecases.GenerateInitialPoolUseCase;
import com.dinetime.matchmaker.application.usecases.GetPoolUseCase;
import com.dinetime.matchmaker.domain.model.Match;
import com.dinetime.matchmaker.domain.model.Meal;

@ExtendWith(MockitoExtension.class)
class MatchmakerServiceImplTest {

    @Mock
    private GenerateInitialPoolUseCase generateInitialPoolUseCase;

    @Mock
    private GetPoolUseCase getPoolUseCase;

    @InjectMocks
    private MatchmakerServiceImpl service;

    @Test
    void generateInitialPool_shouldReturnCreatedMatchResponse() {
        // Arrange
        InitialPoolRequest request = new InitialPoolRequest("123", List.of("Italian"), List.of("cheese"), List.of("nuts"));
        when(generateInitialPoolUseCase.execute(List.of("Italian"), List.of("cheese"), List.of("nuts")))
            .thenReturn("ABC123");

        // Act
        CreatedMatchResponse response = service.generateInitialPool(request);

        // Assert
        assertEquals("ABC123", response.getGameCode());
        verify(generateInitialPoolUseCase).execute(List.of("Italian"), List.of("cheese"), List.of("nuts"));
    }

    @Test
    void getPool_shouldReturnMatchResponseWithMeals() {
        // Arrange
        Meal meal = new Meal("m1", "Pizza", List.of("Italian"), List.of("cheese", "tomato"), "Bake it", "img.jpg");
        Match match = new Match(123,"GAME123", 1, List.of(meal));
        when(getPoolUseCase.execute("GAME123")).thenReturn(match);

        // Act
        MatchResponse response = service.getPool("GAME123");

        // Assert
        assertEquals("GAME123", response.getGameCode());
        assertEquals(1, response.getCurrentPoolRound());
        assertEquals(1, response.getMeals().size());
        assertEquals("Pizza", response.getMeals().get(0).getTitle());

        verify(getPoolUseCase).execute("GAME123");
    }
}
