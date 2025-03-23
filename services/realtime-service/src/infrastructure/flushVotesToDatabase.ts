import axios from "axios";
import { getRedisClient } from "./config/redisClient";

export async function flushLeaderboardsToRankingService() {
    try {
        const redis = getRedisClient();

        // ✅ Check if another process is already running
        const isProcessing = await redis.get("processing_lock");
        if (isProcessing) {
            console.log("🔒 Already processing, skipping this run.");
            return;
        }

        // ✅ Set processing lock with expiration
        await redis.set("processing_lock", "true", "EX", 300);

        // ✅ Get all pending leaderboards from Redis
        const keys = await redis.keys("pending_votes:*");
        if (keys.length === 0) {
            console.log("⚠️ No leaderboards to flush.");
            await redis.del("processing_lock");
            return;
        }

        const leaderboards: Record<string, { mealId: number; totalScore: number }[]> = {};

        // ✅ Fetch data from each pending leaderboard
        for (const key of keys) {
            const lobbyId = key.split(":")[1]; // Extract the lobbyId
            const meals = await redis.hgetall(key);

            // ✅ Remove invalid/empty meal scores
            const validMeals = Object.entries(meals)
                .filter(([_, totalScore]) => totalScore.trim() !== "") // Ensure scores are valid
                .map(([mealId, totalScore]) => ({
                    mealId: parseInt(mealId),
                    totalScore: parseFloat(totalScore),
                }));

            if (validMeals.length > 0) {
                leaderboards[lobbyId] = validMeals;
            }
        }

        // ✅ Check if leaderboards are completely empty
        if (Object.keys(leaderboards).length === 0) {
            console.log("⚠️ No valid leaderboards found. Skipping HTTP request.");
            await redis.del("processing_lock");
            return; // ✅ Exit early to prevent the request
        }

        console.log("🔎 Payload sent to Ranking-Service:", JSON.stringify({ leaderboards }, null, 2));

        try {
            // ✅ Send leaderboards in batch
            await axios.post("http://localhost:5001/leaderboards/batch", { leaderboards });

            // ✅ Clear processed leaderboards
            for (const key of keys) {
                await redis.del(key);
            }

            console.log(`💾 Sent ${Object.keys(leaderboards).length} leaderboards to ranking-service.`);
        } catch (error) {
            console.error("❌ Error sending leaderboards to ranking-service:", error);
        }

        // ✅ Remove processing lock
        await redis.del("processing_lock");
    } catch (error) {
        console.error("❌ Unexpected error in leaderboard flush:", error);
    }
}

setInterval(flushLeaderboardsToRankingService, 30000)
//flushLeaderboardsToRankingService();
