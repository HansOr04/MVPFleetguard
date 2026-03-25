package com.fleetguard.rulesalerts.infrastructure.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación de una regla de mantenimiento.
 * Sin anotaciones de validación (gestionadas en sub-issue posterior).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMaintenanceRuleRequest {

    private String name;

    private String maintenanceType;

    private Integer intervalKm;

    /** Opcional — puede ser null; en ese caso se aplicará el DEFAULT definido en BD (500 km). */
    private Integer warningThresholdKm;
}
