package com.dinetime.ranking.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Entity
@Table(name = "ranking")
public class RankingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long lobbyId;
    private int mealId;
    private double score;
    private long userId;

    @ManyToOne
    @JoinColumn(name = "leaderboard_id")
    private LeaderboardEntity leaderboard;

    public RankingEntity(long lobbyId, int mealId, double score, long userId) {
        this.lobbyId = lobbyId;
        this.mealId = mealId;
        this.score = score;
        this.userId = userId;
    }

    public RankingEntity(long lobbyId, int mealId, double score) {
        this.lobbyId = lobbyId;
        this.mealId = mealId;
        this.score = score;
    }
}
