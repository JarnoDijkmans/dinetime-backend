import { WebSocketMessage } from "../../shared/types";
import { Connection } from "../ports/Connection";

export interface WebSocketMessageHandlerPort {
    handleJoinLobby(conn: Connection) : void;
    handleFetchLeaderboard(conn: Connection, data: WebSocketMessage) : void;
    handleVoteMeal(conn: Connection, data: WebSocketMessage): void;
}