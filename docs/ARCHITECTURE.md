# 📄 Architecture.md

## 1. Visión General

El sistema se diseña como un conjunto de **microservicios ligeros** basados en **Arquitectura Hexagonal (Ports & Adapters)**, con un enfoque en:

* Separación estricta de responsabilidades
* Dominio desacoplado de frameworks
* Alta testabilidad
* Evolución progresiva hacia un sistema event-driven completo

El sistema permite gestionar vehículos, registrar kilometraje, definir reglas de mantenimiento y generar alertas basadas en dichas reglas.

La comunicación interna de cada servicio es **sincrónica**. La comunicación entre servicios se realiza de forma **asíncrona mediante RabbitMQ**.

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

* Arquitectura de microservicios ligeros, cada uno con arquitectura hexagonal interna
* Comunicación interna sincrónica dentro de cada servicio
* Comunicación entre servicios asíncrona vía RabbitMQ
* Publicación de eventos simple (sin outbox, sin retries avanzados)
* Sin seguridad
* Persistencia con PostgreSQL (una base de datos por servicio)
* Best-effort en publicación de eventos

---

## 3. Principios Arquitectónicos

* Arquitectura Hexagonal (Ports & Adapters)
* Clean Architecture
* DDD táctico (Agregados, Entidades, Value Objects)
* Principios SOLID
* Bajo acoplamiento / Alta cohesión
* Separación explícita entre dominio y tecnología
* Uso de casos de uso como orquestadores
* Un Use Case debe modificar un único agregado raíz por transacción lógica (cuando sea posible)

---

## 4. Estructura General por Servicio

Cada microservicio respeta la siguiente estructura de capas:

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

## 5. Definición de Microservicios

---

### 5.1 fleet-service

#### Responsabilidad

Gestionar vehículos, tipos de vehículo y registro de kilometraje.

#### HUs que cubre

* HU-01 → Registrar vehículo
* HU-04 → Registrar kilometraje
* HU-05 → Validar kilometraje

#### Agregados

* Vehicle (con VehicleType como entidad interna)
* MileageLog

#### Eventos que produce

* `MileageRegisteredEvent` → consumido por `rules-alerts-service`

---

### 5.2 rules-alerts-service

#### Responsabilidad

Gestionar reglas de mantenimiento, generar alertas y registrar mantenimientos.

#### HUs que cubre

* HU-07 → Crear regla de mantenimiento
* HU-09 → Asociar regla a tipo de vehículo
* HU-11 → Generar alerta automática
* HU-13 → Registrar mantenimiento

#### Agregados

* MaintenanceRule
* MaintenanceAlert
* MaintenanceRecord

#### Eventos que consume

* `MileageRegisteredEvent` → dispara evaluación de reglas y generación de alertas

#### Eventos que produce

* `AlertGeneratedEvent` → sin consumidor en el MVP
* `MaintenanceRegisteredEvent` → sin consumidor en el MVP

---

## 6. Topología de Despliegue (MVP)

```
┌─────────────────┐       RabbitMQ        ┌──────────────────────┐
│  fleet-service   │ ──────────────────▶  │  rules-alerts-service │
│  (PostgreSQL A)  │  MileageRegistered   │  (PostgreSQL B)       │
└─────────────────┘       Event           └──────────────────────┘
```

* Cada servicio tiene su propia base de datos PostgreSQL
* RabbitMQ actúa como broker de mensajería entre servicios
* No hay comunicación HTTP directa entre servicios en el MVP

---

## 7. Arquitectura Interna de un Servicio

---

### 7.1 Reglas Generales de Capas

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

### 7.2 Domain

#### Responsabilidad

Contener toda la lógica de negocio e invariantes del sistema.

#### Contiene

* Agregados
* Entidades
* Value Objects
* Enumeraciones
* Excepciones de negocio
* Domain Events (eventos internos del dominio, no eventos de integración)

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

#### Convención de paquetes

Cada carpeta dentro de `/domain/model` representa un paquete que contiene:
- La clase principal del agregado (ej: `Vehicle.java`)
- Sus entidades internas (ej: `VehicleType.java`)
- Sus Value Objects propios si son exclusivos del agregado

Ejemplo:

```
/domain/model/vehicle/
├── Vehicle.java          ← Agregado raíz
├── VehicleType.java      ← Entidad interna
└── VehicleStatus.java    ← Enum del agregado
```

---

### 7.3 Application

#### Responsabilidad

Orquestar los casos de uso del sistema.

#### Contiene

* Use Cases (uno por intención de negocio)
* Ports In (interfaces de entrada)
* Ports Out (interfaces hacia infraestructura)
* DTOs de aplicación (si aplica)
* Mappers de eventos (Domain → Integration)

#### Reglas

