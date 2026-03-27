package com.fleetguard.rulesalerts.infrastructure.web.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterMaintenanceRequest {

    @NotNull(message = "El vehicleId es obligatorio.")
    private UUID vehicleId;

    private UUID alertId;

    private UUID ruleId;

    @NotBlank(message = "El tipo de servicio es obligatorio.")
    private String serviceType;

    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "El costo no puede ser negativo.")
    private BigDecimal cost;

    private String provider;

    private LocalDateTime performedAt;

    @NotNull(message = "El kilometraje del servicio es obligatorio.")
    @Positive(message = "El kilometraje del servicio debe ser mayor a cero.")
    private Long mileageAtService;
}