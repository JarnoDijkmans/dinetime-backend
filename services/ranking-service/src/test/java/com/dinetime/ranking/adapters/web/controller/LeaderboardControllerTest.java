package com.dinetime.ranking.adapters.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


import com.dinetime.ranking.adapters.web.request.LeaderboardsRequestModel;
import com.dinetime.ranking.adapters.web.response.LeaderboardResponseModel;
import com.dinetime.ranking.application.service.LeaderboardService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@WebMvcTest(LeaderboardController.class)
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LeaderboardService leaderboardService;

    @Test
    void batchSaveVotes_shouldReturn200AndSuccessMessage() throws Exception {
        LeaderboardsRequestModel requestModel = new LeaderboardsRequestModel(
                Map.of("ABC", List.of(new LeaderboardsRequestModel.LeaderboardEntry("meal1", 10),
                                      new LeaderboardsRequestModel.LeaderboardEntry("meal2", 20)))
        );
        LeaderboardResponseModel mockResponse = new LeaderboardResponseModel(true, 200, "Success");

        when(leaderboardService.batchSaveLeaderboard(any()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/ranking/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestModel)))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LeaderboardService leaderboardService() {
            return mock(LeaderboardService.class);
        }
    }
}
