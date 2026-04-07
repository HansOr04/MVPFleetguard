package com.fleetguard.rulesalerts.infrastructure.messaging.consumer;

import com.fleetguard.rulesalerts.application.service.EvaluateMaintenanceAlertsService;
import com.fleetguard.rulesalerts.infrastructure.messaging.event.MileageRegisteredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MileageRegisteredConsumer")
class MileageRegisteredConsumerTest {

    @Mock private EvaluateMaintenanceAlertsService evaluateService;

    @InjectMocks
    private MileageRegisteredConsumer consumer;

    private MileageRegisteredEvent validEvent() {
        return new MileageRegisteredEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ACTIVE",
                45000L,
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("onMileageRegistered")
    class OnMileageRegistered {

        @Test
        @DisplayName("delegates to EvaluateMaintenanceAlertsService with correct params")
        void delegatesToService() {
            MileageRegisteredEvent event = validEvent();

            consumer.onMileageRegistered(event);

            verify(evaluateService, times(1)).evaluate(
                    event.getVehicleId(),
                    event.getVehicleTypeId(),
                    event.getMileage(),
                    event.getVehicleStatus()
            );
        }

        @Test
        @DisplayName("does not propagate exception — swallows and logs error")
        void doesNotPropagateException() {
            MileageRegisteredEvent event = validEvent();

            doThrow(new RuntimeException("DB unavailable"))
                    .when(evaluateService)
                    .evaluate(any(), any(), anyLong(), any());

            assertThatNoException().isThrownBy(() ->
                    consumer.onMileageRegistered(event));
        }

        @Test
        @DisplayName("handles INACTIVE vehicle without exception")
        void handlesInactiveVehicle() {
            MileageRegisteredEvent event = new MileageRegisteredEvent(
                    UUID.randomUUID(), UUID.randomUUID(),
                    "INACTIVE", 45000L, LocalDateTime.now());

            assertThatNoException().isThrownBy(() ->
                    consumer.onMileageRegistered(event));

            verify(evaluateService).evaluate(
                    event.getVehicleId(),
                    event.getVehicleTypeId(),
                    event.getMileage(),
                    "INACTIVE"
            );
        }
    }
}