package com.dinetime.ranking.adapters.web.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LeaderboardResponseModel {
    private final boolean success;
    private final int code;
    private final String message;
}
