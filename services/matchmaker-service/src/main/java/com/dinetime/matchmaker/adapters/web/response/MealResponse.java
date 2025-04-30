package com.dinetime.matchmaker.adapters.web.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MealResponse {
        private String id;
        private String title;
        private List<String> cuisines;
        private List<String> ingredients;
        private String instructions;
        private String imageUrl;
    }