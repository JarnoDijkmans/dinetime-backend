package com.dinetime.ranking.application.usecase.Leaderboard;

import com.dinetime.ranking.domain.model.Leaderboard;
import com.dinetime.ranking.domain.model.LeaderboardItem;
import com.dinetime.ranking.domain.repository.ILeaderboardRepository;
import com.dinetime.ranking.presentation.request.LeaderboardsRequestModel;
import com.dinetime.ranking.presentation.response.LeaderboardResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreLeaderboardUseCase {

    private final ILeaderboardRepository leaderboardRepository;

    public StoreLeaderboardUseCase(ILeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    public LeaderboardResponseModel execute(LeaderboardsRequestModel requestModel) {
        try {
            requestModel.getLeaderboards().forEach((lobbyId, entries) -> {
                Leaderboard leaderboard = new Leaderboard(lobbyId, convertToDomain(entries));

                // ✅ Send to repository
                leaderboardRepository.save(leaderboard);
            });

            // ✅ If everything succeeds
            return new LeaderboardResponseModel(true, 200, "Leaderboards saved successfully.");
        } catch (Exception e) {
            // ✅ Handle failure and return an error response
            return new LeaderboardResponseModel(false, 500, "Failed to save leaderboards: " + e.getMessage());
        }
    }



    private List<LeaderboardItem> convertToDomain(List<LeaderboardsRequestModel.LeaderboardEntry> entries) {
        return entries.stream()
                .map(entry -> new LeaderboardItem(entry.getMealId(), entry.getTotalScore()))
                .collect(Collectors.toList());
    }


}
