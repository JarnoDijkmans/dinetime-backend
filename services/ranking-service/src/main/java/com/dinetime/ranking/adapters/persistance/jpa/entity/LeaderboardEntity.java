package com.dinetime.ranking.adapters.persistance.jpa.entity;

import com.dinetime.ranking.domain.model.Leaderboard;
import com.dinetime.ranking.domain.model.LeaderboardItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "leaderboards")
@Getter
@NoArgsConstructor
public class LeaderboardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String lobbyCode;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "leaderboard_id") 
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
                this.items.add(new LeaderboardItemEntity(newItem.getMealId(), newItem.getTotalScore()));
            }
        }
    }


    public static LeaderboardEntity fromDomain(Leaderboard leaderboard) {
        return new LeaderboardEntity(
            leaderboard.getLobbyCode(),
            leaderboard.getItems().stream()
                .map(item -> new LeaderboardItemEntity(item.getMealId(), item.getTotalScore()))
                .toList()
        );
    }

    public Leaderboard toDomain() {
        return new Leaderboard(
            lobbyCode,
            items.stream()
                 .map(LeaderboardItemEntity::toDomain)
                 .toList()
        );
    }
}