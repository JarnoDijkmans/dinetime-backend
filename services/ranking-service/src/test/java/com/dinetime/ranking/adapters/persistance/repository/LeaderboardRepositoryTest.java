package com.dinetime.ranking.adapters.persistance.repository;

import com.dinetime.ranking.adapters.persistance.jpa.JpaLeaderboardRepo;
import com.dinetime.ranking.adapters.persistance.jpa.entity.LeaderboardEntity;
import com.dinetime.ranking.domain.model.Leaderboard;
import com.dinetime.ranking.domain.model.LeaderboardItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class LeaderboardRepositoryTest {

    @Mock
    private JpaLeaderboardRepo jpaLeaderboardRepo;

    private LeaderboardRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new LeaderboardRepository(jpaLeaderboardRepo);
    }

    @Test
    void shouldUpdateExistingLeaderboard() {
        // Arrange
        String lobbyCode = "lobby123";
        List<LeaderboardItem> items = List.of(new LeaderboardItem("meal-1", 10));

        Leaderboard domain = new Leaderboard(lobbyCode, items);

        LeaderboardEntity existingEntity = mock(LeaderboardEntity.class);
        when(jpaLeaderboardRepo.findByLobbyCode(lobbyCode)).thenReturn(Optional.of(existingEntity));

        // Act
        repository.save(domain);

        // Assert
        verify(existingEntity).replaceItem(items);
        verify(jpaLeaderboardRepo).save(existingEntity);
    }

    @Test
    void shouldCreateNewLeaderboardIfNotExists() {
        // Arrange
        String lobbyCode = "lobby456";
        List<LeaderboardItem> items = List.of(new LeaderboardItem("meal-2", 20));
        Leaderboard domain = new Leaderboard(lobbyCode, items);

        when(jpaLeaderboardRepo.findByLobbyCode(lobbyCode)).thenReturn(Optional.empty());

        LeaderboardEntity newEntity = mock(LeaderboardEntity.class);
        // We can also stub static method via PowerMockito or rewrite static call if needed

        try (MockedStatic<LeaderboardEntity> mockedStatic = mockStatic(LeaderboardEntity.class)) {
            mockedStatic.when(() -> LeaderboardEntity.fromDomain(domain)).thenReturn(newEntity);

            // Act
            repository.save(domain);

            // Assert
            verify(jpaLeaderboardRepo).save(newEntity);
        }
    }
}
