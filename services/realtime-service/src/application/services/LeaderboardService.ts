import { LeaderboardServicePort } from "../ports/LeaderboardServicePort";
import { LeaderboardPort } from "../ports/LeaderboardPort";

export class LeaderboardService implements LeaderboardServicePort {
    private leaderboardRepository: LeaderboardPort;

    constructor(leaderboardRepository: LeaderboardPort) {
        this.leaderboardRepository = leaderboardRepository;
    }

    async getLeaderboard(lobbyId: number, limit: number) {
        return await this.leaderboardRepository.getLeaderboard(lobbyId, limit);
    }

    async voteMeal(userId: number, mealId: number, lobbyId: number, score: number) {
        return await this.leaderboardRepository.voteMeal(userId, mealId, lobbyId, score);
    }
}
