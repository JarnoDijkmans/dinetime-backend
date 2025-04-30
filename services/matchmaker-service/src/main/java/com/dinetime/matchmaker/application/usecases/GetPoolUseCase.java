package com.dinetime.matchmaker.application.usecases;

import org.springframework.stereotype.Service;

import com.dinetime.matchmaker.domain.model.Match;
import com.dinetime.matchmaker.ports.output.MatchRepository;

@Service
public class GetPoolUseCase {
    private final MatchRepository matchRepository;

    public GetPoolUseCase(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Match execute(String gameCode) {
        return matchRepository.getMatch(gameCode);
    }
}
