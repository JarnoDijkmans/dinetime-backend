package com.dinetime.matchmaker.adapters.persistence.mapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CuisineDeserializer extends JsonDeserializer<List<String>> {
    @Override
      public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.currentToken();

        if (token == JsonToken.START_ARRAY) {
            return p.readValueAs(new TypeReference<List<String>>() {});
        } else if (token == JsonToken.VALUE_STRING) {
            String singleCuisine = p.getValueAsString();
            return Collections.singletonList(singleCuisine);
        } else {
            return Collections.emptyList(); 
        }
    }
    
}
