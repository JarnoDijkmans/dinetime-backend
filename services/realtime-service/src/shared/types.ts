export type WebSocketMessage =
    | { type: "join_lobby"; lobbyCode: string }
    | { type: "fetch_leaderboard"; lobbyCode: string }
    | { type: "vote_meal"; userId: number; mealId: number; score: number };


    export interface Vote {
        userId: number;   
        mealId: number;   
        lobbyCode: string;  
        score: number;
    }
    
    export interface VoteEvent {
        votes: Vote[];
    }
    