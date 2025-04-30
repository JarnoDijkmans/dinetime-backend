import { Connection } from "../ports/out/connection";
import WebSocket from "ws";

export class LobbyConnection implements Connection {
    private readonly socket: WebSocket;
    private _lobbyCode: string | null = null;
  
    constructor(socket: WebSocket) {
      this.socket = socket;
    }
  
    send(type: string, payload: object) {
      this.socket.send(JSON.stringify({ type, payload }));
    }
  
    get lobbyCode(): string {
      if (this._lobbyCode === null) {
        this.socket.send(JSON.stringify({ type: "error", message: "No active lobby. Please reconnect." }));
        throw new Error("No active lobby. Please reconnect.");
      }
      return this._lobbyCode;
    }
    
  
    set lobbyCode(id: string) {
      this._lobbyCode = id;
    }
  }
  