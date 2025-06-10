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

    async canUserVote(lobbyCode: string): Promise<boolean> {
        const timeRemaining = await this.redis.ttl(`leaderboard:${lobbyCode}`);
        return timeRemaining > 7200;
    }
      

    async voteMeal(deviceId: string, mealId: string, lobbyCode: string, newScore: number) {
        const leaderboardKey = `leaderboard:${lobbyCode}`;
        const pendingVotesKey = `pending_votes:${lobbyCode}`;
        const userVoteKey = `vote:${deviceId}:${mealId}:${lobbyCode}`;
        const dirtyLobbiesKey = "dirty_lobbies";
    
        const leaderboardExists = await this.redis.exists(leaderboardKey);
        if (!leaderboardExists) {
            console.log(`⚠️ Leaderboard ${leaderboardKey} does not exist. Creating it now...`);
            await this.redis.expire(leaderboardKey, 50400); 
        }
    
        const existingVoteStr = await this.redis.get(userVoteKey);
        const existingVote = existingVoteStr !== null ? parseFloat(existingVoteStr) : null;
    
        const delta = existingVote !== null ? newScore - existingVote : newScore;
    
        await this.redis.zincrby(leaderboardKey, delta, mealId.toString());
        await this.redis.hincrbyfloat(pendingVotesKey, mealId.toString(), delta);
    
        await this.redis.set(userVoteKey, newScore.toString());
    
        await this.redis.sadd(dirtyLobbiesKey, lobbyCode);
    
        console.log(`🔄 User ${deviceId} voted ${newScore} on ${mealId} (delta: ${delta})`);
    }
    
}
