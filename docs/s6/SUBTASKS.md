# SUBTASKS.md — Tasking y Estimación por Historia de Usuario

**Software:** FleetGuard — Gestión de Flota y Mantenimiento Automotriz  
**Fecha:** 18/03/2026  

**Equipo:**

| Rol | Integrante | País |
|-----|-----------|------|
| QA  | Hans Ortiz | Ecuador |
| DEV | Javier Luis | Colombia |

---
```
Este documento desglosa las tareas técnicas (DEV) y de calidad (QA) para cada Historia de Usuario del MVP, junto con su estimación en Story Points.
```

## Tasking 

### HU-01: Registrar vehículo con tipo asociado
> **Story Points:**  5 DEV + 3 QA = 8 SP

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Crear la tabla vehicle en BD con campos: id, plate, brand, model, year, fuel_type, vin, status, current_mileage, vehicle_type_id |
|     | 2 | Crear la tabla vehicle_type en BD con campos: id, name, description |
|     | 3 | Vincular vehicle con vehicle_type mediante llave foránea en vehicle_type_id |
|     | 4 | Definir DTOs de entrada (placa, marca, modelo, año, combustible, VIN, tipo) y de salida (datos del vehículo creado) |
|     | 5 | Verificar que la placa sea única, VIN de 17 caracteres, tipo obligatorio y estado inicial ACTIVE |
|     | 6 | Exponer endpoint POST /api/vehicles |
|     | 7 | Retornar error claro si la placa ya existe, VIN inválido o tipo no encontrado |
| QA  | 1 | Diseñar matriz de datos: válidos, placa duplicada, VIN inválido, sin tipo |
|     | 2 | Automatizar 4 escenarios Gherkin con Serenity BDD + RestAssured |

### HU-04: Registrar y acumular kilometraje
> **Story Points:**  3 DEV + 2 QA = 5 SP

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Crear la tabla mileage_log en la base de datos con campos: id, vehicle_id, mileage_value, recorded_at, recorded_by |
|     | 2 | Vincular mileage_log con vehicle mediante llave foránea en vehicle_id |
|     | 3 | Definir DTOs de entrada (placa, valor de km, conductor) y de salida (registro creado y current_mileage actualizado) |
|     | 4 | Actualizar el current_mileage del vehículo cada vez que se registre un nuevo valor de km |
|     | 5 | Asegurarse de que el conductor quede registrado en recorded_by, es un campo obligatorio |
|     | 6 | Exponer endpoint POST /api/vehicles/{placa}/mileage |
|     | 7 | Retornar error claro si el vehículo no existe o si el conductor no fue especificado |
| QA  | 1 | Automatizar 2 escenarios Gherkin (registro exitoso, asociación conductor) |
|     | 2 | Prueba de rendimiento: registro toma < 30 segundos |

### HU-05: Validar coherencia del kilometraje
> **Story Points:**  2 DEV + 1 QA = 3 SP

> **Nota:** estas validaciones se ejecutan dentro del endpoint  POST /api/vehicles/{placa}/mileage de HU-04, no exponen un endpoint independiente.

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Verificar que el mileage_value ingresado sea mayor a cero |
|     | 2 | Si el vehículo no tiene kilometraje previo, aceptar cualquier valor mayor a cero como primer registro |
|     | 3 | Verificar que el mileage_value sea mayor al current_mileage actual del vehículo |
|     | 4 | Avisar en la respuesta si el incremento supera 2000 km respecto al último registro, sin bloquear el guardado |
|     | 5 | Retornar error claro si el km es negativo o menor al actual |
| QA  | 1 | Diseñar matriz de datos de borde: menor, negativo, igual, excesivo |
|     | 2 | Automatizar 4 escenarios Gherkin |

### HU-06: Consultar estado de mantenimiento del vehículo
> **Story Points:**  5 DEV + 3 QA = 8 SP

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Consultar las reglas de mantenimiento que aplican según el tipo del vehículo |
|     | 2 | Comparar el current_mileage del vehículo contra el intervalo definido en cada regla |
|     | 3 | Determinar el estado del vehículo según la comparación: AL_DIA (faltan más km que el umbral), PROXIMO (dentro del rango de advertencia), VENCIDO (superó el km límite) |
|     | 4 | Manejar el caso donde el vehículo no tiene reglas configuradas para su tipo |
|     | 5 | Exponer endpoint GET /api/vehicles/{placa}/maintenance-status |
|     | 6 | Retornar error claro si el vehículo no existe |
| QA  | 1 | Diseñar matriz: al día, próximo, vencido, múltiples reglas, sin reglas |
|     | 2 | Automatizar 4 escenarios Gherkin |
|     | 3 | Prueba exploratoria: vehículo con 5+ reglas de diferentes tipos |

