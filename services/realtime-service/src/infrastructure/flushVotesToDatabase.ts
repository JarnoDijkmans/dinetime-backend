import axios from "axios";
import { getRedisClient } from "./config/redisClient";

export async function flushLeaderboardsToRankingService() {
    try {
        const redis = getRedisClient();

        const isProcessing = await redis.get("processing_lock");
        if (isProcessing) {
            console.log("Already processing, skipping this run.");
            return;
        }

        await redis.set("processing_lock", "true", "EX", 300);

        const dirtyLobbies = await redis.smembers("dirty_lobbies");
        if (dirtyLobbies.length === 0) {
            console.log("No dirty lobbies to flush.");
            await redis.del("processing_lock");
            return;
        }

        const leaderboards: Record<string, { mealId: string; totalScore: number }[]> = {};

        for (const lobbyCode of dirtyLobbies) {
            const pendingVotesKey = `pending_votes:${lobbyCode}`;
            const meals = await redis.hgetall(pendingVotesKey);

            const validMeals = Object.entries(meals)
                .filter(([_, totalScore]) => totalScore.trim() !== "") 
                .map(([mealId, totalScore]) => ({
                    mealId,
                    totalScore: parseFloat(totalScore),
                }));

            if (validMeals.length > 0) {
                leaderboards[lobbyCode] = validMeals;
            }
        }

        if (Object.keys(leaderboards).length === 0) {
            await redis.del("processing_lock");
            return; 
        }

        try {
            await axios.post("http://localhost:5001/leaderboards/batch", { leaderboards });

            for (const lobbyCode of Object.keys(leaderboards)) {
                await redis.del(`pending_votes:${lobbyCode}`);
                await redis.srem("dirty_lobbies", lobbyCode);
            }

            console.log(`✅ Sent ${Object.keys(leaderboards).length} leaderboards to ranking-service.`);
        } catch (error) {
            console.error("❌ Error sending leaderboards to ranking-service:", error);
        }

        await redis.del("processing_lock");
    } catch (error) {
        console.error("❌ Unexpected error in leaderboard flush:", error);
    }
}

//setInterval(flushLeaderboardsToRankingService, 30000)
//flushLeaderboardsToRankingService();
