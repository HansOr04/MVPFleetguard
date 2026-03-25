# 📄 `events.md – FleetGuard MVP`

## 1. Propósito del Documento

Este documento define los **eventos de integración** utilizados en el sistema FleetGuard.

Su objetivo es:

* Establecer contratos de comunicación asíncrona entre servicios
* Definir productores y consumidores
* Distinguir entre Domain Events e Integration Events
* Mantener desacoplamiento entre servicios y preparar futuras integraciones

---

## 2. Tipos de Eventos

El sistema maneja dos niveles de eventos:

### Domain Events (internos)

* Se generan dentro del dominio de cada servicio
* Representan cambios de estado que ya ocurrieron
* Son internos al agregado y no salen del dominio
* No dependen de infraestructura
* No representan contratos de integración

Ejemplos:

* `MileageRegistered` (en `fleet-service`)
* `AlertGenerated` (en `rules-alerts-service`)
* `MaintenanceRegistered` (en `rules-alerts-service`)

### Integration Events (externos)

* Se derivan de Domain Events mediante un EventMapper en la capa de Application
* Se publican vía RabbitMQ hacia otros servicios o futuros consumidores
* Representan contratos de comunicación entre servicios
* Su publicación es **best-effort** y no afecta el flujo principal del negocio
* Se publican **después de persistir el agregado**

---

## 3. Reglas Generales de Eventos

Los Integration Events del sistema cumplen las siguientes reglas:

* Representan un **hecho de negocio que ya ocurrió**
* No son comandos
* No esperan respuesta
* Pueden ser consumidos por múltiples servicios
* No se garantiza entrega única
* Pueden procesarse más de una vez (los consumidores deben ser idempotentes)
* El orden de procesamiento no está garantizado
* Su fallo de publicación no debe afectar la operación principal del servicio productor

---

## 4. Integration Events del Sistema

---

### 4.1 MileageRegisteredEvent

#### Descripción

Se emite cuando se registra un nuevo kilometraje para un vehículo y se persiste exitosamente.

#### Productor

* `fleet-service` (Application Layer — `RegisterMileageUseCase`)

#### Consumidores

* `rules-alerts-service` → consume el evento para invocar `GenerateAlertUseCase`, evaluar reglas asociadas al tipo de vehículo y generar alertas si corresponde

#### Domain Event origen

* `MileageRegistered`

#### Estructura

```json
{
  "vehicleId": "UUID",
  "vehicleTypeId": "UUID",
  "vehicleStatus": "String (ACTIVE | INACTIVE)",
  "mileage": "Long",
  "timestamp": "ISO-8601"
}
```

#### Notas

* Es el único evento con consumidor activo en el MVP
* Es el puente de comunicación entre los dos servicios
* Incluye `vehicleStatus` para que `rules-alerts-service` pueda descartar vehículos inactivos sin necesidad de consulta síncrona a `fleet-service` (HU-11: no evaluar vehículos inactivos)
* Si falla la publicación, el kilometraje queda registrado pero la alerta no se genera automáticamente

---

### 4.2 AlertGeneratedEvent

#### Descripción

Se emite cuando se genera una alerta de mantenimiento y se persiste exitosamente.

#### Productor

* `rules-alerts-service` (Application Layer — `GenerateAlertUseCase`)

#### Consumidores

* (Ninguno en el MVP)

#### Domain Event origen

* `AlertGenerated`

#### Estructura

```json
{
  "alertId": "UUID",
  "vehicleId": "UUID",
  "ruleId": "UUID",
  "status": "String (PENDING | WARNING | OVERDUE)",
  "generatedAt": "ISO-8601"
}
```

#### Notas

* Es un evento informativo para futuras integraciones (notificaciones, dashboards, analytics)
* No tiene consumidor activo en el MVP

---

### 4.3 MaintenanceRegisteredEvent

#### Descripción

Se emite cuando se registra un mantenimiento y se persiste exitosamente.

#### Productor

* `rules-alerts-service` (Application Layer — `RegisterMaintenanceUseCase`)

#### Consumidores

* (Ninguno en el MVP)

#### Domain Event origen

* `MaintenanceRegistered`

#### Estructura

