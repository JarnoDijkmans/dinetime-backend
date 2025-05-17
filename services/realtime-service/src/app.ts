import "dotenv/config";
import http from "http";
import express from "express";
import "./infrastructure/flushVotesToDatabase";
import { WebSocketGateway } from "./presentation/websocket/websocketGateway";
import { LeaderboardService } from "./application/services/leaderboardService";
import { RedisRepository } from "./infrastructure/repository/redisRepository";
import { LobbyManager } from "./application/websocket/lobbyManager";
import { WebsocketMessageHandler } from "./application/websocket/websocketMessageHandler";
import { WebSocketServer } from "ws";

const app = express();
const PORT = Number(process.env.PORT) || 5000;

const server = http.createServer(app);
const wss = new WebSocketServer({ server, path: "/realtime/" });

const leaderboardRepository = new RedisRepository();
const leaderboardService = new LeaderboardService(leaderboardRepository);
const lobbyManager = new LobbyManager();
const messageHandler = new WebsocketMessageHandler(leaderboardService, wss, lobbyManager);


const gateway = new WebSocketGateway(wss, messageHandler);

process.on("uncaughtException", (err) => {
  console.error("Uncaught Exception:", err);
});

process.on("unhandledRejection", (reason, promise) => {
  console.error("Unhandled Rejection at:", promise, "reason:", reason);
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`✅ WebSocket Leaderboard Server Running on port ${PORT}`);
});
