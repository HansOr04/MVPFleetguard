
-- Tabla: vehicle (HU-01 subtask DEV#1)
CREATE TABLE vehicle (
    id              UUID PRIMARY KEY,
    plate           VARCHAR(20)  NOT NULL UNIQUE,
    brand           VARCHAR(100) NOT NULL,
    model           VARCHAR(100) NOT NULL,
    year            INT          NOT NULL,
    fuel_type       VARCHAR(50)  NOT NULL,
    vin             VARCHAR(17)  NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    current_mileage BIGINT       NOT NULL DEFAULT 0,
    vehicle_type_id UUID         NOT NULL REFERENCES vehicle_type(id)
);
