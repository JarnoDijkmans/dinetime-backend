import { WebSocketServer } from "ws";
import { getRedisSubscriber } from "../config/redisClient"; 

export async function createLeaderboardSubscriber(wss: WebSocketServer) {
    const redisSubscriber = getRedisSubscriber(); 

    redisSubscriber.subscribe("leaderboardUpdate", (err, count) => {
        if (err) {
            console.error("❌ Redis Subscription Error:", err);
            return;
        }
        console.log(`✅ Subscribed to ${count} Redis channels.`);
    });

    redisSubscriber.on("message", (channel, message) => {
        if (channel === "leaderboardUpdate") {
            try {
                const { lobbyCode } = JSON.parse(message);
                console.log(`🔄 Leaderboard updated for lobby ${lobbyCode}`);

                wss.clients.forEach((client) => {
                    if (client.readyState === WebSocket.OPEN) {
                        client.send(JSON.stringify({ type: "leaderboard_update", lobbyCode }));
                    }
                });
            } catch (error) {
                console.error("❌ Failed to parse leaderboard update message:", error);
            }
        }
    });

    console.log("✅ Redis Pub/Sub for Leaderboard Updates Initialized");
}
