import { WebSocketServer } from "ws";
import { getRedisSubscriber } from "../config/redisClient";
import { getRedisClient } from "../config/redisClient"; // if needed for redis.del()

export async function createLeaderboardSubscriber(wss: WebSocketServer) {
    const redisSubscriber = getRedisSubscriber();
    const redis = getRedisClient(); 

    const channels = ["leaderboardUpdate", "lobby-events"];
    await redisSubscriber.subscribe(...channels, (err, count) => {
        if (err) {
            console.error("❌ Redis Subscription Error:", err);
            return;
        }
        console.log(`✅ Subscribed to ${count} Redis channel(s): ${channels.join(", ")}`);
    });

    redisSubscriber.on("message", async (channel, message) => {
        console.log("🎯 Received Pub/Sub message:", channel, message);
        try {
            const payload = JSON.parse(message);

            if (channel === "leaderboardUpdate") {
                const { lobbyCode } = payload;
                console.log(`🔄 Leaderboard updated for lobby ${lobbyCode}`);

                wss.clients.forEach((client) => {
                    if (client.readyState === WebSocket.OPEN) {
                        client.send(JSON.stringify({ type: "leaderboard_update", lobbyCode }));
                    }
                });
            }

            if (channel === "lobby-events" && payload.event === "lobby_deleted") {
                const { lobbyCode } = payload;
                console.log(`🧹 Cleaning up Redis data for deleted lobby: ${lobbyCode}`);

                const leaderboardKey = `leaderboard:${lobbyCode}`;
                const pendingVotesKey = `pending_votes:${lobbyCode}`;
                const dirtyLobbiesKey = "dirty_lobbies";
                const voteKeysPattern = `vote:*:*:${lobbyCode}`;

                // Delete leaderboard and pending votes
                await redis.del(leaderboardKey, pendingVotesKey);

                // Remove lobbyCode from dirtyLobbies
                await redis.srem(dirtyLobbiesKey, lobbyCode);

                // Delete all vote keys related to this lobby
                const voteKeys = await redis.keys(voteKeysPattern);
                if (voteKeys.length > 0) {
                    await redis.del(...voteKeys);
                    console.log(`✅ Deleted ${voteKeys.length} vote key(s) for lobby ${lobbyCode}`);
                }

                // Notify WebSocket clients (optional)
                wss.clients.forEach((client) => {
                    if (client.readyState === WebSocket.OPEN) {
                        client.send(JSON.stringify({ type: "lobby_deleted", lobbyCode }));
                    }
                });
            }

        } catch (error) {
            console.error("❌ Failed to handle Redis Pub/Sub message:", error);
        }
    });

    console.log("✅ Redis Pub/Sub for Leaderboard + Lobby Events Initialized");
}
