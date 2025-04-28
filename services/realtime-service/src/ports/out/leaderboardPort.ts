export interface LeaderboardPort {
    getLeaderboard(lobbyCode: string, limit: number): Promise<Array<{ mealId: number; score: number }>>;
    voteMeal(userId: number, mealId: number, lobbyCode: string, score: number): Promise<void>;
}
