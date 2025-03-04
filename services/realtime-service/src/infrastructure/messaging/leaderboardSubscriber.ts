import { WebSocketServer } from "ws";
import { redisClient } from "../config/redisClient";

export async function createLeaderboardSubscriber(wss: WebSocketServer) {
    redisClient.subscribe("leaderboardUpdate", async (message) => {
        const { lobbyId } = JSON.parse(message);
        console.log(`🔄 Leaderboard updated for lobby ${lobbyId}`);

        wss.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(JSON.stringify({ type: "leaderboard_update", lobbyId }));
            }
        });
    });

    console.log("✅ Redis Pub/Sub for Leaderboard Updates Initialized");
}