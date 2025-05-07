package com.dinetime.matchmaker.adapters.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@SpringBootTest
@ActiveProfiles("test")
class CuisineDeserializerTest {

    private final ObjectMapper mapper;

    public CuisineDeserializerTest() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(List.class, new CuisineDeserializer());
        mapper.registerModule(module);
    }

    @Test
    void shouldDeserializeArray() throws JsonProcessingException {
        String json = "[\"Italian\", \"Mexican\"]";
        List<String> result = mapper.readValue(json, new TypeReference<List<String>>() {});
        assertEquals(List.of("Italian", "Mexican"), result);
    }

    @Test
    void shouldDeserializeSingleString() throws JsonProcessingException {
        String json = "\"Italian\"";
        List<String> result = mapper.readValue(json, new TypeReference<List<String>>() {});
        assertEquals(List.of("Italian"), result);
    }

    @Test
    void shouldReturnEmptyListForInvalidType() throws JsonProcessingException {
        String json = "123"; 
        List<String> result = mapper.readValue(json, new TypeReference<List<String>>() {});
        assertEquals(List.of(), result);
    }
}
