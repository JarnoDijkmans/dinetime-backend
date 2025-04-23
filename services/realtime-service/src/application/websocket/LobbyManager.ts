import { LobbyConnection } from "../../infrastructure/lobbyConnection";

export class LobbyManager{
    private lobbies: Map<number, Set<LobbyConnection>> = new Map();

    addConnection(conn: LobbyConnection) {
        const lobbyId = conn.lobbyId;
        if (!this.lobbies.has(lobbyId)) {
            this.lobbies.set(lobbyId, new Set());
        }
        this.lobbies.get(lobbyId)!.add(conn);
    }

    removeConnection(conn: LobbyConnection) {
        const lobbyId = conn.lobbyId;
        const lobby = this.lobbies.get(lobbyId);
        if (lobby) {
            lobby.delete(conn);
            if (lobby.size === 0) {
                this.lobbies.delete(lobbyId); 
            }
        }
    }

    getConnections(lobbyId: number): LobbyConnection[] {
        return Array.from(this.lobbies.get(lobbyId) || []);
    }

    broadcastToLobby(lobbyId: number, type: string, payload: any) {
        const connections = this.getConnections(lobbyId);
        connections.forEach(conn => conn.send(type, payload));
    }
}
