import { LeaderboardPort } from "../../ports/out/leaderboardPort";
import { getRedisClient  } from "../config/redisClient"; 

export class RedisRepository implements LeaderboardPort {
    private readonly redis: any;

    constructor(redisClient?: any) {
      this.redis = redisClient ?? getRedisClient();
    }

    async getLeaderboard(lobbyCode: string, limit: number): Promise<{ mealId: string; score: number }[]> {
        const leaderboardKey = `leaderboard:${lobbyCode}`;
    
        const rawData = await this.redis.zrevrangebyscore(
            leaderboardKey, "+inf", "-inf", "WITHSCORES", "LIMIT", 0, limit
        );
    
        const formattedData = [];
        for (let i = 0; i < rawData.length; i += 2) {
            formattedData.push({
                mealId: String(rawData[i]),
                score: Number(rawData[i + 1]),
            });
        }

        return formattedData;
    }

    async getUserVote(userId: string, mealId: string, lobbyCode: string): Promise<number | null> {
        const key = `vote:${userId}:${mealId}:${lobbyCode}`;
        const score = await this.redis.get(key);
        return score ? parseFloat(score) : null;
    }

    async canUserVote(lobbyCode: string): Promise<boolean> {
        const timeRemaining = await this.redis.ttl(`leaderboard:${lobbyCode}`);
        return timeRemaining > 7200;
      }
      

    async voteMeal(userId: string, mealId: string, lobbyCode: string, newScore: number) {
        const leaderboardKey = `leaderboard:${lobbyCode}`;
        const pendingVotesKey = `pending_votes:${lobbyCode}`;
        const userVoteKey = `vote:${userId}:${mealId}:${lobbyCode}`;

        const exists = await this.redis.exists(leaderboardKey);
        if (!exists) {
            console.log(`⚠️ Leaderboard ${leaderboardKey} does not exist. Creating it now...`);
            await this.redis.expire(leaderboardKey, 50400); 
        }

        const existingVote = await this.getUserVote(userId, mealId, lobbyCode);

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
                ? parseFloat(existingPendingTotal) - (existingVote ?? 0) + newScore
                : realTimeTotalScore;

            await this.redis.hset(pendingVotesKey, mealId.toString(), updatedPendingTotal.toString());

            console.log(`📢 Updated pending_votes_db: Meal ${mealId} in lobby ${lobbyCode} now has ${updatedPendingTotal}`);
        }
    }
}
