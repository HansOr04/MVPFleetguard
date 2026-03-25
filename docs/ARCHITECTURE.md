# 📄 Architecture.md


## 1. Visión General

El sistema se diseña como una aplicación backend basada en **Arquitectura Hexagonal (Ports & Adapters)**, con un enfoque en:

* Separación estricta de responsabilidades
* Dominio desacoplado de frameworks
* Alta testabilidad
* Evolución progresiva hacia un sistema event-driven

El sistema permite gestionar vehículos, registrar kilometraje, definir reglas de mantenimiento y generar alertas basadas en dichas reglas.

La comunicación interna del sistema es **sincrónica**.
La publicación de eventos hacia RabbitMQ es **asíncrona y fire-and-forget** (no bloquea el flujo principal).

---

## 2. Alcance del MVP

El MVP incluye únicamente las siguientes capacidades:

* HU-01 → Registrar vehículo
* HU-04 → Registrar km
* HU-05 → Validar km
* HU-07 → Crear regla
* HU-09 → Asociar regla a tipo de vehículo
* HU-11 → Generar alerta
* HU-13 → Registrar mantenimiento

### Decisiones del MVP

* Un único servicio Spring Boot con arquitectura hexagonal interna
* Comunicación interna completamente sincrónica
* Publicación de eventos simple hacia RabbitMQ (sin outbox, sin retries avanzados)
* Sin seguridad
* Persistencia con PostgreSQL
* RabbitMQ solo para emisión de eventos de salida, no para coordinar flujo interno

---

## 3. Principios Arquitectónicos

* Arquitectura Hexagonal (Ports & Adapters)
* Clean Architecture
* DDD táctico (Agregados, Entidades, Value Objects)
* Principios SOLID
* Bajo acoplamiento / Alta cohesión
* Separación explícita entre dominio y tecnología
* Uso de casos de uso como orquestadores

---

## 4. Estructura General

```
┌──────────────────────────────┐
│       Infrastructure         │
├──────────────────────────────┤
│         Application          │
├──────────────────────────────┤
│           Domain             │
└──────────────────────────────┘
```

### Regla fundamental

> Todas las dependencias apuntan hacia el dominio.

---

## 5. Arquitectura Interna de un Servicio

---

### 5.1 Reglas Generales de Capas

* El **Domain no depende de ninguna otra capa**
* Application depende de Domain
* Infrastructure depende de Application y Domain
* La infraestructura implementa interfaces (Ports)
* El dominio nunca conoce:

  * Spring
  * JPA
  * RabbitMQ
  * HTTP

---

### 5.2 Domain

#### Responsabilidad

Contener toda la lógica de negocio e invariantes del sistema.

#### Contiene

* Agregados
* Entidades
* Value Objects
* Enumeraciones
* Excepciones de negocio
* Domain Events

#### Reglas

* No contiene anotaciones de frameworks
* Toda regla de negocio vive aquí
* Un agregado no modifica otro agregado directamente
* Las validaciones de negocio son obligatorias aquí (no en controllers)

#### Ejemplo estructura

```
/domain
├── model
│   ├── vehicle
│   ├── rule
│   ├── alert
│   └── maintenance
├── valueobject
├── enums
├── exception
└── event
```

---

### 5.3 Application

#### Responsabilidad

Orquestar los casos de uso del sistema.

#### Contiene

* Use Cases (uno por intención de negocio)
* Ports In (interfaces de entrada)
* Ports Out (interfaces hacia infraestructura)
* DTOs de aplicación (si aplica)

#### Reglas

* No contiene lógica de negocio compleja
* Coordina agregados
* Invoca repositorios mediante Ports Out
* Publica eventos mediante un EventPublisher Port

#### Ejemplo estructura

```
/application
├── ports
│   ├── in
│   └── out
├── usecase
└── service
```

---

### 5.4 Infrastructure

#### Responsabilidad

Implementar detalles técnicos.

#### Contiene

* Controllers (REST)
* DTOs de entrada/salida
* Mappers
* Repositorios JPA
* Configuración de RabbitMQ
* Implementación de EventPublisher
* Configuración de Spring

#### Reglas

* No contiene lógica de negocio
* Traduce entre mundo externo y dominio
* Implementa Ports Out

#### Ejemplo estructura

```
/infrastructure
├── config
├── web
├── persistence
├── messaging
└── mapper
```

---

## 6. Flujos de Ejecución

---

### 6.1 Flujo estándar de un caso de uso

1. Controller recibe request
2. Convierte a comando (DTO → modelo aplicación)
3. Invoca Use Case (Port In)
4. Use Case:

   * Carga agregados
   * Ejecuta lógica de dominio
   * Persiste cambios
   * Publica eventos (si aplica)
5. Retorna respuesta

---

### 6.2 Generación de Alertas (HU-11)

La generación de alertas ocurre de forma **sincrónica** dentro del mismo flujo de registro de kilometraje.
No existe un scheduler, proceso batch ni consumidor de eventos que dispare este proceso.

Flujo:

```
POST /mileage
    ↓
RegisterMileageUseCase
    ↓
vehicle.registerMileage()        ← Domain: valida y actualiza km
    ↓
GenerateAlertUseCase             ← llamada directa, mismo request
    ↓
Evalúa reglas del tipo de vehículo
    ↓
Persiste alertas generadas
    ↓
EventPublisherPort.publish(AlertGeneratedEvent)   ← fire-and-forget
```

#### Reglas de este flujo

* `GenerateAlertUseCase` es invocado directamente desde `RegisterMileageUseCase`
* La evaluación de reglas es responsabilidad del dominio
* El evento `AlertGeneratedEvent` se publica **después** de persistir, no antes
* Si la publicación del evento falla, **no se revierte** la alerta (sin Outbox en MVP)

---

### 6.3 Publicación de Eventos

Los eventos son **salidas del sistema**, no mecanismos de coordinación interna.

Flujo:

1. El dominio genera un Domain Event
2. El Use Case lo recoge
3. Invoca `EventPublisherPort`
4. Infrastructure publica en RabbitMQ

#### Regla crítica

> RabbitMQ en el MVP se usa **solo para publicar eventos de salida**.
> No se usa para coordinar flujo entre casos de uso dentro del mismo servicio.

---

## 7. Convenciones de Nombres y Estructura

### Use Cases

```
<Verb><Entity>UseCase
Ej: RegisterVehicleUseCase
```

### Ports In

```
<Entity>UseCase
```

### Ports Out

```
<Entity><Action>Port
Ej: VehicleRepositoryPort
```

### Eventos

```
<Entity><Action>Event
Ej: AlertGeneratedEvent
```

---

## 8. Decisiones del MVP (Simplificaciones)

* No se implementa Outbox Pattern
* No se manejan retries de eventos
* No hay versionado de eventos
* No hay seguridad
* No hay separación en microservicios
* No hay schedulers ni procesos batch
* Un único servicio maneja todo el flujo

Estas decisiones son intencionales para priorizar velocidad de entrega.

---

## 9. Evolución Futura

* Separación en microservicios (vehicle-service, rules-alerts-service, maintenance-service)
* Implementación de Outbox Pattern
* Seguridad (JWT / OAuth2)
* Reintentos de eventos y DLQ
* Versionado de eventos
* Observabilidad
* Coordinación real via eventos entre servicios separados