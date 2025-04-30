export interface LeaderboardPort {
    getLeaderboard(lobbyCode: string, limit: number): Promise<Array<{ mealId: string; score: number }>>;
    voteMeal(userId: string, mealId: string, lobbyCode: string, score: number): Promise<void>;
}
