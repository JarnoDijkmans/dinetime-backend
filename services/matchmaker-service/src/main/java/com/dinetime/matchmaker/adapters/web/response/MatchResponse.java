package com.dinetime.matchmaker.adapters.web.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchResponse {
    private String gameCode;
    private int currentPoolRound;
    private List<MealResponse> meals;
}
