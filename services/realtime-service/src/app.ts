import "dotenv/config";
import http from "http";
import express from "express";
import { getRedisClient } from "./infrastructure/config/redisClient"; // ✅ Use getRedisClient()
import "./infrastructure/flushVotesToDatabase";
import { LeaderboardWebSocketController } from "./presentation/controllers/leaderboardWebSocketController";
import { LeaderboardService } from "./application/leaderboardService";
import { RedisRepository } from "./infrastructure/repository/redisRepository";
import { WebSocketServer } from "ws";

const app = express();
const PORT = process.env.PORT || 5000;

const server = http.createServer(app);

// ✅ Ensure Redis is ready before starting the app
const redis = getRedisClient();
redis.ping()
    .then(() => console.log("🚀 Redis is ready!"))
    .catch((err) => console.error("❌ Redis connection failed:", err));

const leaderboardRepository = new RedisRepository();
const leaderboardService = new LeaderboardService(leaderboardRepository);

// ✅ Initialize WebSocket Server
const websocketServer = new WebSocketServer({ server, path: "/leaderboard-events" });
new LeaderboardWebSocketController(websocketServer, leaderboardService);

// ✅ Express Route for Debugging (Optional)
app.get("/", (req, res) => {
    res.send("WebSocket Leaderboard Server is Running!");
});

// ✅ Start the WebSocket & HTTP Server
server.listen(PORT, () => {
    console.log(`✅ WebSocket Leaderboard Server Running on port ${PORT}`);
});
