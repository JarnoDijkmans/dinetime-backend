package com.dinetime.ranking.presentation.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConflictRequestModel {
    private String userId;
    private String lobbyId;
    private String diet;
    private List <String> allergies;
}
