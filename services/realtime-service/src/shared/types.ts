export type WebSocketMessage =
    | { type: "join_lobby"; lobbyId: number }
    | { type: "fetch_leaderboard"; lobbyId: number }
    | { type: "vote_meal"; userId: number; mealId: number; score: number };


    export interface Vote {
        userId: number;   
        mealId: number;   
        lobbyId: number;  
        score: number;
    }
    
    export interface VoteEvent {
        votes: Vote[];
    }
    