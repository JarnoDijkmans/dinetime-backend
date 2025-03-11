import { WebSocketServer, WebSocket as BaseWebSocket } from "ws";
import { LeaderboardServicePort } from "../../ports/service/leaderboardServicePort";
import { WebSocketMessage } from "../../shared/types";

// ✅ Extend WebSocket to include a lobbyId property
interface WebSocketWithLobby extends BaseWebSocket {
    lobbyId?: number;
}

export class LeaderboardWebSocketController {
    private wss: WebSocketServer;
    private leaderboardService: LeaderboardServicePort;

    constructor(wss: WebSocketServer, leaderboardService: LeaderboardServicePort) {
        this.wss = wss;
        this.leaderboardService = leaderboardService;
        this.setupWebSocket();
    }

    private setupWebSocket() {
        this.wss.on("connection", async (ws: WebSocketWithLobby) => {
            console.log("✅ Leaderboard WebSocket Connected");

            ws.on("message", async (message: string) => {
                await this.handleWebSocketMessage(ws, message);
            });

            ws.on("close", () => console.log("❌ Leaderboard WebSocket Disconnected"));
        });

        this.wss.on("error", (error) => {
            console.error("❌ WebSocket Server Error:", error);
        });
    }

    private async handleWebSocketMessage(ws: WebSocketWithLobby, message: string) {
        try {
            const data: WebSocketMessage = JSON.parse(message);
            console.log("📥 Received WebSocket Message:", data);
    
            if (data.type === "join_lobby") {
                ws.lobbyId = data.lobbyId;
                console.log(`✅ User joined lobby ${ws.lobbyId}`);
            }
    
            if (data.type === "vote_meal") {
                if (!ws.lobbyId) {
                    ws.send(JSON.stringify({ type: "error", message: "You must join a lobby before voting!" }));
                    return;
                }
    
                console.log(`📝 Storing vote: User ${data.userId} voted ${data.score} for meal ${data.mealId} in lobby ${ws.lobbyId}`);
                await this.leaderboardService.voteMeal(data.userId, data.mealId, ws.lobbyId, data.score);
    
                // ✅ Broadcast updated leaderboard ONLY to users in the same lobby
                const updatedLeaderboard = await this.leaderboardService.getLeaderboard(ws.lobbyId, 10);
                this.broadcastToLobby(ws.lobbyId, {
                    type: "update_leaderboard",
                    leaderboard: updatedLeaderboard,
                });
            }
        } catch (error) {
            console.error("❌ Error processing WebSocket message:", error);
            ws.send(JSON.stringify({ type: "error", message: "Invalid message format" }));
        }
    }
    
    

    private broadcastToLobby(lobbyId: number, message: any) {
        this.wss.clients.forEach((client: WebSocketWithLobby) => {
            if (client.readyState === WebSocket.OPEN && client.lobbyId === lobbyId) {
                client.send(JSON.stringify(message));
            }
        });
    }
    
}
