package com.fleetguard.fleet.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
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
}
