package com.dinetime.ranking.presentation.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LeaderboardResponseModel {
    private final boolean success;
    private final int code;
    private final String message;
}
