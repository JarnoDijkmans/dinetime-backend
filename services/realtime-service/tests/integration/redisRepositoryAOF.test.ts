import { RedisRepository } from "../../src/infrastructure/repository/redisRepository";
import Redis from "ioredis";
import { execSync } from "child_process";

const isCI = process.env.CI === "true";

if (isCI) {
    test.skip("Redis AOF integration test skipped in CI", () => {});
  } else {
    describe("RedisRepository AOF Tests", () => {
    let redisRepo: RedisRepository;
    let redis: Redis;

    beforeAll(async () => {
      jest.setTimeout(30000); // apply global timeout
      redis = new Redis({ host: "localhost", port: 6380 });
      await redis.ping(); // confirms it's running
      redisRepo = new RedisRepository(redis); // inject your instance
    });

    afterAll(async () => {
      await redis.flushdb();
      await redis.quit();
      jest.clearAllTimers();
    });

    beforeEach(async () => {
      await redis.flushdb();
    });

    test(
      "AOF should persist data after Redis restart",
      async () => {
        await redisRepo.voteMeal("1", "2", "3", 15);
        await redis.bgrewriteaof();
        await waitForAOFCompletion(redis);

        execSync("docker-compose restart redis-test");

        await waitForRedisReady();

        await redis.quit(); 

        redis = new Redis({ host: "localhost", port: 6380 });
        redisRepo = new RedisRepository(redis);

        const afterRestartScore = await redis.zscore("leaderboard:3", "2");
        expect(afterRestartScore).toBe("15");
      },
      20000
    );

    async function waitForAOFCompletion(redis: Redis) {
      let status = await redis.info("persistence");
      while (status.includes("aof_rewrite_in_progress:1")) {
        await new Promise((resolve) => setTimeout(resolve, 1000));
        status = await redis.info("persistence");
      }
    }

    async function waitForRedisReady() {
      while (true) {
        try {
          const client = new Redis({ host: "localhost", port: 6380 });
          await client.ping();
          await client.quit();
          return;
        } catch (e) {
          await new Promise((resolve) => setTimeout(resolve, 1000));
        }
      }
    }
  });
}
