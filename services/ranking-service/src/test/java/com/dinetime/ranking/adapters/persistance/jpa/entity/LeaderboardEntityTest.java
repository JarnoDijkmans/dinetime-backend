package com.dinetime.ranking.adapters.persistance.jpa.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dinetime.ranking.domain.model.Leaderboard;
import com.dinetime.ranking.domain.model.LeaderboardItem;

class LeaderboardEntityTest {

@Test
void fromDomain_shouldConvertLeaderboardToEntity() {
    // Arrange
    List<LeaderboardItem> domainItems = List.of(
        new LeaderboardItem("1", 50),
        new LeaderboardItem("2", 80)
    );
    Leaderboard domain = new Leaderboard("ABC123", domainItems);

    // Act
    LeaderboardEntity entity = LeaderboardEntity.fromDomain(domain);

    // Assert
    assertEquals("ABC123", entity.getLobbyCode());
    assertEquals(2, entity.getItems().size());
    assertEquals("1", entity.getItems().get(0).getMealId());
    assertEquals(50, entity.getItems().get(0).getTotalScore());
}

@Test
void replaceItem_shouldUpdateExistingAndAddNewItems() {
    // Arrange
    LeaderboardItemEntity existingItem = new LeaderboardItemEntity("1", 50);
    LeaderboardEntity entity = new LeaderboardEntity("ABC123", new ArrayList<>(List.of(existingItem)));

    // Act
    List<LeaderboardItem> newItems = List.of(
        new LeaderboardItem("1", 75),
        new LeaderboardItem("2", 100)  
    );
    entity.replaceItem(newItems);

    // Assert
    assertEquals(2, entity.getItems().size());

    LeaderboardItemEntity updated = entity.getItems().stream()
        .filter(i -> i.getMealId() == "1")
        .findFirst()
        .orElseThrow();
    assertEquals(75, updated.getTotalScore());

    LeaderboardItemEntity added = entity.getItems().stream()
        .filter(i -> i.getMealId() == "2")
        .findFirst()
        .orElseThrow();
    assertEquals(100, added.getTotalScore());
}

@Test
void toDomain_shouldConvertEntityToDomainModel() {
    // Arrange
    LeaderboardItemEntity item1 = new LeaderboardItemEntity("1", 40);
    LeaderboardItemEntity item2 = new LeaderboardItemEntity("2", 90);
    List<LeaderboardItemEntity> entities = List.of(item1, item2);

    LeaderboardEntity entity = new LeaderboardEntity("LOBBY42", new ArrayList<>(entities));

    // Act
    Leaderboard domain = entity.toDomain();

    // Assert
    assertEquals("LOBBY42", domain.getLobbyCode());
    assertEquals(2, domain.getItems().size());

    LeaderboardItem first = domain.getItems().get(0);
    assertEquals("1", first.getMealId());
    assertEquals(40, first.getTotalScore());

    LeaderboardItem second = domain.getItems().get(1);
    assertEquals("2", second.getMealId());
    assertEquals(90, second.getTotalScore());
}
}
