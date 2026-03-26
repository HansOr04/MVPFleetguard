package com.fleetguard.fleet.application.ports.out;

import com.fleetguard.fleet.domain.model.mileage.MileageLog;

public interface MileageLogRepositoryPort {

    MileageLog save(MileageLog mileageLog);
}
