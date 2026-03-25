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
* Si hay conflicto aparente:

  * Gana `context.md` sobre negocio
  * Gana `architecture.md` sobre estructura

---

## 4. Selección de Contexto (Regla Clave)

La IA debe decidir qué documento consultar según la intención de la tarea:

### 4.1 Tareas de negocio

Ejemplos:

* Crear entidad
* Definir reglas
* Validaciones

→ Consultar:

```
context.md
```

---

### 4.2 Tareas de estructura

Ejemplos:

* Definir capas
* Crear paquetes
* Ubicación de clases

→ Consultar:

```
architecture.md
```

---

### 4.3 Tareas de eventos

Ejemplos:

* Publicar evento de salida
* Definir payload de evento
* Entender qué eventos existen

→ Consultar:

```
events.md
```

⚠️ Importante: los eventos NO coordinan flujo interno. Si la duda es sobre cómo se genera una alerta, consultar `architecture.md`, no `events.md`.

---

### 4.4 Tareas de implementación

Ejemplos:

* Crear Use Case
* Crear Controller
* Implementar Repository
* Mappers

→ Consultar:

```
implementation.md
```

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

## 6. Flujo de Trabajo Basado en HUs

El desarrollo se realiza **por Historia de Usuario + Tasks**.

Para cada Task, la IA debe:

### Paso 1 — Entender la intención

* Identificar:

  * Entidad
  * Acción
  * Regla de negocio

→ Consultar `context.md` si hay duda

---

### Paso 2 — Ubicar en arquitectura

* Determinar:

  * Capa (Domain, Application, Infrastructure)
  * Tipo de componente

→ Consultar `architecture.md`

---

### Paso 3 — Verificar eventos (si aplica)

* ¿La acción publica un evento de salida?
* ¿El evento está definido en `events.md`?

→ Consultar `events.md`

⚠️ No asumir que un evento dispara lógica interna. En el MVP, toda la lógica es sincrónica.

---

### Paso 4 — Implementar

* Crear código siguiendo:

  * Nombres
  * Estructura
  * Responsabilidades

→ Consultar `implementation.md`

---

## 7. Reglas Obligatorias de Generación

### 7.1 Dominio

* No usar frameworks
* No usar anotaciones
* Contiene reglas de negocio

---

### 7.2 Application

* Orquesta casos de uso
* No contiene lógica compleja
* Usa Ports
* `GenerateAlertUseCase` es invocado **directamente** desde `RegisterMileageUseCase`, no via evento

---

### 7.3 Infrastructure

* Implementa detalles técnicos
* No contiene reglas de negocio
* Traduce entre capas

---

## 8. Uso de Eventos

### Reglas estrictas del MVP

* Los eventos se publican **después** de persistir
* Son **fire-and-forget**: si fallan, no revierten la operación
* **No disparan lógica interna**
* No hay consumidores de eventos dentro del mismo servicio
* No hay schedulers ni listeners internos para generar alertas

### Flujo correcto

```
UseCase → persiste → EventPublisherPort → RabbitMQ
```

### Anti-patrón prohibido en MVP

```
❌ UseCase → publica evento → listener interno → genera alerta
```

---

## 9. Restricciones del MVP

La IA debe respetar:

* Sin seguridad
* Sin outbox
* Sin retries
* Sin versionado de eventos
* Un único servicio Spring Boot
* Sin microservicios separados
* Sin schedulers ni procesos batch
* PostgreSQL como persistencia
* RabbitMQ solo para eventos de salida

---

## 10. Evolución del Sistema

Este workflow debe permitir:

* Escalar a microservicios completos
* Introducir seguridad
* Implementar patrones avanzados (Outbox, DLQ)
* Convertir el flujo sincrónico en event-driven entre servicios separados
* Agregar nuevos consumidores de eventos

---

## 11. Regla Final

> La IA debe priorizar:
>
> 1. Correctitud del dominio
> 2. Respeto por la arquitectura
> 3. Claridad en la implementación
> 4. Simplicidad acorde al MVP

---

## 12. Decisión de Diseño Documentada

> **HU-11 — Generación de Alertas**
>
> La generación de alertas es **sincrónica** y ocurre dentro del mismo request de registro de kilometraje.
> `RegisterMileageUseCase` invoca directamente a `GenerateAlertUseCase`.
> No existe un scheduler, batch ni consumidor de eventos que dispare este proceso.
>
> Esta decisión prioriza simplicidad para el MVP.
> En una evolución futura, se separará en un microservicio que consuma `MileageRegisteredEvent`.