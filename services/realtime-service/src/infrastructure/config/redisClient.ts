import Redis from "ioredis";

let redisClientInstance: Redis | null = null;
let redisSubscriberInstance: Redis | null = null;

// ✅ Function to get Redis client (lazy-loads the instance)
export function getRedisClient(): Redis {
    if (!redisClientInstance) {
        redisClientInstance = new Redis({
            host: process.env.REDIS_HOST || "localhost",
            port: Number(process.env.REDIS_PORT) || 6379,
            retryStrategy: (times) => Math.min(times * 50, 2000),
        });

        redisClientInstance.on("connect", () => console.log("🚀 Redis Connected"));
        redisClientInstance.on("error", (err) => console.error("❌ Redis Error:", err));
    }
    return redisClientInstance;
}

// ✅ Function to get Redis Subscriber (lazy-loads the instance)
export function getRedisSubscriber(): Redis {
    if (!redisSubscriberInstance) {
        redisSubscriberInstance = new Redis({
            host: process.env.REDIS_HOST || "localhost",
            port: Number(process.env.REDIS_PORT) || 6379,
            retryStrategy: (times) => Math.min(times * 50, 2000),
        });

        redisSubscriberInstance.on("error", (err) => console.error("❌ Redis Subscriber Error:", err));
    }
    return redisSubscriberInstance;
}

// ✅ Function to properly close all Redis connections
export async function closeRedisConnections() {
    if (redisClientInstance) {
        await redisClientInstance.quit();
        redisClientInstance = null;
    }
    if (redisSubscriberInstance) {
        await redisSubscriberInstance.quit();
        redisSubscriberInstance = null;
    }
}
