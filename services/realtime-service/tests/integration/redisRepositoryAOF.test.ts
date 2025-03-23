import { RedisRepository } from "../../src/infrastructure/repository/redisRepository";
import { getRedisClient, closeRedisConnections } from "../../src/infrastructure/config/redisClient";

describe("RedisRepository AOF Tests (Mocked Redis)", () => {
    let redisRepo: RedisRepository;
    let redis: any;
    const { execSync } = require("child_process");

    beforeAll(async () => {
        redis = getRedisClient(); // ✅ Use the mock Redis client
        redisRepo = new RedisRepository();
        (redisRepo as any).redis = redis;
    });

    afterAll(async () => {
        await redis.flushdb();
        await closeRedisConnections(); // ✅ Ensure Redis is properly closed
        jest.clearAllTimers();
    });

    beforeEach(async () => {
        await redis.flushdb();
    });
    

    test("AOF should persist data after Redis restart", async () => {
        jest.setTimeout(20000);

        await redisRepo.voteMeal(1, 2, 3, 15);
        await redis.bgrewriteaof();
        await waitForAOFCompletion(redis);

        execSync("docker-compose restart redis-test");
        await waitForRedisReady(redis);

        redis = getRedisClient(); 
        (redisRepo as any).redis = redis;

        const afterRestartScore = await redis.zscore("leaderboard:3", "2");
        expect(afterRestartScore).toBe("15");
    }, 20000);

    async function waitForAOFCompletion(redis: any) {
        let status = await redis.info("persistence");
        while (status.includes("aof_rewrite_in_progress:1")) {
            await new Promise(resolve => setTimeout(resolve, 1000));
            status = await redis.info("persistence");
        }
    }

    async function waitForRedisReady(redis: any) {
        while (true) {
            try {
                await redis.ping();
                return;
            } catch (e) {
                await new Promise(resolve => setTimeout(resolve, 1000));
            }
        }
    }
});
