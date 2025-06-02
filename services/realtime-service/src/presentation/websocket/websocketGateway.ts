import { WebSocketServer } from "ws";
import { WebSocketMessage } from "../../shared/types";
import { WebSocketMessageHandlerPort } from "../../ports/in/webSocketMessageHandlerPort";
import { Connection } from "../../ports/out/connection";
import { LobbyConnection } from "../../infrastructure/lobbyConnection";

export class WebSocketGateway {
    private readonly wss: WebSocketServer;
    private readonly messageHandler: WebSocketMessageHandlerPort;

    constructor(wss: WebSocketServer, messageHandler: WebSocketMessageHandlerPort) {
        this.wss = wss;
        this.messageHandler = messageHandler;
        this.setupWebSocket();
    }

    private setupWebSocket() {
    setInterval(() => {
        this.wss.clients.forEach((ws: any) => {
            if (ws.isAlive === false) {
                console.log("Terminating dead socket");
                return ws.terminate();
            }
            ws.isAlive = false;
            ws.ping(); 
        });
    }, 10000);

    this.wss.on("connection", (ws: any) => {
        ws.isAlive = true; 

        ws.on('pong', () => {
            console.log("Received pong from client");
            ws.isAlive = true;
        });

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
