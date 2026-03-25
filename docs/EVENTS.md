# 📄 `events.md – FleetGuard MVP`

## 1. Propósito del Documento

Este documento define los **eventos de integración** utilizados en el sistema FleetGuard.

Su objetivo es:

* Establecer contratos de comunicación asíncrona hacia el exterior
* Definir productores y consumidores
* Mantener desacoplamiento para evolución futura

---

## 2. Reglas Generales de Eventos

Los eventos del sistema cumplen las siguientes reglas:

* Representan un **hecho de negocio que ya ocurrió**
* No son comandos
* No esperan respuesta
* Pueden ser consumidos por múltiples servicios
* No se garantiza entrega única
* Pueden procesarse más de una vez
* El orden de procesamiento no está garantizado

### Regla crítica del MVP

> En el MVP, los eventos son **únicamente de salida**.
> No se usan para coordinar flujo interno entre casos de uso.
> El flujo interno es completamente sincrónico.

---

## 3. Eventos del Sistema

---

## 3.1 MileageRegisteredEvent

### Descripción

Se emite cuando se registra un nuevo kilometraje para un vehículo.

### Productor

* `fleet-guard` (único servicio del MVP)

### Consumidores

* Ninguno en el MVP

### Propósito

Notificar hacia sistemas externos que el kilometraje fue actualizado.
En una evolución futura, podría consumirlo un `rules-alerts-service` separado.

### Cuándo se publica

Después de persistir el registro de kilometraje y **después** de haber generado las alertas correspondientes.

### Estructura (conceptual)

```
{
  vehicleId,
  vehicleTypeId,
  mileage,
  timestamp
}
```

---

## 3.2 AlertGeneratedEvent

### Descripción

Se emite cuando se genera una alerta de mantenimiento.

### Productor

* `fleet-guard` (único servicio del MVP)

### Consumidores

* Ninguno en el MVP

### Propósito

Notificar que un vehículo requiere mantenimiento.
Disponible para consumo futuro (notificaciones, analytics, etc.).

### Cuándo se publica

Después de persistir la alerta. Es fire-and-forget: si falla, no revierte la alerta.

### Estructura (conceptual)

```
{
  alertId,
  vehicleId,
  ruleId,
  status,
  generatedAt
}
```

---

## 3.3 MaintenanceRegisteredEvent

### Descripción

Se emite cuando se registra un mantenimiento.

### Productor

* `fleet-guard` (único servicio del MVP)

### Consumidores

* Ninguno en el MVP

### Propósito

Registrar que un mantenimiento fue realizado.
Disponible para consumo futuro.

### Estructura (conceptual)

```
{
  maintenanceId,
  vehicleId,
  mileage,
  date
}
```

---

## 4. Flujo de Eventos en el MVP

Los eventos se publican **al final de cada caso de uso**, luego de persistir. No disparan ni coordinan lógica interna.

```
RegisterMileageUseCase
    ↓ (persiste km)
    ↓ (llama GenerateAlertUseCase síncronamente)
    ↓ (persiste alertas)
    ↓
publica MileageRegisteredEvent   ← fire-and-forget
publica AlertGeneratedEvent      ← fire-and-forget (por cada alerta)
```

```
RegisterMaintenanceUseCase
    ↓ (persiste mantenimiento)
    ↓
publica MaintenanceRegisteredEvent  ← fire-and-forget
```

---

## 5. Consideraciones del MVP

* No se implementa Outbox Pattern
* No hay reintentos automáticos
* No hay versionado de eventos
* No hay manejo de duplicados avanzado
* Los eventos no coordinan flujo interno

---

## 6. Evolución Futura

Cuando el sistema escale a microservicios:

* `MileageRegisteredEvent` será consumido por `rules-alerts-service`
* La generación de alertas pasará a ser asíncrona y event-driven
* Se implementará Outbox Pattern para garantizar entrega
* Se agregará versionado de eventos
* Reintentos y DLQ (Dead Letter Queue)
* Nuevos consumidores (notificaciones, analytics)