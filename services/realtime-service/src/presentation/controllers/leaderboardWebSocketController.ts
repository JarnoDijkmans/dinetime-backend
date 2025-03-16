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
                
                ws.send(JSON.stringify({ type: "join_lobby", lobbyId: ws.lobbyId }));
            }

            if (data.type === "FETCH_LEADERBOARD") {
                console.log(`🔍 Fetching leaderboard request received for lobby ${data.lobbyId}`);
                if (!data.lobbyId) {
                    ws.send(JSON.stringify({ type: "error", message: "You must join a lobby before voting!" }));
                    return;
                }
                try {
                    const leaderboard = await this.leaderboardService.getLeaderboard(data.lobbyId, 10);
                    console.log("✅ Sending leaderboard:", leaderboard);
                    this.broadcastToLobby(data.lobbyId, { type: "update_leaderboard", leaderboard });
                } catch (error) {
                    console.error("❌ Error fetching leaderboard:", error);
                    ws.send(JSON.stringify({ type: "error", message: "Failed to retrieve leaderboard" }));
                }
            }
    
            if (data.type === "vote_meal") {
                if (!ws.lobbyId) {
                    console.log(`🛠️ Debug: ws.lobbyId =`, ws.lobbyId);
                    ws.send(JSON.stringify({ type: "error", message: "You must join a lobby before voting!" }));
                    return;
                }
    
                console.log(`📝 Storing vote: User ${data.userId} voted ${data.score} for meal ${data.mealId} in lobby ${ws.lobbyId}`);
                await this.leaderboardService.voteMeal(data.userId, data.mealId, ws.lobbyId, data.score);
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
