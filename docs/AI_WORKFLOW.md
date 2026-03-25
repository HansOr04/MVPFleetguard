# 📄 `AI_WORKFLOW.md – FleetGuard MVP`

## 1. Propósito del Documento

Este documento define **cómo la IA debe razonar, qué contexto consultar y cómo ejecutar tareas** dentro del proyecto FleetGuard.

Su objetivo es:

* Evitar ambigüedad en generación de código
* Mantener consistencia con dominio y arquitectura
* Optimizar el uso de contexto (no leer todo siempre)
* Guiar la implementación basada en HUs y Tasks

---

## 2. Principio Fundamental

> La IA **NO debe leer todos los documentos siempre**.
> Debe consultar **únicamente lo necesario según la tarea**.

---

## 3. Jerarquía de Documentos

Los documentos del sistema tienen una relación jerárquica:

```
context.md        → define el QUÉ (negocio)
architecture.md   → define el CÓMO estructural
events.md         → define la COMUNICACIÓN de salida
implementation.md → define el DETALLE de código
```

### Regla obligatoria

* Ningún archivo contradice a otro
* Si hay conflicto:
  * Gana `context.md` sobre negocio
  * Gana `architecture.md` sobre estructura

---

## 4. Selección de Contexto (Regla Clave)

La IA debe decidir qué documento consultar según la intención de la tarea:

### Reglas generales

* Si existe ambigüedad, la IA debe consultar más de un documento antes de inferir comportamiento
* Nunca asumir reglas de negocio sin validar en `context.md`

---

### 4.1 Tareas de negocio

Ejemplos:

* Crear entidad
* Definir reglas de validación
* Entender flujo del dominio

→ Consultar: `context.md`

---

### 4.2 Tareas de estructura

Ejemplos:

* Definir capas
* Crear paquetes
* Ubicar clases en un servicio
* Determinar a qué microservicio pertenece una HU

→ Consultar: `architecture.md`

---

### 4.3 Tareas de eventos

Ejemplos:

* Publicar evento
* Consumir evento
* Definir payload
* Transformar Domain Event a Integration Event

→ Consultar: `events.md`

⚠️ Importante: los eventos NO coordinan flujo interno. Si la duda es sobre cómo se genera una alerta, consultar `architecture.md`, no `events.md`.

---

### 4.4 Tareas de implementación

Ejemplos:

* Crear Use Case
* Crear Controller
* Implementar Repository / Adapter
* Crear Mappers
* Definir DTOs
* Crear tablas SQL

→ Consultar: `implementation.md`

---

## 5. Flujo Condicional de Consulta

Si la tarea no es clara o mezcla múltiples responsabilidades:

```
1. context.md
2. architecture.md
3. events.md (solo si la tarea involucra publicación de eventos)
4. implementation.md
```

---

## 6. Alcance del MVP Core

El MVP incluye **únicamente 7 HUs** distribuidas en 2 microservicios:

### fleet-service

| HU | Funcionalidad |
|----|--------------|
| HU-01 | Registrar vehículo con tipo asociado |
| HU-04 | Registrar y acumular kilometraje |
| HU-05 | Validar coherencia del km (dentro de HU-04, sin endpoint propio) |

### rules-alerts-service

| HU | Funcionalidad |
|----|--------------|
| HU-07 | Crear regla con tipo de mantenimiento |
| HU-09 | Asociar regla a tipo de vehículo |
| HU-11 | Generar alerta automática por km (disparada por evento, sin endpoint REST) |
| HU-13 | Registrar mantenimiento |

### HUs diferidas (NO implementar)

* HU-06, HU-12, HU-14, HU-16

> Si la IA recibe una tarea relacionada con una HU diferida, debe indicar que está fuera del alcance del MVP.

---

## 7. Flujo de Trabajo Basado en HUs

El desarrollo se realiza **por Historia de Usuario + Tasks**.

Para cada Task, la IA debe:

### Paso 1 — Identificar el servicio

