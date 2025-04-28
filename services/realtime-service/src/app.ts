import "dotenv/config";
import http from "http";
import express from "express";
import "./infrastructure/flushVotesToDatabase";
import { WebSocketGateway } from "./presentation/websocket/webSocketGateway";
import { LeaderboardService } from "./application/services/leaderboardService";
import { RedisRepository } from "./infrastructure/repository/redisRepository";
import { LobbyManager } from "./application/websocket/lobbyManagerTemp";
import { WebsocketMessageHandler } from "./application/websocket/websocketMessageHandlerTemp";
import { WebSocketServer } from "ws";

const app = express();
const PORT = Number(process.env.PORT) || 5000;

const server = http.createServer(app);
const wss = new WebSocketServer({ server, path: "/leaderboard-events" });

const leaderboardRepository = new RedisRepository();
const leaderboardService = new LeaderboardService(leaderboardRepository);
const lobbyManager = new LobbyManager();
const messageHandler = new WebsocketMessageHandler(leaderboardService, wss, lobbyManager);


new WebSocketGateway(wss, messageHandler);

server.listen(PORT, '0.0.0.0', () => {
    console.log(`✅ WebSocket Leaderboard Server Running on port ${PORT}`);
});
