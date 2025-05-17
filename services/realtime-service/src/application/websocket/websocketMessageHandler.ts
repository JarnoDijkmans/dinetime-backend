
import { WebSocketServer } from "ws";
import { LeaderboardServicePort } from "../../ports/in/leaderboardServicePort";
import { LobbyManager } from "./lobbyManager";
import { WebSocketMessage } from "../../shared/types";
import { WebSocketMessageHandlerPort } from "../../ports/in/webSocketMessageHandlerPort";
import { LobbyConnection } from "../../infrastructure/lobbyConnection";

export class WebsocketMessageHandler implements WebSocketMessageHandlerPort {
    private readonly leaderboardService: LeaderboardServicePort;
    private readonly wss: WebSocketServer;
    private readonly lobbyManager: LobbyManager;

    constructor(leaderboardService: LeaderboardServicePort, wss: WebSocketServer, lobbyManager: LobbyManager ) {
        this.leaderboardService = leaderboardService;
        this.wss = wss;
        this.lobbyManager = lobbyManager;
    }

    public handleJoinLobby(conn: LobbyConnection) {
        this.lobbyManager.addConnection(conn);
        console.log("User joined lobby:", conn.lobbyCode);
        conn.send("joined_lobby", { lobbyCode: conn.lobbyCode, message: "User joined" });
    }  

    public async handleFetchLeaderboard(conn: LobbyConnection, data: WebSocketMessage) {
        if (!conn.lobbyCode) {
          conn.send("error", { message: "You must join a lobby before voting!" });
          return;
        }
      
        try {
          const leaderboardArray = await this.leaderboardService.getLeaderboard(conn.lobbyCode, 10);
          console.log("Leaderboard fetched:", leaderboardArray);
      
          const sortedLeaderboard = leaderboardArray.slice().sort((a, b) => b.score - a.score);
          const leaderboardObject = Object.fromEntries(
            sortedLeaderboard.map(({ mealId, score }) => [mealId, score])
          );
          
      
          this.lobbyManager.broadcastToLobby(conn.lobbyCode, "update_leaderboard", {
            leaderboard: leaderboardObject
          });
        } catch (error) {
          console.error("❌ Error fetching leaderboard:", error);
          conn.send("error", { message: "Failed to retrieve leaderboard" });
        }
      }
      
    
    public async handleVoteMeal(conn: LobbyConnection, data: WebSocketMessage) {
        if (!conn.lobbyCode) {
            conn.send("error", {message: "You must join a lobby before voting!" });
        return;
        }

        if (data.type !== "vote_meal") return;

        await this.leaderboardService.voteMeal(data.userId ,data.mealId, conn.lobbyCode, data.score);
    }

    public handleDisconnect(conn: LobbyConnection): void {
      this.lobbyManager.removeConnection(conn);
    }
}