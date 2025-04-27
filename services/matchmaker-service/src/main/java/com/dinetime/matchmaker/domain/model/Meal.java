package com.dinetime.matchmaker.domain.model;

import java.util.List;

import com.dinetime.matchmaker.adapters.persistence.mapper.CuisineDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;

@Getter
public class Meal {
    private String id;
    private String title;
    @JsonProperty("cuisine")
    @JsonDeserialize(using = CuisineDeserializer.class)
    private List<String> cuisines;
    private List<String> ingredients;
    private String instructions;
    @JsonProperty("image_url")
    private String imageUrl;
}
