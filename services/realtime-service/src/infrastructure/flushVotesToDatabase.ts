import axios from "axios";
import { redisClient, connectRedis } from "./config/redisClient";

async function flushLeaderboardsToRankingService() {
    try {
        // ✅ Ensure Redis is connected before using it
        await connectRedis();

        // ✅ Check if another process is already running
        const isProcessing = await redisClient.get("processing_lock");
        if (isProcessing) {
            console.log("🔒 Already processing, skipping this run.");
            return;
        }

        // ✅ Set processing lock with expiration (ioredis uses separate `set` options)
        await redisClient.set("processing_lock", "true", "EX", 300);

        // ✅ Get all pending leaderboards from Redis
        const keys = await redisClient.keys("pending_votes:*");
        if (keys.length === 0) {
            console.log("⚠️ No leaderboards to flush.");
            await redisClient.del("processing_lock");
            return;
        }

        const leaderboards: Record<string, { mealId: number; totalScore: number }[]> = {};

        // ✅ Fetch data from each pending leaderboard
        for (const key of keys) {
            const lobbyId = key.split(":")[1]; // Extract the lobbyId
            const meals = await redisClient.hgetall(key);

            // ✅ Convert Redis string values to numbers properly
            leaderboards[lobbyId] = Object.entries(meals).map(([mealId, totalScore]) => ({
                mealId: parseInt(mealId),
                totalScore: parseFloat(totalScore)
            }));
        }

        if (Object.keys(leaderboards).length > 0) {
            try {
                // ✅ Send leaderboards in batch
                await axios.post("http://localhost:8080/leaderboards/batch", { leaderboards });

                // ✅ Clear processed leaderboards
                for (const key of keys) {
                    await redisClient.del(key);
                }

                console.log(`💾 Sent ${Object.keys(leaderboards).length} leaderboards to ranking-service.`);
            } catch (error) {
                console.error("❌ Error sending leaderboards to ranking-service:", error);
            }
        } else {
            console.log("⚠️ No leaderboards found.");
        }

        // ✅ Remove processing lock
        await redisClient.del("processing_lock");
    } catch (error) {
        console.error("❌ Unexpected error in leaderboard flush:", error);
    }
}

// ✅ Run every 6 hours
// setInterval(flushLeaderboardsToRankingService, 21600000); // 6 hours
// flushLeaderboardsToRankingService();
