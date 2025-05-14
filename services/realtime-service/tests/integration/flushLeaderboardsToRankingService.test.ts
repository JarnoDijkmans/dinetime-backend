import nock from "nock";
import Redis from "ioredis-mock";



const mockRedis = new (require("ioredis-mock"))();

jest.mock("../../src/infrastructure/config/redisClient", () => ({
    getRedisClient: jest.fn(() => mockRedis),
    closeRedisConnections: jest.fn(() => Promise.resolve()) 
}));

import { flushLeaderboardsToRankingService } from "../../src/infrastructure/flushVotesToDatabase";
import { getRedisClient, closeRedisConnections } from "../../src/infrastructure/config/redisClient";


describe("flushLeaderboardsToRankingService Integration Test (Mocked Redis)", () => {
    let redis: any; 

    beforeEach(async () => {
        redis = getRedisClient(); 
        await redis.flushdb(); 
        nock.cleanAll();
    });

    afterAll(async () => {
        await closeRedisConnections(); 
        nock.cleanAll();
        jest.clearAllTimers();
    });

    test("should flush leaderboards to ranking service successfully", async () => {
        await redis.hset("pending_votes:100", "1", "20");
        await redis.hset("pending_votes:100", "2", "40");
        await redis.hset("pending_votes:200", "3", "15");
    
        // 🔧 Add dirty lobbies
        await redis.sadd("dirty_lobbies", "100");
        await redis.sadd("dirty_lobbies", "200");
    
        const scope = nock("http://localhost:5001")
            .post("/leaderboards/batch", {
                leaderboards: {
                    "100": [
                        { mealId: "1", totalScore: 20.0 },
                        { mealId: "2", totalScore: 40.0 }
                    ],
                    "200": [
                        { mealId: "3", totalScore: 15.0 }
                    ]
                }
            })
            .reply(200, { success: true });
    
        await flushLeaderboardsToRankingService();
    
        expect(scope.isDone()).toBe(true);
    
        const keysAfter = await redis.keys("pending_votes:*");
        expect(keysAfter.length).toBe(0);
    
        const dirtyLobbies = await redis.smembers("dirty_lobbies");
        expect(dirtyLobbies).not.toContain("100");
        expect(dirtyLobbies).not.toContain("200");
    });
    

    test("✅ Should do nothing if no leaderboards exist", async () => {
        const scope = nock("http://localhost:5001")
            .post("/leaderboards/batch")
            .reply(200, { success: true });

        await flushLeaderboardsToRankingService();

        expect(scope.isDone()).toBe(false); 
    });

    test("✅ Should skip processing when locked", async () => {
        await redis.set("processing_lock", "true");

        const scope = nock("http://localhost:5001")
            .post("/leaderboards/batch")
            .reply(200, { success: true });

        await flushLeaderboardsToRankingService();

        expect(scope.isDone()).toBe(false); 

        const lockExists = await redis.get("processing_lock");
        expect(lockExists).toBe("true"); 
    });

    test("✅ Should not delete Redis data if ranking service fails", async () => {
        await redis.hset("pending_votes:300", "4", "50");
        await redis.sadd("dirty_lobbies", "300"); 
    
        const scope = nock("http://localhost:5001")
            .post("/leaderboards/batch")
            .reply(500, { success: false });
    
        await flushLeaderboardsToRankingService();
    
        expect(scope.isDone()).toBe(true); 
    
        const keysAfter = await redis.keys("pending_votes:*");
        expect(keysAfter).toContain("pending_votes:300"); 
    });
    

    test("✅ Should handle an empty payload properly", async () => {
        await redis.hset("pending_votes:400", "invalid", "");

        const scope = nock("http://localhost:5001")
            .post("/leaderboards/batch")
            .reply(200, { success: true });

        await flushLeaderboardsToRankingService();

        expect(scope.isDone()).toBe(false); 
    });
});
