import axios from "axios";
import { redisClient, connectRedis } from "./config/redisClient";
import { Vote, VoteEvent } from "../shared/types";

async function flushVotesToRankingService() {
    if (!redisClient.isOpen) {
        console.log("⏳ Waiting for Redis connection...");
        await connectRedis();  // ✅ Ensure Redis is connected before using it
    }

    const isProcessing = await redisClient.get("processing_lock");
    if (isProcessing) {
        console.log("🔒 Already processing, skipping this run.");
        return;
    }

    await redisClient.set("processing_lock", "true", { EX: 300 });

    // ✅ Fetch votes from Redis queue
    const votes = await redisClient.lRange("pending_votes_db", 0, -1);
    const parsedVotes: Vote[] = votes.map((vote) => {
        try {
            return JSON.parse(vote) as Vote;
        } catch (error) {
            console.error("❌ Error parsing vote from Redis:", error);
            return null;
        }
    }).filter((vote): vote is Vote => vote !== null); // ✅ Filters out invalid votes

    if (parsedVotes.length > 0) {
        try {
            const voteEvent: VoteEvent = { votes: parsedVotes };

            // ✅ Send votes in a batch to `ranking-service`
            await axios.post("http://localhost:8080/votes/batch", voteEvent);

            // ✅ Clear Redis queue after successful processing
            await redisClient.del("pending_votes_db");

            console.log(`💾 Sent ${parsedVotes.length} votes to ranking-service.`);
        } catch (error) {
            console.error("❌ Error sending votes to ranking-service:", error);
        }
    } else {
        console.log("⚠️ No votes to flush.");
    }

    await redisClient.del("processing_lock");
}

// ✅ Run every 6 hours
setInterval(flushVotesToRankingService, 30000);
flushVotesToRankingService();
