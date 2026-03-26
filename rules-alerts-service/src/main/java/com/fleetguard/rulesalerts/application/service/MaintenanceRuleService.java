package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRuleConstants;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceRuleJpaEntity;
import com.fleetguard.rulesalerts.infrastructure.persistence.repository.MaintenanceRuleJpaRepository;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.CreateMaintenanceRuleRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRuleResponse;
import com.fleetguard.rulesalerts.infrastructure.web.mapper.MaintenanceRuleMapper;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación para reglas de mantenimiento.
 * Orquesta la lógica de negocio sin depender directamente de Spring Data ni de JPA.
 */
@Service
public class MaintenanceRuleService {

    private final MaintenanceRuleJpaRepository repository;
    private final MaintenanceRuleMapper mapper;

    public MaintenanceRuleService(MaintenanceRuleJpaRepository repository,
                                  MaintenanceRuleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // ── Lógica de negocio ─────────────────────────────────────────────────

    /**
     * Resuelve el umbral de advertencia a utilizar.
     * Si el valor es {@code null} o menor a 1, retorna el valor por defecto
     * ({@link MaintenanceRuleConstants#DEFAULT_WARNING_THRESHOLD_KM}).
     *
     * @param warningThresholdKm valor proporcionado por el cliente (nullable)
     * @return umbral resuelto en kilómetros
     */
    public int resolveWarningThreshold(Integer warningThresholdKm) {
        if (warningThresholdKm == null || warningThresholdKm < 1) {
            return MaintenanceRuleConstants.DEFAULT_WARNING_THRESHOLD_KM;
        }
        return warningThresholdKm;
    }

    // ── Casos de uso ──────────────────────────────────────────────────────

    /**
     * Crea y persiste una nueva regla de mantenimiento con status "ACTIVE".
     *
     * @param request DTO de entrada validado
     * @return DTO de salida con los datos persistidos
     */
    public MaintenanceRuleResponse create(CreateMaintenanceRuleRequest request) {
        int resolvedThreshold = resolveWarningThreshold(request.getWarningThresholdKm());
        MaintenanceRuleJpaEntity entity = mapper.toEntity(request, resolvedThreshold);
        MaintenanceRuleJpaEntity saved = repository.save(entity);
        return mapper.toResponse(saved);
    }
}
