package com.dinetime.matchmaker.adapters.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinetime.matchmaker.adapters.web.request.InitialPoolRequest;
import com.dinetime.matchmaker.adapters.web.response.CreatedMatchResponse;
import com.dinetime.matchmaker.adapters.web.response.MatchResponse;
import com.dinetime.matchmaker.ports.input.MatchmakerService;

@RestController
@RequestMapping("/matchmaker")
public class MatchmakerController {

    private final MatchmakerService matchmakerService;

    public MatchmakerController(MatchmakerService matchmakerService) {
        this.matchmakerService = matchmakerService;
    }

    @PostMapping("/generate-initial-pool")
    public ResponseEntity<Object> generateInitialPool(@RequestBody InitialPoolRequest request) {
        try {
            CreatedMatchResponse response = matchmakerService.generateInitialPool(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-pool/{gameCode}")
    public ResponseEntity<Object> getCurrentPool(@PathVariable String gameCode) {
        try {
            MatchResponse response = matchmakerService.getPool(gameCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-pool/{gameCode}")
    public ResponseEntity<Object> deletePool(@PathVariable String gameCode) {
        try {
            matchmakerService.deletePool(gameCode);
            return ResponseEntity.ok("Pool deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

