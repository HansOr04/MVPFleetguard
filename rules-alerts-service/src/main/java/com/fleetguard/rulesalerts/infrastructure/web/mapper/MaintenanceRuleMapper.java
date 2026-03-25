package com.fleetguard.rulesalerts.infrastructure.web.mapper;

import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceRuleJpaEntity;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.CreateMaintenanceRuleRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRuleResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapper de infraestructura que convierte entre DTOs y la entidad JPA.
 * No contiene lógica de negocio.
 */
@Component
public class MaintenanceRuleMapper {

    /**
     * Construye un {@link MaintenanceRuleJpaEntity} a partir del request y del
     * threshold ya resuelto (con el valor por defecto aplicado si corresponde).
     *
     * @param request           DTO de entrada validado
     * @param resolvedThreshold umbral resuelto (nunca null, nunca {@literal < 1})
     * @return entidad lista para persistir, con status "ACTIVE" e id generado
     */
    public MaintenanceRuleJpaEntity toEntity(CreateMaintenanceRuleRequest request,
                                             int resolvedThreshold) {
        LocalDateTime now = LocalDateTime.now();
        return new MaintenanceRuleJpaEntity(
                UUID.randomUUID(),
                request.getName(),
                request.getMaintenanceType(),
                request.getIntervalKm(),
                resolvedThreshold,
                "ACTIVE",
                now,
                now
        );
    }

    /**
     * Construye un {@link MaintenanceRuleResponse} a partir de la entidad persistida.
     *
     * @param entity entidad recuperada o guardada
     * @return DTO de salida con todos los campos mapeados
     */
    public MaintenanceRuleResponse toResponse(MaintenanceRuleJpaEntity entity) {
        return MaintenanceRuleResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .maintenanceType(entity.getMaintenanceType())
                .intervalKm(entity.getIntervalKm())
                .warningThresholdKm(entity.getWarningThresholdKm())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
