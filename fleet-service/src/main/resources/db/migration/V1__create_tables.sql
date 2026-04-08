CREATE TABLE vehicle_type
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE vehicle
(
    id              UUID PRIMARY KEY,
    plate           VARCHAR(20)  NOT NULL UNIQUE,
    brand           VARCHAR(100) NOT NULL,
    model           VARCHAR(100) NOT NULL,
    year            INTEGER      NOT NULL,
    fuel_type       VARCHAR(50)  NOT NULL,
    vin             VARCHAR(17)  NOT NULL UNIQUE CHECK (LENGTH(vin) = 17),
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    current_mileage BIGINT       NOT NULL DEFAULT 0,
    vehicle_type_id UUID         NOT NULL REFERENCES vehicle_type (id)
);

CREATE TABLE mileage_log
(
    id                  UUID PRIMARY KEY,
    vehicle_id          UUID         NOT NULL REFERENCES vehicle (id),
    vehicle_type_id     UUID,
    previous_mileage    BIGINT       NOT NULL,
    mileage_value       BIGINT       NOT NULL,
    km_traveled         BIGINT       NOT NULL,
    recorded_by         VARCHAR(255) NOT NULL,
    recorded_at         TIMESTAMP    NOT NULL,
    excessive_increment BOOLEAN      NOT NULL
);