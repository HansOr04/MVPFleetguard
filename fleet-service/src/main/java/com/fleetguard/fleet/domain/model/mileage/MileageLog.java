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
    private Mileage previousMileage;
    private Mileage mileageValue;
    private long kmTraveled;
    private LocalDateTime recordedAt;
    private String recordedBy;
    private boolean excessiveIncrement;

    public MileageLog(UUID id, UUID vehicleId, Mileage previousMileage,
                      Mileage mileageValue, long kmTraveled,
                      LocalDateTime recordedAt, String recordedBy) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.previousMileage = previousMileage;
        this.mileageValue = mileageValue;
        this.kmTraveled = kmTraveled;
        this.recordedAt = recordedAt;
        this.recordedBy = recordedBy;
    }

    public static MileageLog create(UUID vehicleId, UUID vehicleTypeId, String vehicleStatus,
                                    Mileage previousMileage, Mileage mileageValue,
                                    LocalDateTime recordedAt, String recordedBy,
                                    boolean excessiveIncrement) {
        if (recordedBy == null || recordedBy.isBlank()) {
            throw new MissingRecordedByException();
        }

        MileageLog log = new MileageLog();
        log.id = UUID.randomUUID();
        log.vehicleId = vehicleId;
        log.vehicleTypeId = vehicleTypeId;
        log.vehicleStatus = vehicleStatus;
        log.previousMileage = previousMileage;
        log.mileageValue = mileageValue;
        log.kmTraveled = mileageValue.getValue() - previousMileage.getValue();
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