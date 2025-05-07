package com.dinetime.matchmaker.adapters.persistence.mapper;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CuisineDeserializer extends JsonDeserializer<List<String>> {

    private final ObjectMapper mapper = new ObjectMapper(); 

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return switch (p.currentToken()) {
            case START_ARRAY -> mapper.readValue(p, new TypeReference<List<String>>() {});
            case VALUE_STRING -> List.of(p.getValueAsString());
            default -> List.of();
        };
    }
}