package com.dinetime.ranking.adapters.persistance.jpa.entity;

import com.dinetime.ranking.domain.model.LeaderboardItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leaderboard_items")
@NoArgsConstructor
@Getter
public class LeaderboardItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String mealId;
    private int totalScore;


    public LeaderboardItemEntity(String mealId, int totalScore ) {
        this.mealId = mealId;
        this.totalScore = totalScore;
    }

    public void replaceScore(int newScore) {
        if (this.totalScore != newScore) {
            this.totalScore = newScore;
        }
    }

    public static LeaderboardItemEntity fromDomain(LeaderboardItem item ) {
        return new LeaderboardItemEntity(item.getMealId(), item.getTotalScore());
    }

    public LeaderboardItem toDomain() {
        return new LeaderboardItem(mealId, totalScore);
    }
}
