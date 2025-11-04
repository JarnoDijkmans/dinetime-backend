package com.dinetime.ranking.application.usecase;

import com.dinetime.ranking.adapters.web.request.LeaderboardsRequestModel;
import com.dinetime.ranking.adapters.web.response.LeaderboardResponseModel;
import com.dinetime.ranking.domain.model.Leaderboard;
import com.dinetime.ranking.domain.model.LeaderboardItem;
import com.dinetime.ranking.ports.output.ILeaderboardRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreLeaderboardUseCase {

    private final ILeaderboardRepository leaderboardRepository;

    public StoreLeaderboardUseCase(ILeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    public LeaderboardResponseModel execute(LeaderboardsRequestModel requestModel) {
        try {
            requestModel.getLeaderboards().forEach((lobbyCode, entries) -> {
                Leaderboard leaderboard = new Leaderboard(lobbyCode, convertToDomain(entries));

                leaderboardRepository.save(leaderboard);
            });

            return new LeaderboardResponseModel(true, 200, "Leaderboards saved successfully.");
        } catch (Exception e) {
            return new LeaderboardResponseModel(false, 500, "Failed to save leaderboards: " + e.getMessage());
        }
    }



    private List<LeaderboardItem> convertToDomain(List<LeaderboardsRequestModel.LeaderboardEntry> entries) {
        return entries.stream()
                .map(entry -> new LeaderboardItem(entry.getMealId(), entry.getTotalScore()))
                .toList();
    }


}
