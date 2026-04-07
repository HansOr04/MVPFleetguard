package com.fleetguard.fleet.infrastructure.messaging.publisher;

import com.fleetguard.fleet.application.ports.out.EventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Primary
@Component
public class EventPublisherStrategy implements EventPublisherPort {

    private final Map<String, EventPublisherPort> publishers;
    private final String activePublisher;

    public EventPublisherStrategy(
            Map<String, EventPublisherPort> publishers,
            @Value("${fleetguard.events.publisher:rabbitMQEventPublisher}") String activePublisher) {
        this.publishers = publishers;
        this.activePublisher = activePublisher;
    }

    @Override
    public void publish(Object event) {
        EventPublisherPort publisher = publishers.get(activePublisher);

        if (publisher == null) {
            log.warn("No publisher found for key '{}', falling back to noOp", activePublisher);
            publishers.get("noOpEventPublisher").publish(event);
            return;
        }

        log.debug("Publishing event '{}' via '{}'",
                event.getClass().getSimpleName(), activePublisher);
        publisher.publish(event);
    }
}