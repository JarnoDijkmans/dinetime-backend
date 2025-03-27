import { WebSocketServer } from "ws";
import { WebSocketMessage } from "../../shared/types";
import { WebSocketMessageHandlerPort } from "../../application/ports/WebSocketMessageHandlerPort";
import { Connection } from "../../application/ports/Connection";
import { LobbyConnection } from "../../infrastructure/LobbyConnection";

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
                conn.lobbyId = data.lobbyId;
                this.messageHandler.handleJoinLobby(conn);
                case "fetch_leaderboard": this.messageHandler.handleFetchLeaderboard(conn, data);
                case "vote_meal": this.messageHandler.handleVoteMeal(conn, data);
            }

        } catch (error) {
            console.error("Error processing WebSocket message:", error);
            conn.send("error", { message: "Invalid message format" });
        }
    }
}
