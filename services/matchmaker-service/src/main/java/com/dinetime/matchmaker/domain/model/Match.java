package com.dinetime.matchmaker.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Match {
    private long id;
    private String gameCode;
    private int poolNumber;
    private List<Meal> meals;
}