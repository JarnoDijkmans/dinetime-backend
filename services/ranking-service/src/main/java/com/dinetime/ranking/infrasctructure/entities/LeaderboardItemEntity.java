package com.dinetime.ranking.infrasctructure.entities;

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
    private int mealId;
    private int totalScore;

    @ManyToOne
    @JoinColumn(name = "leaderboard_id", nullable = false)
    private LeaderboardEntity leaderboard;

    public LeaderboardItemEntity(int mealId, int totalScore, LeaderboardEntity leaderboard) {
        this.mealId = mealId;
        this.totalScore = totalScore;
        this.leaderboard = leaderboard;
    }

    public void replaceScore(int newScore) {
        if (this.totalScore != newScore) {
            this.totalScore = newScore;
        }
    }

    public static LeaderboardItemEntity fromDomain(LeaderboardItem item, LeaderboardEntity leaderboard) {
        return new LeaderboardItemEntity(item.getMealId(), item.getTotalScore(), leaderboard);
    }

    // ✅ Convert to Domain Model
    public LeaderboardItem toDomain() {
        return new LeaderboardItem(mealId, totalScore);
    }
}
