# 📄 Architecture.md


## 1. Visión General

El sistema se diseña como una aplicación backend basada en **Arquitectura Hexagonal (Ports & Adapters)**, con un enfoque en:

* Separación estricta de responsabilidades
* Dominio desacoplado de frameworks
* Alta testabilidad
* Evolución progresiva hacia un sistema event-driven

El sistema permite gestionar vehículos, registrar kilometraje, definir reglas de mantenimiento y generar alertas basadas en dichas reglas.

La comunicación interna del sistema es **sincrónica**, mientras que la emisión de eventos se realiza de forma **asíncrona mediante RabbitMQ**.

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

* Arquitectura de microservicios ligeros, donde cada servicio implementa arquitectura hexagonal
* Comunicación interna sincrónica
* Publicación de eventos simple (sin outbox, sin retries avanzados)
* Sin seguridad
* Persistencia con PostgreSQL
* Uso de RabbitMQ para eventos de integración

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

### 6.2 Generación de Alertas (decisión B)

La generación de alertas se maneja mediante un caso de uso explícito:

```
GenerateAlertUseCase
```

Flujo:

1. Se registra kilometraje
2. Se ejecuta `GenerateAlertUseCase`
3. Se evalúan reglas asociadas al tipo de vehículo
4. Se generan alertas si corresponde
5. Se persisten alertas
6. Se emite evento (opcional)

---

### 6.3 Publicación de Eventos

Flujo:

1. El dominio genera un Domain Event
2. El Use Case lo recoge
3. Invoca `EventPublisherPort`
4. Infrastructure publica en RabbitMQ

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
* No hay separación de bounded contexts

Estas decisiones son intencionales para priorizar velocidad de entrega.

---

## 9. Evolución Futura

* Implementación de Outbox Pattern
* Separación en microservicios
* Seguridad (JWT / OAuth2)
* Reintentos de eventos
* Versionado de eventos
* Observabilidad
