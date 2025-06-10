import { WebSocketServer } from "ws";
import { WebSocketMessage } from "../../shared/types";
import { WebSocketMessageHandlerPort } from "../../ports/in/webSocketMessageHandlerPort";
import { Connection } from "../../ports/out/connection";
import { LobbyConnection } from "../../infrastructure/lobbyConnection";
import jwt from "jsonwebtoken";
import { JwtClaims } from "../../shared/types/jwtClaims";

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

    this.wss.on("connection", (ws: any, req: any) => {
    ws.isAlive = true;

    console.log("New WebSocket connection attempt:", req.url);

    const url = new URL(req.url, `wss://${req.headers.host}`);
    const token = url.searchParams.get("token");
    console.log("Extracted token from query:", token ? token.slice(0, 12) + "..." : "(none)");

    if (!token) {
        console.warn("Connection closed: No token provided");
        ws.close(4001, "No token provided");
        return;
    }

    let conn: Connection; 

    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET!) as JwtClaims;

        ws.jwt = decoded;
        console.log("JWT verified. Claims:", decoded);

        conn = new LobbyConnection(ws);
        (conn as LobbyConnection).jwtClaims = decoded;

        if (decoded.role === "guest") {
            console.log(`Guest device ${decoded.device} connected.`);
        } else if (decoded.role === "user") {
            console.log(`User ${decoded.userId ?? "unknown"} connected.`);
        }
    } catch (err) {
        console.error("JWT verification failed:", err instanceof Error ? err.message : err);
        ws.close(4002, "Invalid or expired token");
        return;
    }


    ws.on('pong', () => { ws.isAlive = true; });

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
