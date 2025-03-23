import { LeaderboardPort } from "../../ports/repo/leaderboardPort";
import { getRedisClient  } from "../config/redisClient"; 

export class RedisRepository implements LeaderboardPort {
    private redis = getRedisClient(); 

    async getLeaderboard(lobbyId: number, limit: number): Promise<{ mealId: number; score: number }[]> {
        const leaderboardKey = `leaderboard:${lobbyId}`;
    
        // ✅ Fetch top scores in descending order
        const rawData = await this.redis.zrevrangebyscore(
            leaderboardKey, "+inf", "-inf", "WITHSCORES", "LIMIT", 0, limit
        );
    
        const formattedData = [];
        for (let i = 0; i < rawData.length; i += 2) {
            formattedData.push({
                mealId: Number(rawData[i]),
                score: Number(rawData[i + 1]),
            });
        }

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
            return false; 
        } else if (timeRemaining <= 7200) {
            return false; 
        }
        return true;
    }

    async voteMeal(userId: number, mealId: number, lobbyId: number, newScore: number) {
        const leaderboardKey = `leaderboard:${lobbyId}`;
        const pendingVotesKey = `pending_votes:${lobbyId}`;
        const userVoteKey = `vote:${userId}:${mealId}:${lobbyId}`;

        const exists = await this.redis.exists(leaderboardKey);
        if (!exists) {
            console.log(`⚠️ Leaderboard ${leaderboardKey} does not exist. Creating it now...`);
            

            await this.redis.zadd(leaderboardKey, 0, '0');
            await this.redis.expire(leaderboardKey, 50400); 
        }

        const existingVote = await this.getUserVote(userId, mealId, lobbyId);

        if (existingVote !== null) {
            console.log('🔄 User previously voted:', existingVote);
            await this.redis.zincrby(leaderboardKey, newScore - existingVote, mealId.toString());
        } else {
            console.log('🆕 First time voting.');
            await this.redis.zincrby(leaderboardKey, newScore, mealId.toString());
        }

        await this.redis.set(userVoteKey, newScore.toString());

        const realTimeTotalScore = await this.redis.zscore(leaderboardKey, mealId.toString());

        console.log("realTimeTotalScore:", realTimeTotalScore);

        if (realTimeTotalScore !== null) {
            const existingPendingTotal = await this.redis.hget(pendingVotesKey, mealId.toString());

            const updatedPendingTotal = existingPendingTotal
                ? parseFloat(existingPendingTotal) - (existingVote || 0) + newScore
                : realTimeTotalScore;

            await this.redis.hset(pendingVotesKey, mealId.toString(), updatedPendingTotal.toString());

            console.log(`📢 Updated pending_votes_db: Meal ${mealId} in lobby ${lobbyId} now has ${updatedPendingTotal}`);
        }
    }
}
