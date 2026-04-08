package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleRepositoryPort;
import com.fleetguard.rulesalerts.domain.exception.DuplicateAssociationException;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRuleConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateMaintenanceRuleService implements CreateMaintenanceRuleUseCase {

    private final MaintenanceRuleRepositoryPort repositoryPort;

    @Override
    public CreateMaintenanceRuleResponse execute(CreateMaintenanceRuleCommand command) {

        if (repositoryPort.existsByName(command.name())) {
            throw new DuplicateAssociationException(
                    "Ya existe una regla de mantenimiento con el nombre: " + command.name());
        }

        int resolvedThreshold = (command.warningThresholdKm() == null || command.warningThresholdKm() < 1)
                ? MaintenanceRuleConstants.DEFAULT_WARNING_THRESHOLD_KM
                : command.warningThresholdKm();

        LocalDateTime now = LocalDateTime.now();

        MaintenanceRule rule = new MaintenanceRule(
                UUID.randomUUID(),
                command.name(),
                command.maintenanceType(),
                command.intervalKm(),
                resolvedThreshold,
                "ACTIVE",
                now,
                now
        );

        MaintenanceRule saved = repositoryPort.save(rule);

        return new CreateMaintenanceRuleResponse(
                saved.getId(),
                saved.getName(),
                saved.getMaintenanceType(),
                saved.getIntervalKm(),
                saved.getWarningThresholdKm(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }
}