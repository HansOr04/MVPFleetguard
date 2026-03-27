package com.fleetguard.rulesalerts.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "maintenance_alert")
public class MaintenanceAlertJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "vehicle_id", nullable = false, columnDefinition = "uuid")
    private UUID vehicleId;

    @Column(name = "vehicle_type_id", nullable = false, columnDefinition = "uuid")
    private UUID vehicleTypeId;

    @Column(name = "rule_id", nullable = false, columnDefinition = "uuid")
    private UUID ruleId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "triggered_at", nullable = false, updatable = false)
    private LocalDateTime triggeredAt;

    @Column(name = "due_at_km", nullable = false)
    private Long dueAtKm;

    protected MaintenanceAlertJpaEntity() {
    }
}
