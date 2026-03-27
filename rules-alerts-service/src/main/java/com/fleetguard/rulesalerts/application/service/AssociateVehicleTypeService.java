package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.RuleVehicleTypeAssocRepositoryPort;
import com.fleetguard.rulesalerts.domain.exception.DuplicateAssociationException;
import com.fleetguard.rulesalerts.domain.exception.MaintenanceRuleNotFoundException;
import com.fleetguard.rulesalerts.domain.model.association.RuleVehicleTypeAssoc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssociateVehicleTypeService implements AssociateVehicleTypeUseCase {

    private final MaintenanceRuleRepositoryPort maintenanceRuleRepositoryPort;
    private final RuleVehicleTypeAssocRepositoryPort assocRepositoryPort;

    @Override
    public AssociateVehicleTypeResponse execute(AssociateVehicleTypeCommand command) {

        if (!maintenanceRuleRepositoryPort.existsById(command.ruleId())) {
            throw new MaintenanceRuleNotFoundException(
                    "Regla de mantenimiento no encontrada con id: " + command.ruleId());
        }

        if (assocRepositoryPort.existsByRuleIdAndVehicleTypeId(command.ruleId(), command.vehicleTypeId())) {
            throw new DuplicateAssociationException(
                    "La regla ya está asociada a ese tipo de vehículo");
        }

        RuleVehicleTypeAssoc assoc = new RuleVehicleTypeAssoc(
                UUID.randomUUID(),
                command.ruleId(),
                command.vehicleTypeId(),
                LocalDateTime.now()
        );

        RuleVehicleTypeAssoc saved = assocRepositoryPort.save(assoc);

        return new AssociateVehicleTypeResponse(
                saved.getId(),
                saved.getRuleId(),
                saved.getVehicleTypeId(),
                saved.getCreatedAt()
        );
    }
}
