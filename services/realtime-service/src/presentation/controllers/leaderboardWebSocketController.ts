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
                // ✅ Check if the user is in a lobby before allowing voting
                if (!ws.lobbyId) {
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
    

    // ✅ Broadcast leaderboard updates only to users in the correct lobby
    private broadcastLeaderboardUpdate(leaderboard: any, lobbyId: number) {
        this.wss.clients.forEach((client) => {
            const typedClient = client as WebSocketWithLobby;
            if (typedClient.readyState === BaseWebSocket.OPEN && typedClient.lobbyId === lobbyId) {
                typedClient.send(JSON.stringify({ type: "leaderboard_update", data: leaderboard }));
            }
        });
    }
}
