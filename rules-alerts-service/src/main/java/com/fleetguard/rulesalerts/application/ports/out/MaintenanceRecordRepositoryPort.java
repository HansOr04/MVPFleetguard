package com.fleetguard.rulesalerts.application.ports.out;

import com.fleetguard.rulesalerts.domain.model.maintenance.MaintenanceRecord;

public interface MaintenanceRecordRepositoryPort {

    MaintenanceRecord save(MaintenanceRecord record);
}