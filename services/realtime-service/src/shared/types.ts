export type WebSocketMessage =
    | { type: "join_lobby"; lobbyId: number }
    | { type: "FETCH_LEADERBOARD"; lobbyId: number }
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
    