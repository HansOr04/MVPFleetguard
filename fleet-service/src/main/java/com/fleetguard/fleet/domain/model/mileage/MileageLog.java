package com.fleetguard.fleet.domain.model.mileage;

import com.fleetguard.fleet.domain.event.MileageRegistered;
import com.fleetguard.fleet.domain.exception.MissingRecordedByException;
import com.fleetguard.fleet.domain.model.AggregateRoot;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class MileageLog extends AggregateRoot {

    private UUID id;
    private UUID vehicleId;
    private UUID vehicleTypeId;
    private String vehicleStatus;
    private Mileage mileageValue;
    private LocalDateTime recordedAt;
    private String recordedBy;
    private boolean excessiveIncrement;

    public static MileageLog create(UUID vehicleId, UUID vehicleTypeId, String vehicleStatus,
                                    Mileage mileageValue, LocalDateTime recordedAt,
                                    String recordedBy, boolean excessiveIncrement) {
        if (recordedBy == null || recordedBy.isBlank()) {
            throw new MissingRecordedByException();
        }

        MileageLog log = new MileageLog();
        log.id = UUID.randomUUID();
        log.vehicleId = vehicleId;
        log.vehicleTypeId = vehicleTypeId;
        log.vehicleStatus = vehicleStatus;
        log.mileageValue = mileageValue;
        log.recordedAt = recordedAt;
        log.recordedBy = recordedBy;
        log.excessiveIncrement = excessiveIncrement;

        log.addDomainEvent(new MileageRegistered(
                vehicleId,
                vehicleTypeId,
                vehicleStatus,
                mileageValue.getValue(),
                recordedAt
        ));

        return log;
    }
}
