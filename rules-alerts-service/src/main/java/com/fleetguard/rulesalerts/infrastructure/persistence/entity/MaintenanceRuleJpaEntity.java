package com.fleetguard.rulesalerts.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla maintenance_rule.
 * <p>
 * No contiene lógica de negocio ni anotaciones de dominio.
 * Getters y setters escritos manualmente (sin Lombok).
 */
@Entity
@Table(name = "maintenance_rule")
public class MaintenanceRuleJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "maintenance_type", nullable = false, length = 50)
    private String maintenanceType;

    @Column(name = "interval_km", nullable = false)
    private Integer intervalKm;

    @Column(name = "warning_threshold_km", nullable = false)
    private Integer warningThresholdKm;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ── Constructor protegido para JPA ────────────────────────────────────
    protected MaintenanceRuleJpaEntity() {
    }

    // ── Constructor completo ──────────────────────────────────────────────
    public MaintenanceRuleJpaEntity(UUID id,
                                    String name,
                                    String maintenanceType,
                                    Integer intervalKm,
                                    Integer warningThresholdKm,
                                    String status,
                                    LocalDateTime createdAt,
                                    LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.maintenanceType = maintenanceType;
        this.intervalKm = intervalKm;
        this.warningThresholdKm = warningThresholdKm;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public Integer getIntervalKm() {
        return intervalKm;
    }

    public Integer getWarningThresholdKm() {
        return warningThresholdKm;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ── Setters ───────────────────────────────────────────────────────────

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public void setIntervalKm(Integer intervalKm) {
        this.intervalKm = intervalKm;
    }

    public void setWarningThresholdKm(Integer warningThresholdKm) {
        this.warningThresholdKm = warningThresholdKm;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
