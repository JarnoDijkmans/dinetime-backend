export interface Connection {
    lobbyId: number;
    send(type: string, payload: object): void;
}
