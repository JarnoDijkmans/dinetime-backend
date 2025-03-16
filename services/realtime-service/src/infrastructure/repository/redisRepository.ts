import { LeaderboardPort } from "../../ports/repo/leaderboardPort";
import Redis from 'ioredis';

export class RedisRepository implements LeaderboardPort {
    private redis: Redis;

    constructor() {
        this.redis = new Redis(); // ✅ Correctly initializing Redis instance
    }

    async getLeaderboard(lobbyId: number, limit: number): Promise<{ mealId: number; score: number }[]> {
        const leaderboardKey = `leaderboard:${lobbyId}`;
    
        // ✅ Fetch top scores in descending order
        const rawData = await this.redis.zrevrangebyscore(
            leaderboardKey, "+inf", "-inf", "WITHSCORES", "LIMIT", 0, limit
        );
    
        console.log('test');
        const formattedData = [];
        for (let i = 0; i < rawData.length; i += 2) {
            formattedData.push({
                mealId: Number(rawData[i]),
                score: Number(rawData[i + 1]),
            });
        }

        console.log("✅ Leaderboard Data:", formattedData);
        return formattedData;
    }

    async getUserVote(userId: number, mealId: number, lobbyId: number): Promise<number | null> {
        const key = `vote:${userId}:${mealId}:${lobbyId}`;
        const score = await this.redis.get(key);
        return score ? parseFloat(score) : null;
    }

    async canUserVote(lobbyId: number): Promise<boolean> {
        const timeRemaining = await this.redis.ttl(`leaderboard:${lobbyId}`);
    
        if (timeRemaining === -2) {
            return false; // Leaderboard does not exist
        } else if (timeRemaining <= 7200) {
            return false; // Less than 2 hours (12-hour mark reached)
        }
        return true;
    }

    async voteMeal(userId: number, mealId: number, lobbyId: number, newScore: number) {
        const leaderboardKey = `leaderboard:${lobbyId}`;
        const pendingVotesKey = `pending_votes:${lobbyId}`;
        const userVoteKey = `vote:${userId}:${mealId}:${lobbyId}`;

        // ✅ Ensure leaderboard exists before voting
        const exists = await this.redis.exists(leaderboardKey);
        if (!exists) {
            console.log(`⚠️ Leaderboard ${leaderboardKey} does not exist. Creating it now...`);
            
            // ✅ Correct `zadd` usage in ioredis
            await this.redis.zadd(leaderboardKey, 0, '0');
            await this.redis.expire(leaderboardKey, 50400); // 14-hour expiry
        }

        // ✅ Step 1: Get existing user vote
        const existingVote = await this.getUserVote(userId, mealId, lobbyId);

        if (existingVote !== null) {
            console.log('🔄 User previously voted:', existingVote);
            await this.redis.zincrby(leaderboardKey, newScore - existingVote, mealId.toString());
        } else {
            console.log('🆕 First time voting.');
            await this.redis.zincrby(leaderboardKey, newScore, mealId.toString());
        }

        // ✅ Step 2: Store the user's vote in Redis (so `getUserVote()` works)
        await this.redis.set(userVoteKey, newScore.toString());

        // ✅ Step 3: Fetch updated total score from leaderboard
        const realTimeTotalScore = await this.redis.zscore(leaderboardKey, mealId.toString());

        console.log("realTimeTotalScore:", realTimeTotalScore);

        if (realTimeTotalScore !== null) {
            // Get current total from pending_votes_db
            const existingPendingTotal = await this.redis.hget(pendingVotesKey, mealId.toString());

            // Adjust the pending total by removing old vote and adding new one
            const updatedPendingTotal = existingPendingTotal
                ? parseFloat(existingPendingTotal) - (existingVote || 0) + newScore
                : realTimeTotalScore;

            await this.redis.hset(pendingVotesKey, mealId.toString(), updatedPendingTotal.toString());

            console.log(`📢 Updated pending_votes_db: Meal ${mealId} in lobby ${lobbyId} now has ${updatedPendingTotal}`);
        }
    }
}
