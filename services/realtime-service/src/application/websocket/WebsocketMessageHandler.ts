
import { WebSocketServer } from "ws";
import { LeaderboardServicePort } from "../../ports/in/leaderboardServicePort";
import { LobbyManager } from "./lobbyManager";
import { WebSocketMessage } from "../../shared/types";
import { Connection } from "../../ports/out/connection";
import { WebSocketMessageHandlerPort } from "../../ports/in/webSocketMessageHandlerPort";

export class WebsocketMessageHandler implements WebSocketMessageHandlerPort {
    private leaderboardService: LeaderboardServicePort;
    private wss: WebSocketServer;
    private lobbyManager: LobbyManager;

    constructor(leaderboardService: LeaderboardServicePort, wss: WebSocketServer, lobbyManager: LobbyManager ) {
        this.leaderboardService = leaderboardService;
        this.wss = wss;
        this.lobbyManager = lobbyManager;
    }

    public async handleJoinLobby(conn: Connection) {
        conn.send("join_lobby", { lobbyId: conn.lobbyId, message: "User joined" });
    }

    public async handleFetchLeaderboard(conn: Connection, data: WebSocketMessage) {
        if (!conn.lobbyId) {
            conn.send("error", { message: "You must join a lobby before voting!" });
            return;
        }
        try {
            const leaderboard = await this.leaderboardService.getLeaderboard(conn.lobbyId, 10);
            this.lobbyManager.broadcastToLobby(conn.lobbyId, "update_leaderboard", leaderboard)
        } catch (error) {
            console.error("❌ Error fetching leaderboard:", error);
            conn.send("error", { message: "Failed to retrieve leaderboard" });
        }
    } 
    
    public async handleVoteMeal(conn: Connection, data: WebSocketMessage) {
        if (!conn.lobbyId) {
            conn.send("error", {message: "You must join a lobby before voting!" });
        return;
        }

        if (data.type !== "vote_meal") return;

        await this.leaderboardService.voteMeal(data.userId ,data.mealId, conn.lobbyId, data.score);
    }
}