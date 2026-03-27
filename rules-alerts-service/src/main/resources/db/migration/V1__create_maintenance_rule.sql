CREATE TABLE maintenance_rule (
    id                   UUID            NOT NULL,
    name                 VARCHAR(150)    NOT NULL,
    maintenance_type     VARCHAR(50)     NOT NULL,
    interval_km          INT             NOT NULL,
    warning_threshold_km INT             NOT NULL    DEFAULT 500,
    status               VARCHAR(20)     NOT NULL    DEFAULT 'ACTIVE',
    created_at           TIMESTAMP       NOT NULL    DEFAULT NOW(),
    updated_at           TIMESTAMP       NOT NULL    DEFAULT NOW(),

    CONSTRAINT pk_maintenance_rule          PRIMARY KEY (id),
    CONSTRAINT chk_maintenance_type         CHECK (maintenance_type IN ('PREVENTIVE', 'CORRECTIVE')),
    CONSTRAINT chk_interval_km_positive     CHECK (interval_km > 0),
    CONSTRAINT chk_warning_threshold_positive CHECK (warning_threshold_km > 0)
);
