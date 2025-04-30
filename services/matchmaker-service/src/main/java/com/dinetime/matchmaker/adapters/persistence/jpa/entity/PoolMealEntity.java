package com.dinetime.matchmaker.adapters.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pool_meals")
@Getter
@NoArgsConstructor
public class PoolMealEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id", nullable = false)
    private PoolEntity pool;

    @Column(name = "meal_id", nullable = false)
    private String mealId;

    public PoolMealEntity(PoolEntity pool, String mealId) {
        this.pool = pool;
        this.mealId = mealId;
    }
}
