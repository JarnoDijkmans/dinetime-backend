import { LeaderboardServicePort } from "../../ports/in/leaderboardServicePort";
import { LeaderboardPort } from "../../ports/out/leaderboardPort";

export class LeaderboardService implements LeaderboardServicePort {
    private readonly leaderboardRepository: LeaderboardPort;

    constructor(leaderboardRepository: LeaderboardPort) {
        this.leaderboardRepository = leaderboardRepository;
    }

    async getLeaderboard(lobbyCode: string, limit: number) {
        return await this.leaderboardRepository.getLeaderboard(lobbyCode, limit);
    }

    async voteMeal(deviceId: string, mealId: string, lobbyCode: string, score: number) {
        return await this.leaderboardRepository.voteMeal(deviceId, mealId, lobbyCode, score);
    }
}
