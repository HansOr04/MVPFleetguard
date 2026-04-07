package com.fleetguard.fleet.infrastructure.messaging.listener;

import com.fleetguard.fleet.application.ports.out.EventPublisherPort;
import com.fleetguard.fleet.domain.event.MileageRegistered;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class MileageEventListener {

    private final EventPublisherPort eventPublisher;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMileageRegistered(MileageRegistered event) {
        log.info("Publishing MileageRegistered after commit for vehicleId: {}", event.getVehicleId());
        eventPublisher.publish(event);
    }
}