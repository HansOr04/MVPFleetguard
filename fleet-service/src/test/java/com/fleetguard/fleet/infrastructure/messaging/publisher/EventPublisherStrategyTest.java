package com.fleetguard.fleet.infrastructure.messaging.publisher;

import com.fleetguard.fleet.application.ports.out.EventPublisherPort;
import com.fleetguard.fleet.domain.event.MileageRegistered;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@DisplayName("EventPublisherStrategy")
class EventPublisherStrategyTest {

    private EventPublisherPort rabbitPublisher;
    private EventPublisherPort noOpPublisher;
    private MileageRegistered event;

    @BeforeEach
    void setUp() {
        rabbitPublisher = mock(EventPublisherPort.class);
        noOpPublisher = mock(EventPublisherPort.class);
        event = new MileageRegistered(
                UUID.randomUUID(), UUID.randomUUID(),
                "ACTIVE", 1000L, LocalDateTime.now());
    }

    @Nested
    @DisplayName("publisher selection")
    class PublisherSelection {

        @Test
        @DisplayName("delegates to rabbitMQ publisher when configured as active")
        void delegatesToRabbit() {
            EventPublisherStrategy strategy = new EventPublisherStrategy(
                    Map.of(
                            "rabbitMQEventPublisher", rabbitPublisher,
                            "noOpEventPublisher", noOpPublisher),
                    "rabbitMQEventPublisher");

            strategy.publish(event);

            verify(rabbitPublisher, times(1)).publish(event);
            verifyNoInteractions(noOpPublisher);
        }

        @Test
        @DisplayName("delegates to noOp publisher when configured as active")
        void delegatesToNoOp() {
            EventPublisherStrategy strategy = new EventPublisherStrategy(
                    Map.of(
                            "rabbitMQEventPublisher", rabbitPublisher,
                            "noOpEventPublisher", noOpPublisher),
                    "noOpEventPublisher");

            strategy.publish(event);

            verify(noOpPublisher, times(1)).publish(event);
            verifyNoInteractions(rabbitPublisher);
        }

        @Test
        @DisplayName("falls back to noOp when configured publisher key does not exist")
        void fallsBackToNoOpWhenKeyNotFound() {
            EventPublisherStrategy strategy = new EventPublisherStrategy(
                    Map.of(
                            "rabbitMQEventPublisher", rabbitPublisher,
                            "noOpEventPublisher", noOpPublisher),
                    "unknownPublisher");

            strategy.publish(event);

            verify(noOpPublisher, times(1)).publish(event);
            verifyNoInteractions(rabbitPublisher);
        }

        @Test
        @DisplayName("does not throw when publishing — resilient by design")
        void doesNotThrow() {
            EventPublisherStrategy strategy = new EventPublisherStrategy(
                    Map.of(
                            "rabbitMQEventPublisher", rabbitPublisher,
                            "noOpEventPublisher", noOpPublisher),
                    "rabbitMQEventPublisher");

            assertThatNoException().isThrownBy(() -> strategy.publish(event));
        }
    }
}