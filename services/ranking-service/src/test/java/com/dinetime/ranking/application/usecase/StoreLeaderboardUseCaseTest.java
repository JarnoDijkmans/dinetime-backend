package com.dinetime.ranking.application.usecase;

import com.dinetime.ranking.adapters.web.request.LeaderboardsRequestModel;
import com.dinetime.ranking.adapters.web.request.LeaderboardsRequestModel.LeaderboardEntry;
import com.dinetime.ranking.adapters.web.response.LeaderboardResponseModel;
import com.dinetime.ranking.domain.model.Leaderboard;
import com.dinetime.ranking.ports.output.ILeaderboardRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class StoreLeaderboardUseCaseTest {

    @Mock
    private ILeaderboardRepository leaderboardRepository;

    @InjectMocks
    private StoreLeaderboardUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveLeaderboardsSuccessfully() {
        // Arrange
        LeaderboardsRequestModel.LeaderboardEntry entry =
            new LeaderboardsRequestModel.LeaderboardEntry("meal-1", 42);

        Map<String, List<LeaderboardsRequestModel.LeaderboardEntry>> map = new HashMap<>();
        map.put("ABC", List.of(entry));

        LeaderboardsRequestModel requestModel = new LeaderboardsRequestModel(map);

        // Act
        LeaderboardResponseModel result = useCase.execute(requestModel);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(200, result.getCode());
        assertEquals("Leaderboards saved successfully.", result.getMessage());
        verify(leaderboardRepository, times(1)).save(any());
    }


    @Test
    void shouldHandleExceptionDuringSave() {
        // Arrange
        LeaderboardEntry entry = new LeaderboardEntry("meal-1", 99);
        Map<String, List<LeaderboardEntry>> map = new HashMap<>();
        map.put("lobby-1", List.of(entry));

        LeaderboardsRequestModel request = new LeaderboardsRequestModel(map);

        doThrow(new RuntimeException("DB down")).when(leaderboardRepository).save(any(Leaderboard.class));

        // Act
        LeaderboardResponseModel result = useCase.execute(request);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("Failed to save"));
    }
}
