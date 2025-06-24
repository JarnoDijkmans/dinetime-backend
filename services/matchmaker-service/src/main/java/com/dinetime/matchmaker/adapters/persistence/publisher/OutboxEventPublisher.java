package com.dinetime.matchmaker.adapters.persistence.publisher;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.dinetime.matchmaker.adapters.persistence.jpa.OutboxEventRepository;
import com.dinetime.matchmaker.adapters.persistence.jpa.entity.OutboxEventEntity;

@Service
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxRepo;
    private final StringRedisTemplate redisTemplate;

    public OutboxEventPublisher(OutboxEventRepository outboxRepo, StringRedisTemplate redisTemplate) {
        this.outboxRepo = outboxRepo;
        this.redisTemplate = redisTemplate;
    }

    public boolean tryPublishNow(OutboxEventEntity event) {
        try {
            redisTemplate.convertAndSend("lobby-events", event.getPayload());
            event.setPublished(true);
            System.out.println("🔔 Published to Redis: lobby-events: " + event.getPayload());
            outboxRepo.save(event);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Immediate publish failed: " + e.getMessage());
            return false;
        }
    }

    public void scheduleLazyRetry(Long eventId) {
        new Thread(() -> {
            int attempts = 0;
            while (attempts < 10) { 
                try {
                    Thread.sleep(10000);
                    OutboxEventEntity event = outboxRepo.findById(eventId).orElse(null);
                    if (event != null && !event.isPublished()) {
                        if (tryPublishNow(event)) {
                            System.out.println("✅ Retry succeeded for event " + eventId);
                            break;
                        }
                    } else {
                        break; 
                    }
                } catch (Exception ex) {
                    System.err.println("Retry error: " + ex.getMessage());
                }
                attempts++;
            }
        }).start();
    }
}
