# 📄 `context.md – FleetGuard MVP`

## 1. Propósito del Sistema

FleetGuard es un sistema orientado a la gestión de mantenimiento de vehículos basado en kilometraje.

Su objetivo es:

* Registrar vehículos
* Registrar kilometraje de uso
* Definir reglas de mantenimiento
* Generar alertas automáticas
* Registrar mantenimientos realizados

El sistema implementa un flujo donde los eventos de uso del vehículo desencadenan acciones automáticas dentro del dominio.

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

## Regla de separación de responsabilidades

* El **Administrador de Flota** gestiona la configuración del sistema
* El **Conductor** genera datos operativos (kilometraje)
* La generación de alertas es automática y no depende directamente de un actor

---

## 4. Lenguaje Ubicuo (Dominio)

---

### Vehicle

Entidad principal del sistema.

Responsabilidad:

* Representar un vehículo gestionado

Reglas:

* Debe tener placa única
* Debe tener tipo de vehículo
* Puede registrar historial de kilometraje

---

### VehicleType

Entidad que clasifica vehículos.

Responsabilidad:

* Permitir asociar reglas de mantenimiento

---

### MileageLog

Entidad que representa un registro de kilometraje.

Reglas:

* El kilometraje debe ser **siempre creciente**
* No se permiten valores negativos
* Se registra asociado a un vehículo

---

### MaintenanceRule

Agregado que define condiciones de mantenimiento.

Responsabilidad:

* Definir cuándo se debe generar una alerta

Reglas:

* Se basa únicamente en kilometraje
* Debe tener un intervalo válido (> 0)
* Puede asociarse a uno o más tipos de vehículo

---

### MaintenanceAlert

Agregado que representa una alerta generada.

Estados posibles:

* PENDING
* WARNING
* OVERDUE
* RESOLVED

Responsabilidad:

* Indicar que un vehículo requiere mantenimiento

---

### MaintenanceRecord

Agregado que representa un mantenimiento realizado.

Responsabilidad:

* Registrar la ejecución de un mantenimiento

Reglas:

* Debe tener tipo de servicio
* Debe tener kilometraje válido
* No puede tener fecha futura

---

## 5. Reglas de Negocio

---

### Kilometraje

* Siempre debe ser creciente
* No puede ser negativo
* Es la base para generación de alertas

---

### Reglas de Mantenimiento

* Se definen por kilometraje
* Se aplican por tipo de vehículo
* No aplican directamente a vehículos individuales

---

### Alertas

* Se generan automáticamente al registrar kilometraje
* La generación ocurre de forma **sincrónica** dentro del mismo servicio, como parte del flujo de registro de kilometraje
* No dependen de un scheduler ni de un proceso batch externo
* Dependen de reglas asociadas al tipo del vehículo
* Representan estado de mantenimiento requerido

---

### Mantenimiento

* Representa la acción que resuelve una necesidad
* Se registra manualmente
* Cierra el ciclo del sistema

---

## 6. Eventos de Dominio (Conceptual)

El sistema opera basado en eventos de negocio.

### Eventos definidos en el MVP

* `MileageRegistered`
* `AlertGenerated`
* `MaintenanceRegistered`

Características:

* Representan hechos que ya ocurrieron
* No son comandos
* No tienen conocimiento de consumidores
* Pueden ser procesados más de una vez

---

## 7. Flujo Principal del Sistema

El flujo central del negocio es:

1. Se registra un vehículo
2. Se registra kilometraje (validación incluida)
3. Se evalúan reglas del tipo de vehículo — **dentro del mismo flujo**
4. Se genera alerta si corresponde — **dentro del mismo flujo**
5. Se publica evento de integración (asíncrono, no bloquea)
6. Se registra mantenimiento (acción manual posterior)

---

## 8. Consistencia

El sistema opera bajo el siguiente modelo de consistencia:

* El registro de kilometraje y la generación de alertas son **síncronos** y ocurren en el mismo request
* La publicación de eventos hacia RabbitMQ es **asíncrona** y no bloquea el flujo principal
* Si la publicación del evento falla, la operación principal no se revierte (sin Outbox en MVP)

---

## 9. Fuera de Alcance

* Seguridad
* Auditoría avanzada
* Notificaciones externas
* Reglas híbridas (tiempo + km)
* Integraciones externas
* Schedulers o procesos batch para generación de alertas
* Outbox Pattern
* Reintentos de eventos