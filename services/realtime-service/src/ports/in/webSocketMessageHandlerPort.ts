import { WebSocketMessage } from "../../shared/types";
import { Connection } from "../out/connection";

export interface WebSocketMessageHandlerPort {
    handleJoinLobby(conn: Connection) : void;
    handleFetchLeaderboard(conn: Connection, data: WebSocketMessage) : Promise<void>;
    handleVoteMeal(conn: Connection, data: WebSocketMessage): Promise<void>;
    handleDisconnect(conn: Connection): void;
}