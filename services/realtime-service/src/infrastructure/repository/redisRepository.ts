import { LeaderboardPort } from "../../ports/repo/leaderboardPort";
import { RedisClientType } from "redis";

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

    async canUserVote(lobbyId: number): Promise<boolean> {
        const timeRemaining = await this.redisClient.ttl(`leaderboard:${lobbyId}`);
    
        if (timeRemaining === -2) {
            // Leaderboard does not exist
            return false;
        } else if (timeRemaining <= 7200) {
            // Less than 2 hours (12-hour mark reached)
            return false;
        }
        return true;
    }
    

    async voteMeal(userId: number, mealId: number, lobbyId: number, newScore: number) {
        const leaderboardKey = `leaderboard:${lobbyId}`;
        const pendingVotesKey = `pending_votes:${lobbyId}`;
        const userVoteKey = `vote:${userId}:${mealId}:${lobbyId}`;
    
        // ✅ Ensure leaderboard exists before voting
        const exists = await this.redisClient.exists(leaderboardKey);
        if (!exists) {
            console.log(`⚠️ Leaderboard ${leaderboardKey} does not exist. Creating it now...`);
            await this.redisClient.expire(leaderboardKey, 50400); // 14-hour expiry
        }
    
        // ✅ Step 1: Get existing user vote
        const existingVote = await this.getUserVote(userId, mealId, lobbyId);
    
        if (existingVote !== null) {
            console.log('🔄 User previously voted:', existingVote);
            await this.redisClient.zIncrBy(leaderboardKey, newScore - existingVote, mealId.toString());
        } else {
            console.log('🆕 First time voting.');
            await this.redisClient.zIncrBy(leaderboardKey, newScore, mealId.toString());
        }
    
        // ✅ Step 2: Store the user's vote in Redis (so `getUserVote()` works)
        await this.redisClient.set(userVoteKey, newScore);
    
        // ✅ Step 3: Fetch updated full leaderboard
        const leaderboardEntries = await this.redisClient.zRangeWithScores(leaderboardKey, 0, -1);
    
        // ✅ Step 4: Update pending_votes_db to match leaderboard
        await this.redisClient.del(pendingVotesKey); // Clear old values to prevent duplicates
    
        for (const entry of leaderboardEntries) {
            await this.redisClient.hSet(pendingVotesKey, entry.value, entry.score.toString());
        }
    
        const pendingVotes = await this.redisClient.hGetAll(pendingVotesKey);
        console.log(`📢 Updated pending_votes_db for lobby ${lobbyId}:`, { ...pendingVotes });

    }
}
