package com.fleetguard.fleet.domain.factory;

import com.fleetguard.fleet.domain.model.mileage.MileageLog;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MileageLogFactory {

    public MileageLog create(Vehicle vehicle, long mileageValue,
                             String recordedBy, LocalDateTime recordedAt) {
        Mileage previousMileage = vehicle.getCurrentMileage();
        Mileage newMileage = new Mileage(mileageValue);
        boolean excessiveIncrement = newMileage.isExcessiveIncrement(previousMileage);

        return MileageLog.create(
                vehicle.getId(),
                vehicle.getVehicleType().getId(),
                vehicle.getStatus().name(),
                previousMileage,
                newMileage,
                recordedAt,
                recordedBy,
                excessiveIncrement
        );
    }
}