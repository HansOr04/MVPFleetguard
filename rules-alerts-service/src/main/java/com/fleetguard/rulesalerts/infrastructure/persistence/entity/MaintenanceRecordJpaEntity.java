package com.fleetguard.rulesalerts.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "maintenance_record")
public class MaintenanceRecordJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "vehicle_id", nullable = false, columnDefinition = "uuid")
    private UUID vehicleId;

    @Column(name = "alert_id", columnDefinition = "uuid")
    private UUID alertId;

    @Column(name = "rule_id", columnDefinition = "uuid")
    private UUID ruleId;

    @Column(name = "service_type", nullable = false, length = 150)
    private String serviceType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "provider", length = 150)
    private String provider;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    @Column(name = "mileage_at_service", nullable = false)
    private long mileageAtService;

    protected MaintenanceRecordJpaEntity() {
    }
}