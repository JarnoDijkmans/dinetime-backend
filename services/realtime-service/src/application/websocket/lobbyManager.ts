import { LobbyConnection } from "../../infrastructure/lobbyConnectionTemp";

export class LobbyManager{
    private lobbies: Map<string, Set<LobbyConnection>> = new Map();

    addConnection(conn: LobbyConnection) {
        const lobbyCode = conn.lobbyCode;
        if (!this.lobbies.has(lobbyCode)) {
            this.lobbies.set(lobbyCode, new Set());
        }
        this.lobbies.get(lobbyCode)!.add(conn);
    }

    removeConnection(conn: LobbyConnection) {
        const lobbyCode = conn.lobbyCode;
        const lobby = this.lobbies.get(lobbyCode);
        if (lobby) {
            lobby.delete(conn);
            if (lobby.size === 0) {
                this.lobbies.delete(lobbyCode); 
            }
        }
    }

    getConnections(lobbyCode: string): LobbyConnection[] {
        return Array.from(this.lobbies.get(lobbyCode) || []);
    }

    broadcastToLobby(lobbyCode: string, type: string, payload: any) {
        const connections = this.getConnections(lobbyCode);
        connections.forEach(conn => conn.send(type, payload));
    }
}