```json
{
  "maintenanceId": "UUID",
  "vehicleId": "UUID",
  "mileage": "Long",
  "serviceType": "String",
  "date": "ISO-8601"
}
```

#### Notas

* Es un evento informativo para futuras integraciones
* No tiene consumidor activo en el MVP

---

## 5. Flujo de Eventos

### Flujo principal (inter-servicio)

```
fleet-service
    │
    │  RegisterMileageUseCase
    │       ↓
    │  Validación y persistencia de kilometraje
    │       ↓
    │  Publicación de MileageRegisteredEvent → RabbitMQ
    │       (incluye vehicleStatus para filtrado en consumer)
    │
    ╰──────────────────────────────────────────────╮
                                                   ▼
                                          rules-alerts-service
                                               │
                                               │  Consumer recibe MileageRegisteredEvent
                                               │       ↓
                                               │  Verificar vehicleStatus == ACTIVE (HU-11)
                                               │       ↓ (si INACTIVE → descarta)
                                               │  GenerateAlertUseCase (sincrónico)
                                               │       ↓
                                               │  Evaluación de reglas con warning_threshold_km
                                               │       ↓
                                               │  Verificar no duplicidad de alertas (HU-11)
                                               │       ↓
                                               │  Persistencia de alertas (si aplica)
                                               │       ↓
                                               │  Publicación de AlertGeneratedEvent → RabbitMQ
                                               │       (best-effort, sin consumidor en MVP)
```

### Flujo de mantenimiento (dentro de rules-alerts-service)

```
rules-alerts-service
    │
    │  RegisterMaintenanceUseCase
    │       ↓
    │  Validar tipo de servicio obligatorio (HU-13)
    │       ↓
    │  Validar fecha no futura (HU-13)
    │       ↓
    │  Persistencia de mantenimiento
    │       ↓
    │  Resolución de alertas activas PENDING/WARNING → RESOLVED (HU-13)
    │       ↓
    │  Publicación de MaintenanceRegisteredEvent → RabbitMQ
    │       (best-effort, sin consumidor en MVP)
```

---

## 6. Transformación Domain Event → Integration Event

El flujo de transformación dentro de cada servicio es:

```
Agregado genera Domain Event
        ↓
Use Case persiste el agregado (save)
        ↓
Use Case extrae Domain Events (pullDomainEvents)
        ↓
EventMapper transforma Domain Event → Integration Event
        ↓
EventPublisherPort.publish(integrationEvent)
        ↓
RabbitMQ Adapter publica en RabbitMQ
```

### Reglas de transformación

* Los Domain Events **nunca** se publican directamente en RabbitMQ
* Siempre se transforman a Integration Events antes de publicar
* La transformación ocurre en la capa de Application
* La publicación ocurre **después** del `save` exitoso

---

## 7. Nota sobre el MVP

En esta versión del sistema:

* La generación de alertas en `rules-alerts-service` se ejecuta de forma **sincrónica** al recibir el evento (no hay eventos internos entre módulos del mismo servicio)
* Solo `MileageRegisteredEvent` tiene un consumidor activo
* `AlertGeneratedEvent` y `MaintenanceRegisteredEvent` se publican pero no tienen consumidor
* Los eventos son best-effort: si la publicación falla, la operación de negocio no se revierte
* Los consumidores deben ser idempotentes ante posibles duplicados

---

## 8. Consideraciones de RabbitMQ (MVP)

* Se usa un exchange por tipo de evento
* Routing simple (sin routing keys complejas)
* Una cola por consumidor
* Sin Dead Letter Queue en el MVP
* Sin reintentos automáticos avanzados
* Serialización en JSON

---

## 9. Consideraciones del MVP

* No se implementa Outbox Pattern
* No hay reintentos automáticos avanzados
* No hay versionado de eventos
* No hay manejo de duplicados avanzado (se espera idempotencia en consumidores)
* No hay garantía transaccional entre persistencia y publicación de eventos

---

## 10. Evolución Futura

* Implementar Outbox Pattern para garantía transaccional
* Versionado de eventos (schema evolution)
* Reintentos y DLQ (Dead Letter Queue)
* Nuevos consumidores (notificaciones, analytics, dashboards)
* Routing keys más específicos
* Monitoreo y observabilidad de eventos