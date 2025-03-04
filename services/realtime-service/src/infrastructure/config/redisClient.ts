import { createClient, RedisClientType } from "redis";

export const redisClient: RedisClientType = createClient();

export async function connectRedis() {
    if (!redisClient.isOpen) { 
        await redisClient.connect();
        console.log("✅ Redis Connected");
    } else {
        console.log("⚠️ Redis already connected, skipping reconnection.");
    }
}
