# 📄 `events.md – FleetGuard MVP`

## 1. Propósito del Documento

Este documento define los **eventos de integración** utilizados en el sistema FleetGuard.

Su objetivo es:

* Establecer contratos de comunicación asíncrona
* Definir productores y consumidores
* Mantener desacoplamiento entre servicios

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

---

## 3. Eventos del Sistema

---

## 3.1 MileageRegisteredEvent

### Descripción

Se emite cuando se registra un nuevo kilometraje para un vehículo.

### Productor

* `fleet-service`

### Consumidores

* `rules-alerts-service`

### Propósito

Permitir la evaluación de reglas de mantenimiento y la generación de alertas.

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

* `rules-alerts-service`

### Consumidores

* (Ninguno en el MVP)

### Propósito

Notificar que un vehículo requiere mantenimiento.

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

* `maintenance-service`

### Consumidores

* (Ninguno en el MVP)

### Propósito

Registrar que un mantenimiento fue realizado.

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

## 4. Flujo de Eventos

El flujo principal del sistema es:

```
MileageRegisteredEvent
        ↓
rules-alerts-service
        ↓
AlertGeneratedEvent
```

El mantenimiento cierra el ciclo:

```
MaintenanceRegisteredEvent
```

---

## 5. Consideraciones del MVP

* No se implementa Outbox Pattern
* No hay reintentos automáticos
* No hay versionado de eventos
* No hay manejo de duplicados avanzado

---

## 6. Evolución Futura

* Implementar Outbox Pattern
* Versionado de eventos
* Reintentos y DLQ (Dead Letter Queue)
* Nuevos consumidores (notificaciones, analytics)