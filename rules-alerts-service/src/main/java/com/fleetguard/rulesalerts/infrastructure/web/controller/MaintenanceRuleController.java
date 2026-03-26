package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fleetguard.rulesalerts.application.service.MaintenanceRuleService;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.CreateMaintenanceRuleRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRuleResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para el recurso de reglas de mantenimiento.
 * Solo orquesta: recibe el request, delega al servicio y devuelve la respuesta.
 */
@RestController
@RequestMapping("/api/maintenance-rules")
public class MaintenanceRuleController {

    private final MaintenanceRuleService maintenanceRuleService;

    public MaintenanceRuleController(MaintenanceRuleService maintenanceRuleService) {
        this.maintenanceRuleService = maintenanceRuleService;
    }

    /**
     * POST /api/maintenance-rules
     * Crea una nueva regla de mantenimiento con status "ACTIVE".
     *
     * @param request DTO de entrada validado
     * @return 201 CREATED con el DTO de salida
     */
    @PostMapping
    public ResponseEntity<MaintenanceRuleResponse> create(
            @Valid @RequestBody CreateMaintenanceRuleRequest request) {

        MaintenanceRuleResponse response = maintenanceRuleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
