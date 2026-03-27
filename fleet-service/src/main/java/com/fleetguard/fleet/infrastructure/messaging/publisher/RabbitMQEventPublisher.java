package com.fleetguard.fleet.infrastructure.messaging.publisher;

import com.fleetguard.fleet.application.ports.out.EventPublisherPort;
import com.fleetguard.fleet.domain.event.MileageRegistered;
import com.fleetguard.fleet.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(Object event) {
        if (event instanceof MileageRegistered mileageRegistered) {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.MILEAGE_ROUTING_KEY,
                    event
            );
            log.info("MileageRegistered event published for vehicleId: {}", mileageRegistered.getVehicleId());
        } else {
            log.warn("Unhandled event type: {}", event.getClass().getSimpleName());
        }
    }
}
