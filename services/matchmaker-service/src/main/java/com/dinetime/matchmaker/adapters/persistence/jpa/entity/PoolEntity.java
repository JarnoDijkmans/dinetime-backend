package com.dinetime.matchmaker.adapters.persistence.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "pool",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"match_id", "pool_number"})
    }
)
@Getter
@NoArgsConstructor
public class PoolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private MatchEntity matchEntity;
    @Column(name = "pool_number", nullable = false, unique = true)
    private int poolNumber;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PoolEntity(MatchEntity entity ,int poolNumber, LocalDateTime createdAt) {
        this.matchEntity = entity;
        this.poolNumber = poolNumber;
        this.createdAt = createdAt;
    }
}
