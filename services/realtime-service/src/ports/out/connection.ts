export interface Connection {
    lobbyCode: string | null;
    send(type: string, payload: object): void;
}
