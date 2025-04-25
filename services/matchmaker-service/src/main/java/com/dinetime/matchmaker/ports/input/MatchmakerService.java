package com.dinetime.matchmaker.ports.input;

import com.dinetime.matchmaker.adapters.web.request.InitialPoolRequest;
import com.dinetime.matchmaker.adapters.web.response.CreatedMatchResponse;

public interface MatchmakerService {
    CreatedMatchResponse generateInitialPool(InitialPoolRequest request);
}
