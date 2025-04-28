export interface Connection {
    lobbyCode: string;
    send(type: string, payload: object): void;
}