### HU-07: Crear regla con tipo de mantenimiento
> **Story Points:**  3 DEV + 2 QA = 5 SP

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Crear la tabla maintenance_rule en la base de datos con campos: id, name, maintenance_type, interval_km, warning_threshold_km, status |
|     | 2 | Definir los DTOs de entrada (nombre, tipo de mantenimiento, intervalo en km, umbral de advertencia) y de salida (regla creada con su estado) |
|     | 3 | Verificar que el nombre sea obligatorio y que el interval_km sea mayor a cero |
|     | 4 | Asegurarse de que el warning_threshold_km sea configurable y opcional, con un valor por defecto si no se especifica |
|     | 5 | Registrar la regla con estado ACTIVE al momento de crearla |
|     | 6 | Exponer endpoint POST /api/maintenance-rules |
|     | 7 | Retornar error claro si el nombre está vacío o si el interval_km es cero o negativo |
| QA  | 1 | Diseñar matriz: completa, sin nombre, intervalo 0, umbral personalizado |
|     | 2 | Automatizar 4 escenarios Gherkin |
|     | 3 | Prueba exploratoria: valores límite (intervalos muy altos/bajos) |

### HU-09: Asociar regla a tipo de vehículo
> **Story Points:**  3 DEV + 2 QA = 5 SP

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Crear la tabla rule_vehicle_type_assoc en la base de datos con campos: id, rule_id, vehicle_type_id, created_at |
|     | 2 | Definir restricción única sobre la combinación rule_id + vehicle_type_id para evitar duplicados |
|     | 3 | Vincular rule_vehicle_type_assoc con maintenance_rule mediante llave foránea en rule_id |
|     | 4 | Vincular rule_vehicle_type_assoc con vehicle_type mediante llave foránea en vehicle_type_id |
|     | 5 | Definir DTOs de entrada (id de regla, id de tipo de vehículo) y de salida (asociación creada) |
|     | 6 | Exponer endpoint POST /api/maintenance-rules/{id}/vehicle-types |
|     | 7 | Retornar error claro si la asociación ya existe o si la regla o el tipo de vehículo no fueron encontrados |
| QA  | 1 | Automatizar 3 escenarios Gherkin (uno, múltiples, duplicado) |
|     | 2 | Verificar que la asociación se refleja correctamente en la evaluación de HU-06 y HU-11 |

### HU-11: Generar alerta automática por kilometraje
> **Story Points:**  8 DEV + 5 QA = 13 SP

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Crear la tabla maintenance_alert en la base de datos con campos: id, vehicle_id, rule_id, status, triggered_at, due_at_km |
|     | 2 | Vincular maintenance_alert con vehicle mediante llave foránea en vehicle_id |
|     | 3 | Vincular maintenance_alert con maintenance_rule mediante llave foránea en rule_id |
|     | 4 | Comparar el current_mileage del vehículo contra el due_at_km de cada regla asociada a su tipo para saber si ya toca generar una alerta |
|     | 5 | Antes de crear una alerta, revisar que no exista ya una en estado PENDING para ese vehículo y esa regla |
|     | 6 | Configurar un proceso que corra automáticamente y revise todos los vehículos activos para generar alertas cuando corresponda |
|     | 7 | Retornar error claro si el vehículo o la regla no fueron encontrados |
| QA  | 1 | Diseñar matriz: alcanza umbral, lejos, duplicado, inactivo, exacto |
|     | 2 | Automatizar 5 escenarios Gherkin |
|     | 3 | Prueba exploratoria: km masivo, verificar no duplicación |
|     | 4 | Prueba de borde: km exacto en umbral, vehículo sin reglas |

### HU-12: Consultar y clasificar alertas
> **Story Points:**  2 DEV + 1 QA = 3 SP

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Exponer endpoint GET /api/alerts?status={estado} que permita filtrar alertas por estado de forma opcional |
|     | 2 | Si no se especifica filtro, retornar todas las alertas que no estén en estado RESOLVED por defecto |
|     | 3 | Incluir en la respuesta el vehículo, la regla, el estado y la fecha de cada alerta |
|     | 4 | Cuando una alerta en estado WARNING supere el due_at_km del vehículo, cambiar su estado a OVERDUE automáticamente |
|     | 5 | Retornar error claro si el estado enviado como filtro no es un valor válido |
| QA  | 1 | Automatizar 4 escenarios Gherkin (filtrar, listar, escalar, ocultar resueltas) |
|     | 2 | Probar transiciones: PENDING → WARNING → OVERDUE → RESOLVED |

