import "dotenv/config";
import http from "http";
import express from "express";
import { redisClient, connectRedis } from "./infrastructure/config/redisClient";
import "./infrastructure/flushVotesToDatabase";
import { LeaderboardWebSocketController } from "./presentation/controllers/leaderboardWebSocketController";
import { LeaderboardService } from "./application/leaderboardService";
import { RedisRepository } from "./infrastructure/repository/redisRepository";
import { WebSocketServer } from "ws";

const app = express();
const PORT = process.env.PORT || 8001;

const server = http.createServer(app);

// ✅ Connect to Redis
connectRedis();

const leaderboardRepository = new RedisRepository(redisClient);
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
