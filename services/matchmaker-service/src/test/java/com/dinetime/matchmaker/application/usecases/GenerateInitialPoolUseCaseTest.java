package com.dinetime.matchmaker.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.dinetime.matchmaker.ports.output.MatchRepository;
import com.dinetime.matchmaker.ports.output.MealRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class GenerateInitialPoolUseCaseTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private GenerateInitialPoolUseCase useCase;

    @Test
    void execute_shouldReturnGameCodeAndStoreMatch() {
        // Arrange
        List<String> cuisines = List.of("Mexican");
        List<String> required = List.of("cheese");
        List<String> excluded = List.of("nuts");
        List<String> foundMealIds = List.of("m1", "m2");

        when(mealRepository.searchMeals(cuisines, required, excluded, 10)).thenReturn(foundMealIds);
        when(matchRepository.exists(any())).thenReturn(false); 

        // Act
        String result = useCase.execute(cuisines, required, excluded);

        // Assert
        assertNotNull(result);
        assertEquals(7, result.length());
        verify(mealRepository).searchMeals(cuisines, required, excluded, 10);
        verify(matchRepository).storeMatch(eq(result), eq(foundMealIds), any(LocalDateTime.class));
    }

    @Test
    void generateUniqueGameCode_shouldRetryIfCodeExists() {
        // Arrange
        when(mealRepository.searchMeals(any(), any(), any(), anyInt())).thenReturn(List.of("m1"));
        when(matchRepository.exists(any()))
            .thenReturn(true)
            .thenReturn(false);

        // Act
        String result = useCase.execute(List.of(), List.of(), List.of());

        // Assert
        assertNotNull(result);
        verify(matchRepository, times(2)).exists(any()); 
        verify(matchRepository).storeMatch(eq(result), any(), any());
    }
}
