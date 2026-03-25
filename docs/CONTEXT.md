# 📄 `context.md – FleetGuard MVP`

## 1. Propósito del Sistema

FleetGuard es un sistema orientado a la gestión de mantenimiento de vehículos basado en kilometraje.

Su objetivo es:

* Registrar vehículos
* Registrar kilometraje de uso
* Definir reglas de mantenimiento
* Generar alertas automáticas
* Registrar mantenimientos realizados

El sistema implementa un flujo donde el registro de kilometraje desencadena la evaluación de reglas de mantenimiento y la generación de alertas de forma automática.

---

## 2. Alcance del MVP

El MVP cubre las siguientes capacidades:

### Incluidas

* HU-01 → Registrar vehículo
* HU-04 → Registrar kilometraje
* HU-05 → Validar kilometraje
* HU-07 → Crear regla de mantenimiento
* HU-09 → Asociar regla a tipo de vehículo
* HU-11 → Generar alertas automáticamente
* HU-13 → Registrar mantenimiento

### No incluidas

* Autenticación y autorización
* Notificaciones (email, push, etc.)
* Multi-tenant
* Gestión de usuarios
* Reglas complejas (tiempo + km combinados)

---

## 3. Actores del Sistema

### Administrador de Flota

Representa al usuario responsable de la gestión del sistema.

Responsabilidades:

* Registrar vehículos
* Definir reglas de mantenimiento
* Asociar reglas a tipos de vehículo
* Registrar mantenimientos realizados

---

### Conductor

Representa al usuario que opera el vehículo en uso.

Responsabilidades:

* Registrar kilometraje
* Consultar alertas de mantenimiento

---

### Regla de separación de responsabilidades

* El **Administrador de Flota** gestiona la configuración del sistema
* El **Conductor** genera datos operativos (kilometraje)
* La generación de alertas es automática y no depende directamente de un actor

---

## 4. Lenguaje Ubicuo (Dominio)

---

### Vehicle

Agregado raíz que representa un vehículo gestionado.

Responsabilidad:

* Representar un vehículo de la flota con su estado y tipo

Reglas:

* Debe tener placa única
* Debe tener tipo de vehículo
* Mantiene el `current_mileage` como referencia del kilometraje acumulado
* Al registrar un nuevo kilometraje, el `current_mileage` se actualiza

---

### VehicleType

Entidad que clasifica vehículos.

Responsabilidad:

* Permitir asociar reglas de mantenimiento a grupos de vehículos

---

### MileageLog

Agregado raíz que representa un registro individual de kilometraje.

Responsabilidad:

* Registrar el valor de kilometraje reportado por un conductor en un momento específico

Reglas:

* El kilometraje debe ser **siempre creciente** respecto al `current_mileage` del vehículo
* No se permiten valores negativos ni cero
* Se registra asociado a un vehículo y a un conductor
* Es un registro inmutable una vez persistido

---

### MaintenanceRule

Agregado raíz que define condiciones de mantenimiento.

Responsabilidad:

* Definir cuándo se debe generar una alerta

Reglas:

* Se basa únicamente en kilometraje
* Debe tener un intervalo válido (> 0)
* Puede asociarse a uno o más tipos de vehículo
* Tiene un umbral de advertencia (`warning_threshold_km`) configurable

---

### MaintenanceAlert

Agregado raíz que representa una alerta generada.

Estados posibles:

* PENDING
* WARNING
* OVERDUE
* RESOLVED

Responsabilidad:

* Indicar que un vehículo requiere mantenimiento

Reglas:

* Se genera cuando el `current_mileage` del vehículo alcanza el umbral definido por la regla
* No se duplican alertas: no puede existir más de una alerta PENDING para el mismo vehículo y la misma regla
* Puede escalar de WARNING a OVERDUE si el vehículo supera el límite
* Se resuelve (RESOLVED) al registrar un mantenimiento

---

