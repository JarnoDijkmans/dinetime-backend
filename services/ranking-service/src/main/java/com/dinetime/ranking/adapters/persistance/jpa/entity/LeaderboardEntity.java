package com.dinetime.ranking.adapters.persistance.jpa.entity;

import com.dinetime.ranking.domain.model.Leaderboard;
import com.dinetime.ranking.domain.model.LeaderboardItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "leaderboards")
@Getter
@NoArgsConstructor
public class LeaderboardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String lobbyCode;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "leaderboard")
    private List<LeaderboardItemEntity> items;

    public LeaderboardEntity(String lobbyCode, List<LeaderboardItemEntity> items) {
        this.lobbyCode = lobbyCode;
        this.items = items;
    }

    public void replaceItem(List<LeaderboardItem> newItems) {
        for (LeaderboardItem newItem : newItems) {
            boolean updated = false;

            for (LeaderboardItemEntity existingItem : this.items) {
                if (existingItem.getMealId() == newItem.getMealId()) {
                    existingItem.replaceScore(newItem.getTotalScore());
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                this.items.add(new LeaderboardItemEntity(newItem.getMealId(), newItem.getTotalScore(), this));
            }
        }
    }


    public static LeaderboardEntity fromDomain(Leaderboard leaderboard) {
        LeaderboardEntity entity = new LeaderboardEntity(leaderboard.getLobbyCode(), new ArrayList<>());

        // ✅ Pass entity at creation

        entity.items = leaderboard.getItems().stream()
                .map(item -> new LeaderboardItemEntity(item.getMealId(), item.getTotalScore(), entity))
                .collect(Collectors.toList()); // ✅ Assign all items in one step

        return entity;
    }


    public Leaderboard toDomain() {
        List<LeaderboardItem> domainItems = items.stream()
                .map(LeaderboardItemEntity::toDomain)
                .collect(Collectors.toList());

        return new Leaderboard(lobbyCode, domainItems);
    }
}