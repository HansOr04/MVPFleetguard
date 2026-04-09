# fleet-service

Microservicio responsable de la gestión de vehículos y el registro de kilometraje. Publica eventos de actualización de kilometraje a RabbitMQ para que otros servicios puedan reaccionar de forma desacoplada.

---

## Responsabilidades

- Registrar vehículos con su información técnica (placa, VIN, marca, modelo, año, tipo de combustible)
- Consultar vehículos por placa
- Registrar actualizaciones de kilometraje y mantener el historial en `mileage_log`
- Publicar el evento `MileageRegistered` a RabbitMQ tras cada registro de kilometraje

---

## Stack tecnológico

| Tecnología | Version |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.4 |
| Spring Data JPA | — |
| Spring AMQP (RabbitMQ) | — |
| PostgreSQL | 15 |
| Flyway | — (migraciones de BD) |
| Testcontainers | 1.20.4 |
| Lombok | — |
| JaCoCo | 0.8.12 |

---

## API REST

Base path: `/api`

### Registrar vehículo

```
POST /api/vehicles
```

**Body:**

```json
{
  "plate": "ABC-123",
  "vin": "1HGBH41JXMN109186",
  "brand": "Toyota",
  "model": "Hilux",
  "year": 2022,
  "fuelType": "DIESEL",
  "vehicleTypeId": 3
}
```

**Respuestas:**

| Código | Descripción |
|---|---|
| `201 Created` | Vehículo registrado correctamente |
| `409 Conflict` | Placa o VIN ya registrados |
| `400 Bad Request` | Datos inválidos (VIN con formato incorrecto, campos faltantes) |

---

### Registrar kilometraje

```
POST /api/vehicles/{plate}/mileage
```

**Body:**

```json
{
  "mileageValue": 55000,
  "recordedBy": "juan.perez"
}
```

**Respuestas:**

| Código | Descripción |
|---|---|
| `201 Created` | Kilometraje registrado; se publica el evento `MileageRegistered` |
| `404 Not Found` | Vehículo no encontrado |
| `400 Bad Request` | Kilometraje inválido (menor al anterior, vehículo inactivo, etc.) |

---

### Consultar vehículo

```
GET /api/vehicles/{plate}
```

**Respuestas:**

| Código | Descripción |
|---|---|
| `200 OK` | Datos del vehículo |
| `404 Not Found` | Vehículo no encontrado |

---

## Mensajería

**Exchange:** `fleetguard.exchange`  
**Routing key:** `mileage.registered`

**Evento `MileageRegistered`:**

```json
{
  "vehicleId": 1,
  "vehicleTypeId": 3,
  "currentMileage": 55000,
  "status": "ACTIVE"
}
```

---

## Base de datos

**Base de datos:** `fleet_db`

```
vehicle_type
  id, name

vehicle
  id, plate (UNIQUE), vin (UNIQUE, 17 chars), brand, model, year,
  fuel_type, status, current_mileage, vehicle_type_id

mileage_log
  id, vehicle_id, previous_mileage, mileage_value, km_traveled,
  recorded_by, recorded_at
```

Las migraciones se aplican automáticamente con Flyway al iniciar el servicio.

---

## Configuración

### Perfil `default` (local)

| Variable / Propiedad | Valor por defecto |
|---|---|
| Puerto | `8092` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5433/fleet_db` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `postgres` |
| `spring.rabbitmq.host` | `localhost` |
| `spring.rabbitmq.port` | `5672` |
| `spring.rabbitmq.username` | `guest` |
| `spring.rabbitmq.password` | `guest` |

### Perfil `docker`

El perfil `docker` se activa automáticamente en el contenedor (`SPRING_PROFILES_ACTIVE=docker`) y reapunta la base de datos y RabbitMQ a los servicios de la red interna Docker.

---

## Ejecución local

### Prerrequisitos

```bash
# Levantar PostgreSQL y RabbitMQ
docker compose up fleet-db rabbitmq
```

### Correr el servicio

```bash
cd fleet-service
./mvnw spring-boot:run
```

El servicio queda disponible en `http://localhost:8092`.

---

## Tests

```bash
mvn -pl fleet-service -am clean verify
```

- Los tests de integración usan **Testcontainers** (PostgreSQL + RabbitMQ en Docker).
- El reporte de cobertura JaCoCo se genera en `target/site/jacoco/`.

```bash
# Ver reporte en el navegador
open fleet-service/target/site/jacoco/index.html
```

---

## Docker

```bash
# Build de la imagen (desde la raíz del repositorio)
docker build -f fleet-service/Dockerfile -t fleet-service .
```

La imagen usa un build multi-stage (Maven → Eclipse Temurin 17) y expone el puerto `8080`.
