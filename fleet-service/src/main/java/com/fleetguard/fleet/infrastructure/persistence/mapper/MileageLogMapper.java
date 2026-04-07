package com.fleetguard.fleet.infrastructure.persistence.mapper;

import com.fleetguard.fleet.domain.model.mileage.MileageLog;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.infrastructure.persistence.entity.MileageLogJpaEntity;

public class MileageLogMapper {

    public static MileageLogJpaEntity toJpaEntity(MileageLog mileageLog) {
        return new MileageLogJpaEntity(
                mileageLog.getId(),
                mileageLog.getVehicleId(),
                mileageLog.getPreviousMileage().getValue(),
                mileageLog.getMileageValue().getValue(),
                mileageLog.getKmTraveled(),
                mileageLog.getRecordedAt(),
                mileageLog.getRecordedBy()
        );
    }

    public static MileageLog toDomain(MileageLogJpaEntity entity) {
        return MileageLog.reconstitute(
                entity.getId(),
                entity.getVehicleId(),
                new Mileage(entity.getPreviousMileage()),
                new Mileage(entity.getMileageValue()),
                entity.getKmTraveled(),
                entity.getRecordedAt(),
                entity.getRecordedBy()
        );
    }
}