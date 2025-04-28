import { WebSocketServer } from "ws";
import { WebSocketMessage } from "../../shared/types";
import { WebSocketMessageHandlerPort } from "../../ports/in/webSocketMessageHandlerPort";
import { Connection } from "../../ports/out/connection";
import { LobbyConnection } from "../../infrastructure/lobbyConnectionTemp";

export class WebSocketGateway {
    private wss: WebSocketServer;
    private messageHandler: WebSocketMessageHandlerPort;

    constructor(wss: WebSocketServer, messageHandler: WebSocketMessageHandlerPort) {
        this.wss = wss;
        this.messageHandler = messageHandler;
        this.setupWebSocket();
    }

    private setupWebSocket() {
        this.wss.on("connection", async (ws) => {
            const conn: Connection = new LobbyConnection(ws);

            ws.on("message", async (message: string) => {
                await this.handleWebSocketMessage(conn, message);
            });

            ws.on("close", () => console.log("Leaderboard WebSocket Disconnected"));
        });

        this.wss.on("error", (error) => {
            console.error("WebSocket Server Error:", error);
        });
    }

    private async handleWebSocketMessage(conn: Connection, message: string) {
        try {
            const data: WebSocketMessage = JSON.parse(message);

            switch (data.type) {
                case "join_lobby": 
                    if (data.lobbyCode) {
                        conn.lobbyCode = data.lobbyCode;
                        this.messageHandler.handleJoinLobby(conn);
                    } else {
                        conn.send("error", { message: "Lobby ID not provided." });
                    }
                    break;

                case "fetch_leaderboard": 
                    if (conn.lobbyCode !== null) {
                        this.messageHandler.handleFetchLeaderboard(conn, data);
                    } else {
                        conn.send("error", { message: "No active lobby. Please reconnect." });
                    }
                    break;

                case "vote_meal": 
                    if (conn.lobbyCode !== null) {
                        this.messageHandler.handleVoteMeal(conn, data);
                    } else {
                        conn.send("error", { message: "No active lobby. Please reconnect." });
                    }
                    break;

                default:
                    conn.send("error", { message: "Unknown message type." });
            }

        } catch (error) {
            console.error("Error processing WebSocket message:", error);
            conn.send("error", { message: "Invalid message format" });
        }
    }
}