* No contiene lógica de negocio compleja
* Coordina agregados
* Invoca repositorios mediante Ports Out
* Publica eventos mediante un EventPublisher Port
* Un Use Case debe modificar un único agregado raíz por transacción lógica (cuando sea posible)
* Los Domain Events deben publicarse **después de persistir el agregado** (evita inconsistencias)
* La publicación de eventos es best-effort y no afecta el flujo principal

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

### 7.4 Infrastructure

#### Responsabilidad

Implementar detalles técnicos.

#### Contiene

* Controllers (REST)
* DTOs de entrada/salida
* Mappers
* Repositorios JPA
* Configuración de RabbitMQ (publishers y consumers)
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

## 8. Flujos de Ejecución

---

### 8.1 Flujo estándar de un caso de uso

1. Controller recibe request
2. Convierte a comando (DTO → modelo aplicación)
3. Invoca Use Case (Port In)
4. Use Case:
   * Carga agregados
   * Ejecuta lógica de dominio
   * Persiste cambios
   * Publica eventos (si aplica, después del save)
5. Retorna respuesta

---

### 8.2 Flujo de Registro de Kilometraje (fleet-service)

1. Controller recibe request de registro de km
2. Invoca `RegisterMileageUseCase`
3. Use Case:
   * Carga el vehículo
   * Valida coherencia del km (HU-05): rechaza si es negativo o menor al actual; acepta si es igual (sin cambio); advierte si incremento > 2000 km (sin bloquear)
   * Registra `MileageLog` con `recorded_by` obligatorio
   * Actualiza `current_mileage` del vehículo
   * Persiste cambios
   * Publica `MileageRegisteredEvent` vía RabbitMQ (best-effort), incluyendo `vehicleStatus` en el payload
4. Retorna respuesta (con advertencia de incremento excesivo si aplica)

---

### 8.3 Flujo de Generación de Alertas (rules-alerts-service)

1. Consumer de RabbitMQ recibe `MileageRegisteredEvent`
2. **Verifica que `vehicleStatus` sea ACTIVE** (HU-11: no evaluar vehículos inactivos). Si no es ACTIVE, descarta el evento sin procesar
3. Invoca `GenerateAlertUseCase`
4. Use Case:
   * Carga reglas asociadas al tipo de vehículo
   * Evalúa cada regla contra el kilometraje actual y el `warning_threshold_km`
   * Verifica que no exista alerta PENDING duplicada para el mismo vehículo y regla (HU-11: idempotencia)
   * Genera alertas si corresponde
   * Persiste alertas
   * Publica `AlertGeneratedEvent` (best-effort, sin consumidor en el MVP)
5. El flujo dentro del servicio es sincrónico

---

### 8.4 Flujo de Registro de Mantenimiento (rules-alerts-service)

1. Controller recibe request de registro de mantenimiento
2. Invoca `RegisterMaintenanceUseCase`
3. Use Case:
   * Valida que el tipo de servicio sea obligatorio (HU-13)
   * Valida que la fecha no sea futura (HU-13)
   * Valida que el `mileage_at_service` sea válido
   * Crea `MaintenanceRecord`
   * Resuelve alertas activas (PENDING/WARNING) asociadas al vehículo y tipo de servicio (cambia estado a RESOLVED)
   * Persiste cambios
   * Publica `MaintenanceRegisteredEvent` (best-effort, sin consumidor en el MVP)
4. Retorna respuesta

---

### 8.5 Publicación de Eventos

Los eventos son **salidas del sistema**, no mecanismos de coordinación interna.

Flujo:

1. El dominio genera un Domain Event
2. El Use Case lo recoge después del `save`
3. Se transforma a Integration Event mediante EventMapper
4. Invoca `EventPublisherPort`
5. Infrastructure publica en RabbitMQ
6. Los eventos publicados corresponden a Integration Events definidos en `events.md`, no a Domain Events internos

#### Regla crítica

> RabbitMQ en el MVP se usa **solo para publicar eventos de salida**.
> No se usa para coordinar flujo entre casos de uso dentro del mismo servicio.

---

## 9. Convenciones de Nombres y Estructura

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

## 10. Decisiones del MVP (Simplificaciones)

* No se implementa Outbox Pattern
* No se manejan retries avanzados de eventos
* No hay versionado de eventos
* No hay seguridad
* No hay separación de bounded contexts adicional (solo 2 servicios)
* Best-effort en publicación de eventos
* No hay comunicación HTTP directa entre servicios

Estas decisiones son intencionales para priorizar velocidad de entrega.

---

## 11. Evolución Futura

* Separación en microservicios (vehicle-service, rules-alerts-service, maintenance-service)
* Implementación de Outbox Pattern
* Separación en más microservicios si el dominio crece
* Seguridad (JWT / OAuth2)
* Reintentos de eventos y DLQ
* Versionado de eventos
* Observabilidad
* API Gateway