* ¿La tarea pertenece a `fleet-service` o `rules-alerts-service`?

→ Consultar `architecture.md` §5

---

### Paso 2 — Entender la intención

* Identificar:
  * Entidad / Agregado
  * Acción
  * Regla de negocio

→ Consultar `context.md` si hay duda

---

### Paso 3 — Ubicar en arquitectura

* Determinar:
  * Capa (Domain, Application, Infrastructure)
  * Tipo de componente (Agregado, Use Case, Port, Adapter, Controller, etc.)

→ Consultar `architecture.md`

---

### Paso 4 — Verificar eventos (si aplica)

* ¿La acción genera o consume eventos?
* ¿Se necesita transformar Domain Event → Integration Event?

→ Consultar `events.md`

⚠️ No asumir que un evento dispara lógica interna. En el MVP, toda la lógica es sincrónica.

---

### Paso 5 — Implementar

* Crear código siguiendo:
  * Nombres y convenciones
  * Estructura de paquetes del servicio
  * Ejemplos de referencia

→ Consultar `implementation.md`

---

## 8. Reglas Obligatorias de Generación

### 8.1 Dominio

* No usar frameworks ni anotaciones
* Contiene reglas de negocio y validaciones
* Agregados extienden `AggregateRoot`
* Domain Events se registran con `addDomainEvent()`

---

### 8.2 Application

* Orquesta casos de uso
* No contiene lógica compleja
* Usa Ports In y Ports Out
* Un Use Case debe modificar un único agregado raíz por transacción lógica (cuando sea posible)
* Los eventos se publican **después del `save`**, nunca antes

---

### 8.3 Infrastructure

* Implementa detalles técnicos
* No contiene reglas de negocio
* Traduce entre capas (Mappers)
* Controllers solo: reciben → transforman → invocan → responden

---

## 9. Uso de Eventos

* Los eventos representan hechos que ya ocurrieron, no son comandos
* Se publican desde Application (Use Case), nunca desde Controller ni Repository
* Se implementan en Infrastructure (RabbitMQ)
* La publicación de eventos es un efecto secundario **best-effort**
* No debe afectar el flujo principal del caso de uso
* La lógica de negocio nunca depende de la publicación de eventos
* Domain Events se transforman a Integration Events mediante `EventMapper` antes de publicar

---

## 10. Restricciones del MVP

La IA debe respetar:

* 2 microservicios: `fleet-service` y `rules-alerts-service`
* Comunicación entre servicios: asíncrona vía RabbitMQ
* Comunicación interna de cada servicio: sincrónica
* PostgreSQL como persistencia (una BD por servicio)
* Sin FK cross-database entre servicios
* Sin seguridad
* Sin outbox pattern
* Sin retries avanzados de eventos
* Sin versionado de eventos
* Best-effort en publicación de eventos

---

## 11. Reglas de Datos Compartidos entre Servicios

* `rules-alerts-service` recibe datos de `fleet-service` **únicamente** a través de `MileageRegisteredEvent`
* El evento incluye: `vehicleId`, `vehicleTypeId`, `vehicleStatus`, `mileage`
* No hay comunicación HTTP síncrona entre servicios
* Los campos `vehicle_id` y `vehicle_type_id` en tablas de `rules-alerts-service` son UUIDs simples, **sin FK cross-database**
* La integridad referencial entre servicios se mantiene a nivel de aplicación

---

## 12. Evolución del Sistema

Este workflow debe permitir:

* Escalar a más microservicios
* Introducir seguridad (JWT / OAuth2)
* Implementar patrones avanzados (Outbox, DLQ)
* Convertir el flujo sincrónico en event-driven entre servicios separados
* Agregar nuevos consumidores de eventos
* Observabilidad

---

## 13. Regla Final

> La IA debe priorizar:
>
> 1. Correctitud del dominio
> 2. Respeto por la arquitectura (2 servicios, hexagonal)
> 3. Claridad en la implementación
> 4. Simplicidad acorde al MVP
> 5. Trazabilidad con HUs y subtasks
