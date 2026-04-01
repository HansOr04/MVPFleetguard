package com.fleetguard.fleet.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MileageResponse {
    private UUID mileageLogId;
    private UUID vehicleId;
    private String plate;
    private long mileageValue;
    private long currentMileage;
    private String recordedBy;
    private LocalDateTime recordedAt;
    private boolean excessiveIncrement;
    private UUID alertId;
}
