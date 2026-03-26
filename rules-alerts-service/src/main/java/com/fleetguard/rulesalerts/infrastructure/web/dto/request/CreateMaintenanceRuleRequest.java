package com.fleetguard.rulesalerts.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación de una regla de mantenimiento.
 * Incluye anotaciones de validación con mensajes en español.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMaintenanceRuleRequest {

    @NotBlank(message = "El nombre de la regla es obligatorio y no puede estar vacío.")
    private String name;

    @NotBlank(message = "El tipo de mantenimiento es obligatorio y no puede estar vacío.")
    private String maintenanceType;

    @NotNull(message = "El intervalo en kilómetros es obligatorio.")
    @Min(value = 1, message = "El intervalo en kilómetros debe ser mayor a 0.")
    private Integer intervalKm;

    /**
     * Opcional — puede ser null; en ese caso se aplicará el DEFAULT definido en BD (500 km).
     * Si se envía, debe ser mayor a 0.
     */
    @Min(value = 1, message = "El umbral de alerta en kilómetros debe ser mayor a 0.")
    private Integer warningThresholdKm;
}
