package com.fleetguard.fleet.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMileageRequest {

    @NotNull(message = "El valor del kilometraje es obligatorio")
    private Long mileageValue;

    @NotBlank(message = "El conductor es obligatorio")
    private String recordedBy;
}