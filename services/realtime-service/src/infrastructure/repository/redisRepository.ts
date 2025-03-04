import { LeaderboardPort } from "../../ports/repo/leaderboardPort";
import { RedisClientType } from "redis";
import { redisClient } from "../config/redisClient";

export class RedisRepository implements LeaderboardPort {

    private redisClient: RedisClientType;

    constructor(redisClient: RedisClientType) {
        this.redisClient = redisClient;
    }

    async getLeaderboard(lobbyId: number, limit: number): Promise<{ mealId: number; score: number }[]> {
        const rawData = await this.redisClient.zRangeWithScores(`leaderboard:${lobbyId}`, 0, limit - 1, { REV: true });

        return rawData.map(entry => ({
            mealId: Number(entry.value),
            score: entry.score 
        }));
    }

    async getUserVote(userId: number, mealId: number, lobbyId: number): Promise<number | null> {
        const key = `vote:${userId}:${mealId}:${lobbyId}`;
        const score = await this.redisClient.get(key);
        return score ? parseFloat(score) : null;
    }

    async voteMeal(userId: number, mealId: number, lobbyId: number, newScore: number) {
        const existingVote = await this.getUserVote(userId, mealId, lobbyId);

        if (existingVote !== null) {
            await this.redisClient.zIncrBy(`leaderboard:${lobbyId}`, newScore - existingVote, mealId.toString()); 
        } else {
            await this.redisClient.zAdd(`leaderboard:${lobbyId}`, [{ score: newScore, value: mealId.toString() }]); 
        }

        // ✅ Store vote data properly formatted
        const voteData = {
            userId: Number(userId),
            mealId: Number(mealId),
            lobbyId: Number(lobbyId),
            score: newScore
        };

        await this.redisClient.rPush("pending_votes_db", JSON.stringify(voteData));
        console.log(`📢 Stored vote in queue: ${mealId} in lobby ${lobbyId}`);
    }
}
