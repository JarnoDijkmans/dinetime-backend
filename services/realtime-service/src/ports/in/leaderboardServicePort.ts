export interface LeaderboardServicePort {
    getLeaderboard(lobbyCode: string, limit: number): Promise<Array<{ mealId: string; score: number }>>;
    voteMeal(deviceId: string, mealId: string, lobbyCode: string, score: number): Promise<void>;
}
