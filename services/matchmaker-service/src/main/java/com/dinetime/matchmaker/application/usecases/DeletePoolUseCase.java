package com.dinetime.matchmaker.application.usecases;

import org.springframework.stereotype.Service;

import com.dinetime.matchmaker.ports.output.MatchRepository;

@Service
public class DeletePoolUseCase {
    private final MatchRepository matchRepository;

    public DeletePoolUseCase(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public void execute(String gameCode) {
        matchRepository.delete(gameCode);
    }
}
