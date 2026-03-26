package com.fleetguard.fleet.infrastructure.messaging.publisher;

import com.fleetguard.fleet.application.ports.out.EventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOpEventPublisher implements EventPublisherPort {

    @Override
    public void publish(Object event) {
        log.info("Event publishing not yet implemented: {}", event.getClass().getSimpleName());
    }
}