### HU-13: Registrar mantenimiento
> **Story Points:**  5 DEV + 3 QA = 8 SP

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Crear la tabla maintenance_record en la base de datos con campos: id, vehicle_id, alert_id, rule_id, description, cost, provider, performed_at, mileage_at_service |
|     | 2 | Vincular maintenance_record con vehicle mediante llave foránea en vehicle_id |
|     | 3 | Vincular maintenance_record con maintenance_alert mediante llave foránea en alert_id |
|     | 4 | Vincular maintenance_record con maintenance_rule mediante llave foránea en rule_id |
|     | 5 | Definir DTOs de entrada (placa, tipo de servicio, costo, proveedor, observaciones) y de salida (registro creado) |
|     | 6 | Verificar que el tipo de servicio sea obligatorio al momento de registrar |
|     | 7 | Si existe una alerta PENDING o WARNING asociada al vehículo y al tipo de servicio, cambiar su estado a RESOLVED al guardar el registro |
|     | 8 | Exponer endpoint POST /api/vehicles/{placa}/maintenance |
|     | 9 | Retornar error claro si el vehículo no existe o si el tipo de servicio no fue especificado |
| QA  | 1 | Automatizar 3 escenarios Gherkin (completo, sin tipo, resolver alerta) |
|     | 2 | Verificar que la alerta cambia de estado correctamente al registrar el mantenimiento |

### HU-14: Asociar mantenimiento a regla aplicada
> **Story Points:**  3 DEV + 2 QA = 5 SP

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Calcular el próximo servicio sumando el interval_km de la regla al mileage_at_service del mantenimiento registrado y guardarlo en due_at_km |
|     | 2 | Verificar que la regla que se quiere asociar corresponda al tipo del vehículo antes de vincularla |
|     | 3 | Si el mantenimiento se registra sin una alerta previa, resetear el contador desde el mileage_at_service actual del vehículo igualmente |
|     | 4 | Exponer endpoint PATCH /api/maintenance/{id}/rule |
|     | 5 | Retornar error claro si la regla no existe o no corresponde al tipo del vehículo |
| QA  | 1 | Automatizar 4 escenarios Gherkin (reseteo, sin alerta, resolver, cálculo) |
|     | 2 | Verificar cálculo correcto del próximo servicio con datos controlados |
|     | 3 | Prueba exploratoria: múltiples reglas activas para un vehículo |

### HU-16: Registrar fecha y km del servicio
> **Story Points:**  2 DEV + 1 QA = 3 SP

| Rol | # | Tarea |
|-----|---|-------|
| DEV | 1 | Asegurarse de que los campos performed_at y mileage_at_service se reciban en los datos de entrada del registro de mantenimiento y queden guardados correctamente |
|     | 2 | Verificar que el mileage_at_service no sea mayor al current_mileage actual del vehículo al momento del registro |
|     | 3 | Verificar que el performed_at no sea una fecha futura |
|     | 4 | Retornar error claro si el km del servicio supera el actual o si la fecha ingresada es futura |
| QA  | 1 | Automatizar 3 escenarios Gherkin (válido, km mayor, fecha futura) |
|     | 2 | Prueba exploratoria: servicio de hace 1 mes, verificar cálculos |


## Resumen de Estimación 

| HU | Descripción | SP DEV | Justificación DEV | SP QA | Justificación QA | Total |
|---|---|---|---|---|---|---|
| HU-01 | Registrar vehículo | 5 | 7 tareas, complejidad alta | 3 | 2 tareas, complejidad media | 8 |
| HU-04 | Registrar kilometraje | 3 | 7 tareas, complejidad media | 2 | 2 tareas, complejidad baja | 5 |
| HU-05 | Validar coherencia km | 2 | 5 tareas, complejidad baja | 1 | 2 tareas, complejidad media | 3 |
| HU-06 | Consultar estado mantenimiento | 5 | 6 tareas, complejidad alta | 3 | 3 tareas, complejidad media | 8 |
| HU-07 | Crear regla | 3 | 7 tareas, complejidad media | 2 | 3 tareas, complejidad baja | 5 |
| HU-09 | Asociar regla a tipo | 3 | 7 tareas, complejidad media | 2 | 2 tareas, complejidad baja | 5 |
| HU-11 | Generar alerta automática | 8 | 7 tareas, complejidad muy alta | 5 | 4 tareas, complejidad alta | 13 |
| HU-12 | Consultar alertas | 2 | 5 tareas, complejidad baja | 1 | 2 tareas, complejidad media | 3 |
| HU-13 | Registrar mantenimiento | 5 | 9 tareas, complejidad alta | 3 | 2 tareas, complejidad media | 8 |
| HU-14 | Asociar mantenimiento a regla | 3 | 5 tareas, complejidad media | 2 | 3 tareas, complejidad media | 5 |
| HU-16 | Registrar fecha y km | 2 | 4 tareas, complejidad baja | 1 | 2 tareas, complejidad baja | 3 |
---