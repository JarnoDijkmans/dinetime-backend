import { Connection } from "../ports/out/connection";
import WebSocket from "ws";

export class LobbyConnection implements Connection {
    private socket: WebSocket;
    private _lobbyCode: string | null = null;
  
    constructor(socket: WebSocket) {
      this.socket = socket;
    }
  
    send(type: string, payload: object) {
      this.socket.send(JSON.stringify({ type, ...payload }));
    }
  
    get lobbyCode(): string {
      if (this._lobbyCode === null) {
        throw new Error("lobbyId has not been set yet.");
      }
      return this._lobbyCode;
    }
  
    set lobbyCode(id: string) {
      this._lobbyCode = id;
    }
  }
  