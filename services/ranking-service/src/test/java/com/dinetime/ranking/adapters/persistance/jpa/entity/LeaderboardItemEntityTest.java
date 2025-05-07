package com.dinetime.ranking.adapters.persistance.jpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.dinetime.ranking.domain.model.LeaderboardItem;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
class LeaderboardItemEntityTest {

    @Test
    void fromDomain_shouldConvertToEntityCorrectly() {
        LeaderboardItem item = new LeaderboardItem("meal-1", 95);
        LeaderboardItemEntity entity = LeaderboardItemEntity.fromDomain(item);

        assertEquals("meal-1", entity.getMealId());
        assertEquals(95, entity.getTotalScore());
    }

    @Test
    void toDomain_shouldConvertToDomainCorrectly() {
        LeaderboardItemEntity entity = new LeaderboardItemEntity("meal-2", 88);
        LeaderboardItem domain = entity.toDomain();

        assertEquals("meal-2", domain.getMealId());
        assertEquals(88, domain.getTotalScore());
    }

    @Test
    void replaceScore_shouldUpdateScoreWhenDifferent() {
        LeaderboardItemEntity entity = new LeaderboardItemEntity("meal-3", 50);
        entity.replaceScore(70);

        assertEquals(70, entity.getTotalScore());
    }

    @Test
    void replaceScore_shouldNotChangeScoreWhenSame() {
        LeaderboardItemEntity entity = new LeaderboardItemEntity("meal-4", 100);
        entity.replaceScore(100);  // same score

        assertEquals(100, entity.getTotalScore()); // should remain unchanged
    }

    @Test
    void constructor_shouldSetFieldsCorrectly() {
        LeaderboardItemEntity entity = new LeaderboardItemEntity("meal-5", 60);

        assertEquals("meal-5", entity.getMealId());
        assertEquals(60, entity.getTotalScore());
    }
}
