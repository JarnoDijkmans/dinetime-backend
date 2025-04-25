package com.dinetime.matchmaker.domain.model;

import java.util.List;

import lombok.Getter;

@Getter
public class Meal {
    private String id;
    private String name;
    private String cuisine;
    private List<String> ingredients;
}
