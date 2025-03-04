export class Leaderboard {
    id: string;
    mealId: string;
    score: number;

    constructor(id: string, mealId: string, score: number) {
        this.id = id;
        this.mealId = mealId;
        this.score = score;
    }

    updateScore(newScore: number) {
        this.score += newScore;
    }
}
