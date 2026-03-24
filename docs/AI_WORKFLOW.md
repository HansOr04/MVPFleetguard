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
events.md         → define la COMUNICACIÓN
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

* Publicar evento
* Consumir evento
* Definir payload

→ Consultar:

```
events.md
```

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
3. events.md (si aplica)
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

* ¿La acción genera o consume eventos?

→ Consultar `events.md`

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

---

### 7.3 Infrastructure

* Implementa detalles técnicos
* No contiene reglas de negocio
* Traduce entre capas

---

## 8. Uso de Eventos

* Los eventos:

  * Representan hechos
  * No son comandos
* Se publican desde Application
* Se implementan en Infrastructure

---

## 9. Restricciones del MVP

La IA debe respetar:

* Sin seguridad
* Sin outbox
* Sin retries
* Sin versionado de eventos
* Microservicios simples
* PostgreSQL como persistencia
* RabbitMQ como mensajería

---

## 10. Evolución del Sistema

Este workflow debe permitir:

* Escalar a microservicios completos
* Introducir seguridad
* Implementar patrones avanzados (Outbox, DLQ)
* Agregar nuevos consumidores de eventos

---

## 11. Regla Final

> La IA debe priorizar:
>
> 1. Correctitud del dominio
> 2. Respeto por la arquitectura
> 3. Claridad en la implementación
> 4. Simplicidad acorde al MVP
