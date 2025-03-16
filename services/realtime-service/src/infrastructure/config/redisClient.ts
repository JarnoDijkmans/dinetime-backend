import Redis from 'ioredis';

// ✅ Main Redis client (for general operations)
export const redisClient = new Redis({
    host: 'localhost',
    port: 6379,
    retryStrategy: (times) => Math.min(times * 50, 2000) // Auto-reconnect strategy
});

// ✅ Separate Redis client for Pub/Sub (to avoid conflicts)
export const redisSubscriber = new Redis({
    host: 'localhost',
    port: 6379,
    retryStrategy: (times) => Math.min(times * 50, 2000)
});

// ✅ Function to check Redis connection
export async function connectRedis() {
    try {
        await redisClient.ping();
        console.log("✅ Redis Connected");

        redisClient.on('error', (err) => console.error("❌ Redis Error:", err));
        redisSubscriber.on('error', (err) => console.error("❌ Redis Subscriber Error:", err));

    } catch (error) {
        console.error("❌ Redis Connection Failed:", error);
    }
}