### MaintenanceRecord

Agregado raíz que representa un mantenimiento realizado.

Responsabilidad:

* Registrar la ejecución de un mantenimiento

Reglas:

* Debe tener tipo de servicio
* Debe tener kilometraje válido
* No puede tener fecha futura
* Al registrarse, resuelve alertas activas asociadas al vehículo y tipo de servicio

---

## 5. Reglas de Negocio

---

### Kilometraje

* Siempre debe ser creciente respecto al `current_mileage` del vehículo
* No puede ser negativo ni cero
* Es la base para generación de alertas
* Un incremento excesivo (> 2000 km) genera una advertencia pero no bloquea el registro

---

### Reglas de Mantenimiento

* Se definen por kilometraje
* Se aplican por tipo de vehículo
* No aplican directamente a vehículos individuales
* Tienen un umbral de advertencia configurable

---

### Alertas

* Se generan automáticamente al registrar kilometraje
* Dependen de reglas asociadas al tipo del vehículo
* Representan estado de mantenimiento requerido
* Son idempotentes: no se crean duplicados para el mismo vehículo y regla
* No se evalúan para vehículos inactivos

---

### Mantenimiento

* Representa la acción que resuelve una necesidad
* Se registra manualmente por el Administrador
* Cierra el ciclo del sistema resolviendo alertas activas

---

## 6. Eventos (Conceptual)

El sistema maneja eventos en dos niveles:

### Domain Events (internos)

Eventos generados dentro del dominio para representar cambios de estado relevantes.

Ejemplos:

* `MileageRegistered`
* `AlertGenerated`
* `MaintenanceRegistered`

Características:

* Son internos al dominio
* No dependen de infraestructura
* No representan contratos de integración

---

### Integration Events (externos)

Eventos publicados hacia el exterior del sistema, definidos en `events.md`.

Características:

* Son derivados de Domain Events
* Se publican mediante RabbitMQ
* Permiten la comunicación entre servicios
* No afectan el flujo principal del negocio si fallan (best-effort en el MVP)

---

## 7. Comunicación entre Servicios

En el MVP, el sistema se compone de microservicios ligeros:

* `fleet-service`: gestiona vehículos y kilometraje (HU-01, HU-04, HU-05)
* `rules-alerts-service`: gestiona reglas, alertas y mantenimientos (HU-07, HU-09, HU-11, HU-13)

### Flujo de comunicación

* `fleet-service` publica `MileageRegisteredEvent` al registrar kilometraje
* `rules-alerts-service` consume el evento para evaluar reglas y generar alertas
* La generación de alertas dentro de `rules-alerts-service` es sincrónica
* Los eventos de integración entre servicios se transmiten mediante RabbitMQ

---

## 8. Flujo Principal del Sistema

El flujo central del negocio es:

1. Se registra un vehículo (fleet-service)
2. Se registra kilometraje (fleet-service)
3. Se valida el kilometraje (fleet-service)
4. Se publica `MileageRegisteredEvent` (fleet-service → RabbitMQ)
5. Se evalúan reglas (rules-alerts-service)
6. Se genera alerta si aplica (rules-alerts-service)
7. Se registra mantenimiento (rules-alerts-service)

---

## 9. Consistencia

El sistema opera bajo **consistencia fuerte dentro de cada servicio** y **consistencia eventual entre servicios**:

* El registro de kilometraje y la actualización de `current_mileage` son inmediatos dentro de `fleet-service`
* La generación de alertas es sincrónica dentro de `rules-alerts-service`
* La comunicación entre servicios es asíncrona vía RabbitMQ
* La publicación de eventos es best-effort en el MVP (sin outbox, sin retries avanzados)

---

## 10. Fuera de Alcance

* Seguridad
* Auditoría avanzada
* Notificaciones externas
* Reglas híbridas (tiempo + km)
* Integraciones externas
* Outbox Pattern
* Reintentos avanzados de eventos