package com.dinetime.ranking.application.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.dinetime.ranking.application.event.LeaderboardExpiredEvent;
import com.dinetime.ranking.application.port.ILeaderboardService;
import com.dinetime.ranking.application.usecase.Leaderboard.GetLeaderboardUseCase;
import com.dinetime.ranking.application.usecase.Leaderboard.LeaderboardExpiredEventUseCase;
import com.dinetime.ranking.presentation.response.LeaderboardResponseModel;

@Service
public class LeaderboardService implements ILeaderboardService {

    private final GetLeaderboardUseCase getLeaderboardUseCase;
    private final LeaderboardExpiredEventUseCase leaderboardExpiredEventUseCase;

    public LeaderboardService(GetLeaderboardUseCase getLeaderboardUseCase, 
                              LeaderboardExpiredEventUseCase leaderboardExpiredEventUseCase) {
        this.getLeaderboardUseCase = getLeaderboardUseCase;
        this.leaderboardExpiredEventUseCase = leaderboardExpiredEventUseCase;
    }

    @Override
    public LeaderboardResponseModel getLeaderboard(long id) {
        return getLeaderboardUseCase.execute(id);
    }

    @EventListener
    public void handleExpiredLeaderboard(LeaderboardExpiredEvent event) {
        leaderboardExpiredEventUseCase.execute(event);
    }
}

