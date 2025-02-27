package com.dinetime.ranking.infrastructure.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.dinetime.ranking.application.port.IEventPublisher;

@Component
public class RedisEventPublisher implements IEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public RedisEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(Object event) {
        applicationEventPublisher.publishEvent(event);
    }
}
