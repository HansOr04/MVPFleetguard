package com.fleetguard.fleet.infrastructure.messaging.listener;

import com.fleetguard.fleet.application.ports.out.EventPublisherPort;
import com.fleetguard.fleet.domain.event.MileageRegistered;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MileageEventListener")
class MileageEventListenerTest {

    @Mock
    private EventPublisherPort eventPublisher;

    @InjectMocks
    private MileageEventListener listener;

    private MileageRegistered event;

    @BeforeEach
    void setUp() {
        event = new MileageRegistered(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ACTIVE",
                1000L,
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("onMileageRegistered")
    class OnMileageRegistered {

        @Test
        @DisplayName("publishes event via EventPublisherPort")
        void publishesEvent() {
            listener.onMileageRegistered(event);

            verify(eventPublisher, times(1)).publish(event);
        }

        @Test
        @DisplayName("publishes exactly once per call — no duplicate publishing")
        void publishesExactlyOnce() {
            listener.onMileageRegistered(event);

            verify(eventPublisher, times(1)).publish(any());
            verifyNoMoreInteractions(eventPublisher);
        }

        @Test
        @DisplayName("propagates exception when publisher fails — allows retry")
        void propagatesExceptionForRetry() {
            doThrow(new RuntimeException("RabbitMQ unavailable"))
                    .when(eventPublisher).publish(event);

            org.assertj.core.api.Assertions.assertThatThrownBy(
                            () -> listener.onMileageRegistered(event))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("RabbitMQ unavailable");

            verify(eventPublisher, times(1)).publish(event);
        }
    }
}