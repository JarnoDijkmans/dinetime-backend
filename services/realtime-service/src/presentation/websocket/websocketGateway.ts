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
        setInterval(() => {
        console.log(`[WS] Clients now: ${this.wss.clients.size}`);
        }, 5000);
    }
    

    private setupWebSocket() {
        this.wss.on("connection", (ws) => {
            // console.log(`[WS] New client connected! Clients now: ${this.wss.clients.size}`);
            // const conn: Connection = new LobbyConnection(ws);
    
            (ws as any).isAlive = true;
    
            ws.on("pong", () => {
                (ws as any).isAlive = true;
            });
    
            // ws.on("message", async (message: string) => {
            //     await this.handleWebSocketMessage(conn, message);
            // });
    
            ws.on("close", (code, reason) => {
                console.log(`[WS] Client disconnected! Code: ${code}, Reason: ${reason}, Clients now: ${this.wss.clients.size}`);
            });
            
            ws.on("error", (err) => {
                console.error("[WS] Client error:", err);
            });
        });
    
            setInterval(() => {
            this.wss.clients.forEach((ws) => {
                if (!(ws as any).isAlive) return ws.terminate();
                (ws as any).isAlive = false;
                ws.ping();
            });
        }, 30000);
    

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
                case "disconnect":
                    if (conn.lobbyCode !== null) {
                        this.messageHandler.handleDisconnect(conn);
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
