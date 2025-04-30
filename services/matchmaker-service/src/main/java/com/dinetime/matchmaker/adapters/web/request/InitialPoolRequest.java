package com.dinetime.matchmaker.adapters.web.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InitialPoolRequest {
    private String userId;
    private List<String> cuisine;
    private List<String> requiredIngredients;
    private List<String> excludedIngredients;
}