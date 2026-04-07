package com.fleetguard.rulesalerts.application.ports.in;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface GetAlertsUseCase {

    List<AlertDetail> execute(String status);

    record AlertDetail(
            UUID id,
            UUID vehicleId,
            UUID vehicleTypeId,
            UUID ruleId,
            String ruleName,
            String status,
            LocalDateTime triggeredAt,
            Long dueAtKm
    ) {}
}