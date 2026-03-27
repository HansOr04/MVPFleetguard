package com.fleetguard.rulesalerts.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MileageRegisteredEvent {

    private UUID vehicleId;
    private UUID vehicleTypeId;
    private String vehicleStatus;
    private long mileage;
    private LocalDateTime occurredAt;
}
