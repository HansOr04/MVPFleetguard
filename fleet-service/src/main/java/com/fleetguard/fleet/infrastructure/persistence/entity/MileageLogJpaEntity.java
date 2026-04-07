package com.fleetguard.fleet.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mileage_log")
public class MileageLogJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "previous_mileage", nullable = false)
    private long previousMileage;

    @Column(name = "mileage_value", nullable = false)
    private long mileageValue;

    @Column(name = "km_traveled", nullable = false)
    private long kmTraveled;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "recorded_by", nullable = false)
    private String recordedBy;

    @Column(name = "excessive_increment", nullable = false)
    private boolean excessiveIncrement;

    @Column(name = "vehicle_type_id")
    private UUID vehicleTypeId;
}