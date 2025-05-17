export type WebSocketMessage =
    | { type: "join_lobby"; lobbyCode: string }
    | { type: "fetch_leaderboard"; lobbyCode: string }
    | { type: "vote_meal"; userId: string; mealId: string; score: number }
    | { type: "disconnect"; };


    export interface Vote {
        userId: string;   
        mealId: string;   
        lobbyCode: string;  
        score: number;
    }
    
    export interface VoteEvent {
        votes: Vote[];
    }
    