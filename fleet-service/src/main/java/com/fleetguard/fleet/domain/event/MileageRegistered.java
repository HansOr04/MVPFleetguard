package com.fleetguard.fleet.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MileageRegistered implements DomainEvent {

    private final UUID vehicleId;
    private final UUID vehicleTypeId;
    private final String vehicleStatus;
    private final long mileage;
    private final LocalDateTime occurredAt;
}