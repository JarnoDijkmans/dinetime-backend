import { RedisRepository } from "../../../src/infrastructure/repository/redisRepository";
import { closeRedisConnections } from "../../../src/infrastructure/config/redisClient";
import Redis from "ioredis-mock"; 

describe("RedisRepository Integration Tests (Mocked Redis)", () => {
    let redisRepo: RedisRepository;
    let redis: any; 

    beforeAll(async () => {
        redis = new Redis(); 
        redisRepo = new RedisRepository();
        (redisRepo as any).redis = redis;
    });

    afterAll(async () => {
            await closeRedisConnections();
            jest.clearAllTimers();
    });
    

    beforeEach(async () => {
        await redis.flushdb();
    });

    test("voteMeal should update leaderboard", async () => {
        await redisRepo.voteMeal("4", "5", "6", 10);
        const score = await redis.zscore("leaderboard:6", "5");
        expect(score).toBe("10");
    });

    test("voteMeal on the same meal/lobby but different user", async () => {
        await redisRepo.voteMeal("4", "5", "6", 10);
        await redisRepo.voteMeal("5", "5", "6", 20);
        const totalScore = await redis.zscore("leaderboard:6", "5");
        expect(totalScore).toBe("30");
    });

    test("voteMeal: user updates existing vote", async () => {
        await redisRepo.voteMeal("7", "8", "9", 5);
        await redisRepo.voteMeal("7", "8", "9", 10);
        const score = await redis.zscore("leaderboard:9", "8");
        expect(score).toBe("10");
    });

    test("canUserVote should return false if leaderboard expired", async () => {
        await redisRepo.voteMeal("7", "8", "9", 5);
        await redis.expire("leaderboard:9", -2);
        const canVote = await redisRepo.canUserVote("9");
        expect(canVote).toBe(false);
    });

    test("canUserVote: returns false when leaderboard doesn't exist", async () => {
        const canVote = await redisRepo.canUserVote("999");
        expect(canVote).toBe(false);
    });
});
