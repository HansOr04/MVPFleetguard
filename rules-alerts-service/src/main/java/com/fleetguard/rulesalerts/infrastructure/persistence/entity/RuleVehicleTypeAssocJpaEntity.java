package com.fleetguard.rulesalerts.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(
        name = "rule_vehicle_type_assoc",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rule_vehicle_type",
                columnNames = {"rule_id", "vehicle_type_id"}
        )
)
public class RuleVehicleTypeAssocJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "rule_id", nullable = false, columnDefinition = "uuid")
    private UUID ruleId;

    @Column(name = "vehicle_type_id", nullable = false, columnDefinition = "uuid")
    private UUID vehicleTypeId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected RuleVehicleTypeAssocJpaEntity() {
    }
}
