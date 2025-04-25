package com.dinetime.matchmaker.adapters.persistence.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "matches")
@Getter
@NoArgsConstructor
public class MatchEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "game_code", nullable = false, unique = true)
    private String gameCode;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MatchEntity(String gameCode, LocalDateTime createdAt) {
        this.gameCode = gameCode;
        this.createdAt = createdAt;
    }
}