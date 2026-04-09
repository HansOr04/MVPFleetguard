# rules-alerts-service

Microservicio responsable de la gestión de reglas de mantenimiento preventivo y la generación automática de alertas. Consume eventos de kilometraje publicados por `fleet-service` y evalúa si algún vehículo requiere mantenimiento.

---

## Responsabilidades

- Crear y configurar reglas de mantenimiento (intervalo en km, umbral de advertencia)
- Asociar reglas a tipos de vehículo
- Evaluar reglas automáticamente al recibir un evento `MileageRegistered`
- Generar y actualizar alertas de mantenimiento (`PENDING` → `WARNING` → `OVERDUE`)
- Registrar servicios de mantenimiento realizados y resolver las alertas asociadas

---

## Stack tecnológico

| Tecnología | Version |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.4 |
| Spring Data JPA | — |
| Spring AMQP (RabbitMQ) | — |
| Spring Retry | 2.0.11 |
| PostgreSQL | 15 |
| Flyway | — (migraciones de BD) |
| Testcontainers | 1.20.4 |
| Lombok | — |
| JaCoCo | 0.8.12 |

---

## API REST

Base path: `/api`

### Reglas de mantenimiento

#### Crear regla

```
POST /api/maintenance-rules
```

**Body:**

```json
{
  "name": "Cambio de aceite",
  "maintenanceType": "OIL_CHANGE",
  "intervalKm": 10000,
  "warningThresholdKm": 500,
  "status": "ACTIVE"
}
```

**Respuestas:**

| Código | Descripción |
|---|---|
| `201 Created` | Regla creada |
| `400 Bad Request` | Datos inválidos |

---

#### Asociar regla a tipo de vehículo

```
POST /api/maintenance-rules/{id}/vehicle-types
```

**Body:**

```json
{
  "vehicleTypeId": 3
}
```

**Respuestas:**

| Código | Descripción |
|---|---|
| `201 Created` | Asociación creada |
| `409 Conflict` | La regla ya está asociada a ese tipo de vehículo |
| `404 Not Found` | Regla no encontrada |

---

### Alertas

#### Listar alertas activas

```
GET /api/alerts?status=WARNING
```

Parámetro `status` opcional: `PENDING`, `WARNING`, `OVERDUE`.

**Respuesta `200 OK`:**

```json
[
  {
    "id": 1,
    "vehicleId": 5,
    "ruleId": 2,
    "status": "WARNING",
    "triggeredAt": "2024-11-15T10:30:00",
    "dueAtKm": 55000
  }
]
```

---

#### Alertas de un vehículo

```
GET /api/alerts/vehicle/{plate}
```

Retorna las alertas enriquecidas con información del vehículo y la regla asociada.

**Respuestas:**

| Código | Descripción |
|---|---|
| `200 OK` | Lista de alertas del vehículo |
| `404 Not Found` | Vehículo no encontrado |

---

### Mantenimiento

#### Registrar servicio realizado

```
POST /api/maintenance/{plate}
```

**Body:**

```json
{
  "alertId": 1,
  "serviceType": "OIL_CHANGE",
  "description": "Cambio de aceite sintético 5W-30",
  "cost": 85.00,
  "provider": "Taller Central",
  "performedAt": "2024-11-20T09:00:00",
  "mileageAtService": 55000,
  "recordedBy": "juan.perez"
}
```

Al registrar el mantenimiento, la alerta asociada pasa automáticamente a estado `RESOLVED`.

**Respuestas:**

| Código | Descripción |
|---|---|
| `201 Created` | Mantenimiento registrado y alerta resuelta |
| `404 Not Found` | Vehículo o alerta no encontrados |
| `400 Bad Request` | Datos inválidos |

---

## Mensajería

**Cola consumida:** `mileage.registered.queue`  
**Exchange:** `fleetguard.exchange`  
**Routing key:** `mileage.registered`

Al recibir el evento, `EvaluateMaintenanceAlertsService` evalúa todas las reglas activas para el tipo de vehículo y crea o actualiza alertas según corresponda.

**Ciclo de vida de una alerta:**

```
[sin alerta] → PENDING → WARNING → OVERDUE
                                       │
                      mantenimiento ───┘
                                       ▼
                                   RESOLVED
```

| Estado | Condición |
|---|---|
| `PENDING` | Kilometraje supera el umbral de inicio de la regla |
| `WARNING` | Kilometraje supera `dueAtKm - warningThresholdKm` |
| `OVERDUE` | Kilometraje supera `dueAtKm` (el intervalo ya venció) |
| `RESOLVED` | Se registró el mantenimiento correspondiente |

---

## Base de datos

**Base de datos:** `rules_alerts_db`

```
maintenance_rule
  id, name, maintenance_type, interval_km, warning_threshold_km,
  status, created_at, updated_at

rule_vehicle_type_assoc
  id, rule_id, vehicle_type_id  (UNIQUE: rule_id + vehicle_type_id)

maintenance_alert
  id, vehicle_id, vehicle_type_id, rule_id, status,
  triggered_at, due_at_km

maintenance_record
  id, vehicle_id, alert_id, rule_id, service_type, description,
  cost, provider, performed_at, mileage_at_service,
  recorded_by, created_at
```

Las migraciones se aplican automáticamente con Flyway al iniciar el servicio.

---

## Configuración

### Perfil `default` (local)

| Variable / Propiedad | Valor por defecto |
|---|---|
| Puerto | `8093` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5434/rules_alerts_db` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `postgres` |
| `spring.rabbitmq.host` | `localhost` |
| `spring.rabbitmq.port` | `5672` |
| URL fleet-service | `http://localhost:8092` |

### Perfil `docker`

Activado automáticamente con `SPRING_PROFILES_ACTIVE=docker`. Reapunta la base de datos, RabbitMQ y la URL de `fleet-service` a los nombres de los servicios dentro de la red Docker.

---

## Ejecución local

### Prerrequisitos

```bash
# Levantar PostgreSQL, RabbitMQ y fleet-service
docker compose up rules-alerts-db rabbitmq fleet-service
```

### Correr el servicio

```bash
cd rules-alerts-service
./mvnw spring-boot:run
```

El servicio queda disponible en `http://localhost:8093`.

---

## Tests

```bash
mvn -pl rules-alerts-service -am clean verify
```

- Los tests de integración usan **Testcontainers** (PostgreSQL + RabbitMQ en Docker).
- El reporte de cobertura JaCoCo se genera en `target/site/jacoco/`.

```bash
# Ver reporte en el navegador
open rules-alerts-service/target/site/jacoco/index.html
```

---

## Docker

```bash
# Build de la imagen (desde la raíz del repositorio)
docker build -f rules-alerts-service/Dockerfile -t rules-alerts-service .
```

La imagen usa un build multi-stage (Maven → Eclipse Temurin 17) y expone el puerto `8080`.
