CREATE TABLE maintenance_rule
(
    id                   UUID PRIMARY KEY,
    name                 VARCHAR(150) NOT NULL,
    maintenance_type     VARCHAR(50)  NOT NULL,
    interval_km          INTEGER      NOT NULL,
    warning_threshold_km INTEGER      NOT NULL,
    status               VARCHAR(20)  NOT NULL,
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL
);

CREATE TABLE rule_vehicle_type_assoc
(
    id              UUID PRIMARY KEY,
    rule_id         UUID      NOT NULL REFERENCES maintenance_rule (id),
    vehicle_type_id UUID      NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    CONSTRAINT uk_rule_vehicle_type UNIQUE (rule_id, vehicle_type_id)
);

CREATE TABLE maintenance_alert
(
    id              UUID PRIMARY KEY,
    vehicle_id      UUID        NOT NULL,
    vehicle_type_id UUID        NOT NULL,
    rule_id         UUID        NOT NULL REFERENCES maintenance_rule (id),
    status          VARCHAR(20) NOT NULL,
    triggered_at    TIMESTAMP   NOT NULL,
    due_at_km       BIGINT      NOT NULL
);

CREATE TABLE maintenance_record
(
    id                 UUID PRIMARY KEY,
    vehicle_id         UUID         NOT NULL,
    alert_id           UUID REFERENCES maintenance_alert (id),
    rule_id            UUID REFERENCES maintenance_rule (id),
    service_type       VARCHAR(150) NOT NULL,
    description        VARCHAR(500),
    cost               NUMERIC(10, 2),
    provider           VARCHAR(150),
    performed_at       TIMESTAMP    NOT NULL,
    mileage_at_service BIGINT       NOT NULL,
    recorded_by        VARCHAR(150) NOT NULL
);