export interface LeaderboardPort {
    getLeaderboard(lobbyId: number, limit: number): Promise<Array<{ mealId: number; score: number }>>;
    voteMeal(userId: number, mealId: number, lobbyId: number, score: number): Promise<void>;
}
