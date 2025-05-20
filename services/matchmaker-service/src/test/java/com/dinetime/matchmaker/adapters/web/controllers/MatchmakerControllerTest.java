package com.dinetime.matchmaker.adapters.web.controllers;

import com.dinetime.matchmaker.adapters.web.request.InitialPoolRequest;
import com.dinetime.matchmaker.adapters.web.response.CreatedMatchResponse;
import com.dinetime.matchmaker.adapters.web.response.MatchResponse;
import com.dinetime.matchmaker.adapters.web.response.MealResponse;
import com.dinetime.matchmaker.ports.input.MatchmakerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchmakerController.class)
@ActiveProfiles("test")
class MatchmakerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MatchmakerService matchmakerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generateInitialPool_shouldReturn200() throws Exception {
        InitialPoolRequest request = new InitialPoolRequest("123",List.of("italian"), List.of("cheese"), List.of("nuts"));
        CreatedMatchResponse response = new CreatedMatchResponse("GAME123");

        when(matchmakerService.generateInitialPool(any())).thenReturn(response);

        mockMvc.perform(post("/matchmaker/generate-initial-pool")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameCode").value("GAME123"));
    }

    @Test
    void generateInitialPool_shouldReturn400OnError() throws Exception {
        InitialPoolRequest request = new InitialPoolRequest("fail", List.of(), List.of(), List.of());

        when(matchmakerService.generateInitialPool(any())).thenThrow(new RuntimeException("Simulated error"));

        mockMvc.perform(post("/matchmaker/generate-initial-pool")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Simulated error"));
    }

    @Test
    void getCurrentPool_shouldReturn200WithMatchResponse() throws Exception {
        MatchResponse response = new MatchResponse("GAME123", 1, List.of(
            new MealResponse("m1", "Pizza", List.of("Italian"), List.of("cheese"), "Bake it", "img.jpg")
        ));

        when(matchmakerService.getPool("GAME123")).thenReturn(response);

        mockMvc.perform(get("/matchmaker/get-pool/GAME123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameCode").value("GAME123"))
                .andExpect(jsonPath("$.meals[0].title").value("Pizza"));
    }

    @Test
    void getCurrentPool_shouldReturn400OnError() throws Exception {
        when(matchmakerService.getPool("INVALID")).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/matchmaker/get-pool/INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not found"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MatchmakerService matchmakerService() {
            return Mockito.mock(MatchmakerService.class);
        }
    }
}
