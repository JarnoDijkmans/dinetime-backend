import { Connection } from "../ports/out/connection";
import WebSocket from "ws";

export class LobbyConnection implements Connection {
    private socket: WebSocket;
    private _lobbyId: number | null = null;
  
    constructor(socket: WebSocket) {
      this.socket = socket;
    }
  
    send(type: string, payload: object) {
      this.socket.send(JSON.stringify({ type, ...payload }));
    }
  
    get lobbyId(): number {
      if (this._lobbyId === null) {
        throw new Error("lobbyId has not been set yet.");
      }
      return this._lobbyId;
    }
  
    set lobbyId(id: number) {
      this._lobbyId = id;
    }
  }
  