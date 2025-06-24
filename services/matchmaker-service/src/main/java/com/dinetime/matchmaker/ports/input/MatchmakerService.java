package com.dinetime.matchmaker.ports.input;

import com.dinetime.matchmaker.adapters.web.request.InitialPoolRequest;
import com.dinetime.matchmaker.adapters.web.response.CreatedMatchResponse;
import com.dinetime.matchmaker.adapters.web.response.MatchResponse;

public interface MatchmakerService {
    CreatedMatchResponse generateInitialPool(InitialPoolRequest request);
    MatchResponse getPool(String gameCode);
    void deletePool(String gameCode);
}
