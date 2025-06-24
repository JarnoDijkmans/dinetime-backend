package com.dinetime.matchmaker.ports.output;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.dinetime.matchmaker.domain.model.Match;

public interface MatchRepository {
    boolean exists(String code);
    void storeMatch(String gameCode, List<String> mealIds, LocalDateTime now);
    Match getMatch(String gameCode);
    Optional<Match> findByGameCode(String gameCode);
    void delete(String gameCode);
}
