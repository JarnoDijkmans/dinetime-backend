package com.dinetime.ranking.application.port;

public interface IEventPublisher {
    void publish(Object event);
}
