package com.fleetguard.rulesalerts.infrastructure.messaging.consumer;

import com.fleetguard.rulesalerts.application.service.EvaluateMaintenanceAlertsService;
import com.fleetguard.rulesalerts.infrastructure.config.RabbitMQConfig;
import com.fleetguard.rulesalerts.infrastructure.messaging.event.MileageRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MileageRegisteredConsumer {

    private final EvaluateMaintenanceAlertsService evaluateService;

    @RabbitListener(queues = RabbitMQConfig.MILEAGE_QUEUE)
    public void onMileageRegistered(MileageRegisteredEvent event) {
        log.info("Received MileageRegistered event for vehicleId: {}", event.getVehicleId());
        try {
            evaluateService.evaluate(
                    event.getVehicleId(),
                    event.getVehicleTypeId(),
                    event.getMileage(),
                    event.getVehicleStatus()
            );
        } catch (Exception ex) {
            log.error("Error processing MileageRegistered event for vehicleId: {}: {}",
                    event.getVehicleId(), ex.getMessage(), ex);
        }
    }
}