# Análisis de Cumplimiento vs Implementación

## 1. Resumen

El MVP de FleetGuard fue **cumplido en su ciclo core** con las 7 HUs definidas en `PRD_REDUCED.md` implementadas y funcionales en backend. Adicionalmente, se implementaron funcionalidades correspondientes a HUs que fueron explícitamente diferidas en S7 (HU-12, HU-16 parcial), y se agregó un frontend completo en Next.js que **no estaba contemplado en la documentación técnica de S7**.

| Aspecto | Estado |
|---------|--------|
| Ciclo core (HU-01→HU-04→HU-05→HU-11→HU-13) | **Implementado** |
| HUs core S7 (7/7) | **7/7 completadas** |
| HUs diferidas S7 implementadas | HU-12 (completa), HU-16 (parcial) |
| HUs diferidas S7 no implementadas | HU-06, HU-14 (endpoint explícito) |
| Arquitectura hexagonal + microservicios | **Cumplida con desviaciones menores** |
| Frontend | **Implementado (fuera del alcance documentado)** |
| Tests unitarios | **Implementados en los 3 componentes** |

La conclusión general es: **MVP cumplido con extensiones**, con desviaciones técnicas documentadas en las secciones siguientes.

---

## 2. Alcance del MVP: Planeado vs Implementado

### Fuentes de referencia

- **PRD** (`docs/s6/PRD.md`): Define 11 HUs como alcance del MVP original.
- **PRD_REDUCED** (`docs/s7/PRD_REDUCED.md`): Reduce a 7 HUs core, difiere 4 HUs (HU-06, HU-12, HU-14, HU-16).
- **SUBTASKS** (`docs/s6/SUBTASKS.md`): Tasking técnico para las 11 HUs.

### 2.1 Funcionalidades implementadas correctamente

#### HU-01 — Registrar vehículo con tipo asociado ✅

| Subtarea planificada | Estado | Evidencia |
|---------------------|--------|-----------|
| Tabla `vehicle` en BD | ✅ | `fleet-service/src/main/resources/db/migration/V1__create_tables.sql` — tabla `vehicle` con campos: `id`, `plate`, `brand`, `model`, `year`, `fuel_type`, `vin`, `status`, `current_mileage`, `vehicle_type_id` |
| Tabla `vehicle_type` en BD | ✅ | Misma migración — tabla `vehicle_type` con `id`, `name`, `description` |
| FK `vehicle` → `vehicle_type` | ✅ | `vehicle_type_id UUID NOT NULL REFERENCES vehicle_type(id)` |
| DTOs de entrada y salida | ✅ | `RegisterVehicleRequest` (7 campos con `@NotBlank`/`@NotNull`), `VehicleResponse` (10 campos) |
| Placa única, VIN 17 chars, tipo obligatorio, estado ACTIVE | ✅ | `Plate` VO (uppercase, not blank), `Vin` VO (exactly 17 chars), `Vehicle.create()` fuerza `VehicleStatus.ACTIVE` y `Mileage.zero()`. Placa y VIN: `existsByPlate()` / `existsByVin()` en `RegisterVehicleService` |
| Endpoint `POST /api/vehicles` | ✅ | `VehicleController.registerVehicle()` — `@PostMapping`, retorna `201 CREATED` |
| Errores claros | ✅ | `GlobalExceptionHandler`: `DuplicatePlateException` → 409, `DuplicateVinException` → 409, `VehicleTypeNotFoundException` → 404, `InvalidVinException` → 400 |

**Dato adicional no planificado:** Se insertaron 15 tipos de vehículo pre-cargados en `V2__insert_vehicle_types.sql` (Sedán, SUV, Pickup, Camión, etc.).

---

#### HU-04 — Registrar y acumular kilometraje ✅

| Subtarea planificada | Estado | Evidencia |
|---------------------|--------|-----------|
| Tabla `mileage_log` | ✅ | `V1__create_tables.sql` — campos: `id`, `vehicle_id`, `previous_mileage`, `mileage_value`, `km_traveled`, `recorded_by`, `recorded_at`, `excessive_increment`, `vehicle_type_id` |
| FK `mileage_log` → `vehicle` | ✅ | `vehicle_id UUID NOT NULL REFERENCES vehicle(id)` |
| DTOs entrada/salida | ✅ | `RegisterMileageRequest` (`mileageValue`, `recordedBy`), `MileageResponse` (11 campos) |
| Actualizar `current_mileage` | ✅ | `RegisterMileageService`: llama `vehicle.updateMileage(newMileage)` y luego `vehicleRepositoryPort.save(vehicle)` |
| `recorded_by` obligatorio | ✅ | `MileageLog.create()` valida `recordedBy` no nulo/blanco, lanza `MissingRecordedByException` |
| Endpoint `POST /api/vehicles/{placa}/mileage` | ✅ | `MileageController` — `@PostMapping("/{plate}/mileage")`, retorna `201 CREATED` |
| Errores claros | ✅ | `VehicleNotFoundException` → 404, `MissingRecordedByException` → 400 |

**Campos adicionales respecto al plan:** `previous_mileage`, `km_traveled`, `vehicle_type_id` en `mileage_log` — no estaban en SUBTASKS pero mejoran la trazabilidad.

---

#### HU-05 — Validar coherencia del kilometraje ✅

| Subtarea planificada | Estado | Evidencia |
|---------------------|--------|-----------|
| `mileage_value` > 0 | ✅ | `Mileage` VO: `if (value < 0) throw InvalidMileageException`. Además, `Vehicle.updateMileage()` valida `newMileage.getValue() <= 0` |
| Primer registro acepta valor > 0 | ✅ | `Mileage.assertNewMileageIsNotLower()`: si `currentMileage = 0`, cualquier valor positivo es aceptado |
| `mileage_value` > `current_mileage` | ✅ | `Mileage.assertNewMileageIsNotLower()` lanza `InvalidMileageException` si `newMileage < this.value` |
| Advertencia incremento > 2000 km | ✅ | `Mileage.isExcessiveIncrement(previous)`: `return (this.value - previous.value) > 2000`. Flag `excessiveIncrement` en respuesta |
| Errores claros | ✅ | `InvalidMileageException` → 400, `InactiveVehicleException` → 400 |

**Nota:** La HU-05 se implementa correctamente dentro del flujo de HU-04, sin endpoint independiente, tal como indica SUBTASKS.

---

#### HU-07 — Crear regla con tipo de mantenimiento ✅

| Subtarea planificada | Estado | Evidencia |
|---------------------|--------|-----------|
| Tabla `maintenance_rule` | ✅ | `rules-alerts-service/.../V1__create_tables.sql`: `id`, `name`, `maintenance_type`, `interval_km`, `warning_threshold_km`, `status`, `created_at`, `updated_at` |
| DTOs entrada/salida | ✅ | `CreateMaintenanceRuleRequest` (4 campos, `@NotBlank`, `@Min(1)`), `MaintenanceRuleResponse` (8 campos) |
| Nombre obligatorio, `interval_km` > 0 | ✅ | DTO: `@NotBlank` en `name`, `@Min(value = 1)` en `intervalKm`. Servicio: `existsByName()` verifica unicidad |
| `warning_threshold_km` configurable con default | ✅ | `CreateMaintenanceRuleService`: si `null` o `< 1`, usa `MaintenanceRuleConstants.DEFAULT_WARNING_THRESHOLD_KM` (500) |
| Estado ACTIVE al crear | ✅ | `CreateMaintenanceRuleService`: `status = "ACTIVE"` |
| Endpoint `POST /api/maintenance-rules` | ✅ | `MaintenanceRuleController` — `@PostMapping`, retorna `201 CREATED` |
| Errores claros | ✅ | Nombre vacío → 400 (`MethodArgumentNotValidException`), nombre duplicado → 409 (`DuplicateAssociationException`) |

---

#### HU-09 — Asociar regla a tipo de vehículo ✅

| Subtarea planificada | Estado | Evidencia |
|---------------------|--------|-----------|
| Tabla `rule_vehicle_type_assoc` | ✅ | `V1__create_tables.sql`: `id`, `rule_id`, `vehicle_type_id`, `created_at` con `CONSTRAINT uk_rule_vehicle_type UNIQUE (rule_id, vehicle_type_id)` |
| FK `rule_vehicle_type_assoc` → `maintenance_rule` | ✅ | `rule_id UUID NOT NULL REFERENCES maintenance_rule(id)` |
| FK a `vehicle_type` | ⚠️ | No hay FK explícita a `vehicle_type` porque la tabla `vehicle_type` pertenece a `fleet-service` (otra BD). `vehicle_type_id UUID NOT NULL` sin REFERENCES |
| Restricción única `rule_id + vehicle_type_id` | ✅ | `UNIQUE (rule_id, vehicle_type_id)` en SQL + `@UniqueConstraint` en JPA entity |
| DTOs entrada/salida | ✅ | `AssociateVehicleTypeRequest` (`vehicleTypeId`), `RuleVehicleTypeAssocResponse` (4 campos) |
| Endpoint `POST /api/maintenance-rules/{id}/vehicle-types` | ✅ | `MaintenanceRuleController` — `@PostMapping("/{id}/vehicle-types")`, retorna `201 CREATED` |
| Errores claros | ✅ | Regla no encontrada → `MaintenanceRuleNotFoundException` → 404, duplicado → `DuplicateAssociationException` → 409 |

---

#### HU-11 — Generar alerta automática por kilometraje ✅

| Subtarea planificada | Estado | Evidencia |
|---------------------|--------|-----------|
| Tabla `maintenance_alert` | ✅ | `V1__create_tables.sql`: `id`, `vehicle_id`, `vehicle_type_id`, `rule_id`, `status`, `triggered_at`, `due_at_km` |
| FK a `vehicle` y `maintenance_rule` | ⚠️ | FK a `maintenance_rule` presente. FK a `vehicle` no presente (BD separada). `vehicle_id UUID NOT NULL` sin REFERENCES |
| Comparar `current_mileage` vs reglas | ✅ | `EvaluateMaintenanceAlertsService.evaluate()`: calcula `dueAtKm = ((currentMileage / intervalKm) + 1) * intervalKm`, compara `kmRemaining` vs `warningThresholdKm` |
| No duplicar alertas PENDING | ✅ | `findByVehicleIdAndRuleIdAndDueAtKm()` verifica existencia antes de crear. Si existe con status `RESOLVED`, ignora |
| Proceso automático / scheduler | ✅ | `MileageRegisteredConsumer` con `@RabbitListener(queues = "mileage.registered.queue")` — se evalúa cada vez que llega un evento de kilometraje |
| No evaluar vehículos inactivos | ✅ | `EvaluateMaintenanceAlertsService.evaluate()`: `if (!"ACTIVE".equals(vehicleStatus)) return` — primer check |
| Estados: PENDING, WARNING, OVERDUE | ✅ | `resolveStatus()`: `kmRemaining <= 0` → OVERDUE, `kmRemaining <= warningThresholdKm/3` → WARNING, else → PENDING |

**Diferencia con planificación:** El plan original (SUBTASKS) describía un "scheduler que revise todos los vehículos activos". La implementación real es **event-driven**: se evalúa al recibir `MileageRegisteredEvent` vía RabbitMQ, no mediante un scheduler periódico. Esto es coherente con `ARCHITECTURE.md` S7 que define comunicación asíncrona vía MQ.

---

#### HU-13 — Registrar mantenimiento ✅

| Subtarea planificada | Estado | Evidencia |
|---------------------|--------|-----------|
| Tabla `maintenance_record` | ✅ | `V1__create_tables.sql`: `id`, `vehicle_id`, `alert_id`, `rule_id`, `service_type`, `description`, `cost`, `provider`, `performed_at`, `mileage_at_service`, `recorded_by` |
| FKs a `vehicle`, `alert`, `rule` | ⚠️ | FK a `maintenance_alert` y `maintenance_rule` presentes. FK a `vehicle` no presente (BD separada) |
| DTOs entrada/salida | ✅ | `RegisterMaintenanceRequest` (8 campos con validaciones), `MaintenanceRecordResponse` (12 campos) |
| Tipo servicio obligatorio | ✅ | `@NotBlank` en DTO + `MaintenanceRecord` constructor: `if (serviceType == null \|\| serviceType.isBlank()) throw InvalidMaintenanceException` |
| Resolver alerta al registrar | ✅ | `RegisterMaintenanceService`: obtiene alerta por `alertId`, actualiza status a `"RESOLVED"`, guarda alerta |
| Endpoint | ✅ | `MaintenanceController` — `POST /api/maintenance/{plate}`, retorna `201 CREATED` |

**Diferencia con planificación:** El endpoint planeado era `POST /api/vehicles/{placa}/maintenance` (en fleet-service). La implementación real es `POST /api/maintenance/{plate}` (en rules-alerts-service). Esto es **coherente con ARCHITECTURE.md S7** que ubica HU-13 en `rules-alerts-service`.

---

### 2.2 Funcionalidades parcialmente implementadas

#### HU-16 — Registrar fecha y km del servicio (DIFERIDA pero parcialmente implementada)

| Subtarea | Estado | Evidencia |
|----------|--------|-----------|
| Campos `performed_at` y `mileage_at_service` en request | ✅ | `RegisterMaintenanceRequest`: `performedAt` (`@NotNull`), `mileageAtService` (`@NotNull @Positive`) |
| Validar `mileage_at_service` ≤ `current_mileage` | ❌ | **No implementado** — `rules-alerts-service` no tiene acceso al `current_mileage` del vehículo. Solo valida `> 0` |
| Validar `performed_at` no futura | ✅ | `MaintenanceRecord` constructor: `if (performedAt.isAfter(LocalDateTime.now())) throw InvalidMaintenanceException` |
| Cálculo próximo mantenimiento desde km del servicio | ❌ | El cálculo de `dueAtKm` en `EvaluateMaintenanceAlertsService` usa `currentMileage` del evento, no `mileageAtService` del registro |

**Conclusión:** Los campos se reciben y persisten, pero las dos validaciones/cálculos cruzados que requieren datos del vehículo no son viables sin comunicación inter-servicio.

---

#### HU-14 — Asociar mantenimiento a regla y resetear contador (DIFERIDA, parcialmente cubierta)

| Subtarea | Estado | Evidencia |
|----------|--------|-----------|
| Endpoint `PATCH /api/maintenance/{id}/rule` | ❌ | No existe endpoint dedicado |
| Calcular próximo servicio sumando `interval_km` | ⚠️ | `EvaluateMaintenanceAlertsService` calcula `dueAtKm = ((currentMileage / intervalKm) + 1) * intervalKm` — esto NO es `mileageAtService + intervalKm` como describe HU-14, sino un cálculo basado en ciclos |
| Resolver alerta al asociar | ✅ | `RegisterMaintenanceService` resuelve la alerta vinculada al `alertId` |
| Mantenimiento sin alerta previa | ❌ | `RegisterMaintenanceService` requiere `alertId` obligatorio (`@NotNull`). No se puede registrar mantenimiento voluntario sin alerta |

**Conclusión:** El ciclo de reseteo se maneja implícitamente a través de la fórmula de ciclos en la evaluación de alertas, pero no existe un endpoint explícito ni se soporta el caso de mantenimiento voluntario sin alerta.

---

### 2.3 Funcionalidades no implementadas

#### HU-06 — Consultar estado de mantenimiento del vehículo ❌

- **Planeado:** Endpoint `GET /api/vehicles/{placa}/maintenance-status` que retorne estado AL_DIA / PROXIMO / VENCIDO.
- **Implementado:** No existe este endpoint en ningún servicio.
- **Observación:** La funcionalidad de conocer si un vehículo necesita mantenimiento se cubre indirectamente través del sistema de alertas (HU-11 + HU-12). Sin embargo, no existe una consulta on-demand por placa que retorne el estado consolidado como lo describe la HU.
- **Nota:** Esta HU fue explícitamente diferida en `PRD_REDUCED.md` (S7).

---

## 3. Desviaciones del plan original

### 3.1 Cambios funcionales

#### 3.1.1 HU-12 (Consultar alertas) fue implementada pese a estar diferida

**Planeado (S7):** Diferida a siguiente iteración.

**Implementado:**
- `AlertController` en `rules-alerts-service` con 2 endpoints:
  - `GET /api/alerts` (con filtro opcional `?status=`) → `GetAlertsService`
  - `GET /api/alerts/vehicle/{plate}` → `GetAlertsByVehicleService`
- Frontend consume estos endpoints en páginas `/mileage` y `/services`

**Impacto:** Positivo. El frontend necesita consultar alertas para permitir al usuario seleccionar una alerta al registrar mantenimiento (HU-13), por lo que esta HU resultó necesaria para completar el flujo end-to-end.

---

#### 3.1.2 Consulta de vehículo por placa implementada (fuera del MVP S6)

**Planeado:** En PRD S6, sección "OUT":
> "Consultar vehículo por placa — Se difiere al Sprint 1"

**Implementado:**
- `VehicleQueryController` en `fleet-service`: `GET /api/vehicles/{plate}` → `GetVehicleByPlateService`
- `VehicleQueryPort` + `VehicleQueryAdapter` en `rules-alerts-service`: llama al fleet-service vía REST para resolver `vehicleId` por placa

**Impacto:** Necesario para que `rules-alerts-service` pueda resolver el vehículo al registrar mantenimiento por placa y al consultar alertas por placa.

---

#### 3.1.3 Registro de mantenimiento requiere `alertId` obligatorio

**Planeado (SUBTASKS HU-13):**
> DTOs de entrada: placa, tipo de servicio, costo, proveedor, observaciones.

**Implementado:** `RegisterMaintenanceRequest` requiere `@NotNull UUID alertId`. No se puede registrar un mantenimiento sin una alerta asociada.

**Impacto:** Elimina el escenario de "mantenimiento voluntario sin alerta" descrito en HU-14 Gherkin:
> *"Dado vehículo sin alerta pendiente para Rotación de llantas, cuando registra servicio..."*

---

#### 3.1.4 Unicidad de nombre de regla

**Planeado (SUBTASKS HU-07):** No se mencionaba restricción de nombre único para reglas.

**Implementado:** `CreateMaintenanceRuleService` valida `existsByName()` y lanza `DuplicateAssociationException` si ya existe.

**Impacto:** Previene duplicación de reglas por nombre, lo cual es razonable pero no estaba especificado.

---

#### 3.1.5 Validación de VIN duplicado

**Planeado (SUBTASKS HU-01):** Solo mencionaba placa única. El VIN se validaba por longitud.

**Implementado:** `RegisterVehicleService` también valida `existsByVin()` y lanza `DuplicateVinException` (409 CONFLICT).

**Impacto:** Mejora la integridad de datos. No estaba en la especificación original.

---

### 3.2 Cambios técnicos / arquitectónicos

#### 3.2.1 Comunicación HTTP inter-servicio (no planificada)

**Planeado (ARCHITECTURE.md S7):**
> "No hay comunicación HTTP directa entre servicios en el MVP"

**Implementado:** `VehicleQueryAdapter` en `rules-alerts-service` usa `RestTemplate` para llamar `GET /api/vehicles/{plate}` de `fleet-service`. Configurado vía `fleet.service.url` property.

**Motivo probable:** Necesario para que `rules-alerts-service` resuelva `vehicleId` por placa al consultar alertas (`GetAlertsByVehicleService`) y al registrar mantenimiento.

---

#### 3.2.2 Patrón Strategy para publicación de eventos

**Planeado (IMPLEMENTATION.md):** `EventPublisherPort` con implementación directa `RabbitMqEventPublisher`.

**Implementado:** `EventPublisherStrategy` (`@Primary`) con propiedad configurable `fleetguard.events.publisher`. Soporta 2 estrategias:
- `rabbitMQEventPublisher` (producción)
- `noOpEventPublisher` (tests/desarrollo)

**Impacto:** Mejora la testabilidad y flexibilidad. Patrón Strategy no documentado pero bien implementado.

---

#### 3.2.3 @TransactionalEventListener para publicación de eventos

**Planeado (IMPLEMENTATION.md):**
> "El Use Case publica eventos después del `save`"

**Implementado:** `MileageEventListener` usa `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Retryable(maxAttempts = 3)`. El Use Case publica un evento interno vía `ApplicationEventPublisher` de Spring, y el listener lo envía a RabbitMQ después del commit de la transacción.

**Impacto:** Garantiza que el evento solo se publica si la transacción fue exitosa. Mejora respecto al plan simple. El retry (3 intentos, backoff 1s → 2s) añade resiliencia.

---

#### 3.2.4 EvaluateMaintenanceAlertsService sin interface (Port In)

**Planeado (IMPLEMENTATION.md):** `GenerateAlertUseCase` como interfaz en `application/ports/in`.

**Implementado:** `EvaluateMaintenanceAlertsService` es un `@Service` sin interfaz. Invocado directamente por `MileageRegisteredConsumer`.

**Impacto:** Rompe parcialmente el patrón hexagonal (no hay puerto de entrada), pero funcionalmente correcto dado que es un servicio interno invocado solo por el consumer.

---

#### 3.2.5 Modelo de dominio enriquecido en MileageLog

**Planeado (IMPLEMENTATION.md):** `MileageLog` con: `id`, `vehicleId`, `mileageValue`, `recordedAt`, `recordedBy`, `excessiveIncrement`.

**Implementado:** Agrega campos adicionales: `vehicleTypeId`, `vehicleStatus`, `previousMileage`, `kmTraveled`. Estos datos enriquecen el evento `MileageRegistered` enviado a `rules-alerts-service`.

---

#### 3.2.6 Domain model en rules-alerts-service simplificado

**Planeado (ARCHITECTURE.md / IMPLEMENTATION.md):** Agregados para `MaintenanceRule`, `MaintenanceAlert` y `MaintenanceRecord` con DDD táctico (AggregateRoot base, Domain Events).

**Implementado:**
- **No existe** clase base `AggregateRoot` en `rules-alerts-service`
- `MaintenanceAlert` y `MaintenanceRule` usan `@Getter @AllArgsConstructor @NoArgsConstructor` de Lombok — son más POJOs que agregados DDD
- `MaintenanceRecord` sí tiene validaciones en constructor (serviceType, performedAt, mileageAtService, recordedBy)
- **No se generan Domain Events** (`AlertGenerated`, `MaintenanceRegistered`) dentro del dominio de este servicio
- Los `Integration Events` (`AlertGeneratedEvent`, `MaintenanceRegisteredEvent`) definidos en `EVENTS.md` no se publican

**Impacto:** Simplificación significativa respecto al plan. La lógica de negocio vive parcialmente en el servicio de aplicación (`EvaluateMaintenanceAlertsService`) en lugar del dominio.

---

#### 3.2.7 Frontend Next.js completo (no documentado en S7)

**Planeado (S7):** Ningún documento de S7 menciona frontend. `ARCHITECTURE.md` solo describe backend.

**Implementado:** Aplicación completa `fleetguard-frontend` en Next.js 14 con:
- 5 páginas: Home, Register, Mileage, Rules, Services
- 34 componentes React
- 7 hooks custom
- 4 servicios API
- Validadores
- API proxy routes a ambos backends
- Tests unitarios e integración (Vitest + Testing Library + MSW)
- Modo demo (fallback offline con datos mock)
- Tailwind CSS con paleta Material Design 3
- Dockerizado con standalone output

---

## 4. Funcionalidades fuera de alcance que fueron implementadas

| Funcionalidad | Origen | Estado en PRD | Evidencia |
|--------------|--------|---------------|-----------|
| Consultar alertas con filtrado por estado | HU-12 (diferida S7) | "Complementaria, la alerta se genera aunque no se visualice" | `AlertController`: `GET /api/alerts?status=`, `GetAlertsService`, `GetAlertsByVehicleService` |
| Consultar alertas por placa de vehículo | HU-12 (diferida S7) | Diferida | `GET /api/alerts/vehicle/{plate}`, `GetAlertsByVehicleService` con `VehicleQueryPort` |
| Consultar vehículo por placa | Explícitamente OUT en PRD S6 | "Se difiere al Sprint 1" | `VehicleQueryController`: `GET /api/vehicles/{plate}`, `GetVehicleByPlateService` |
| Frontend web completo | No documentado en S7 | No mencionado | `fleetguard-frontend/` — Next.js 14, 5 páginas, 34 componentes |
| Campos `performed_at` y `mileage_at_service` en registro de mantenimiento | HU-16 (diferida S7) | "Complementaria, los campos existen en HU-13" | `RegisterMaintenanceRequest`: `performedAt`, `mileageAtService` con validaciones |
| Escalamiento de estados de alerta (PENDING → WARNING → OVERDUE) | Parcial de HU-12 | Diferida | `EvaluateMaintenanceAlertsService.resolveStatus()`: actualiza alertas existentes |
| Validación de VIN único | No en SUBTASKS | No especificado | `RegisterVehicleService.existsByVin()`, `DuplicateVinException` |
| Unicidad de nombre de regla | No en SUBTASKS HU-07 | No especificado | `CreateMaintenanceRuleService.existsByName()` |
| Datos seed de tipos de vehículo | No en SUBTASKS | No especificado | `V2__insert_vehicle_types.sql` — 15 tipos precargados |
| Modo demo offline en frontend | No planificado | No mencionado | `src/lib/api.ts`: fallback a localStorage mock cuando backend no responde |

---

## 5. Brechas actuales del sistema

### 5.1 Brechas respecto al PRD S6 (11 HUs)

| Brecha | HU afectada | Severidad | Detalle |
|--------|-------------|-----------|---------|
| No existe endpoint de estado de mantenimiento | HU-06 | Media | `GET /api/vehicles/{placa}/maintenance-status` no implementado. No se puede consultar AL_DIA / PROXIMO / VENCIDO por vehículo |
| No existe endpoint de asociación mantenimiento-regla | HU-14 | Baja | `PATCH /api/maintenance/{id}/rule` no implementado. El `ruleId` se infiere del `alertId` |
| Mantenimiento sin alerta previa no soportado | HU-14 Gherkin | Media | `alertId` es `@NotNull` en request. Un mantenimiento voluntario (sin alerta) no se puede registrar |
| Reseteo de contador por `mileageAtService` | HU-14 | Baja | El cálculo de `dueAtKm` usa `currentMileage` del evento, no el `mileageAtService` registrado en el mantenimiento |
| Validación `mileageAtService` ≤ `currentMileage` | HU-16 | Media | `rules-alerts-service` no tiene acceso al kilometraje actual del vehículo |
| Escalamiento automático WARNING → OVERDUE en consulta | HU-12 (DEV#4) | Baja | El escalamiento ocurre al evaluar (evento), no al consultar alertas |

### 5.2 Brechas respecto a SUBTASKS

| Brecha | HU | Detalle |
|--------|-----|---------|
| Endpoint de mantenimiento en servicio diferente | HU-13 | SUBTASKS: `POST /api/vehicles/{placa}/maintenance`. Real: `POST /api/maintenance/{plate}` en `rules-alerts-service` |
| `recorded_at` no incluye hora visible al registrar | HU-04 | El campo se persiste como `LocalDateTime` pero no se expone la hora en el resultado del frontend |
| Falta validación de tipo de vehículo existente al asociar | HU-09 | `AssociateVehicleTypeService` valida que la regla exista pero no valida que el `vehicleTypeId` corresponda a un tipo real (diferente BD) |

### 5.3 Brechas funcionales generales

| Brecha | Impacto |
|--------|---------|
| Sin autenticación ni roles | No se distingue Administrador vs Conductor en la práctica. Cualquier usuario puede ejecutar cualquier operación |
| Sin listado de vehículos | No hay `GET /api/vehicles` para consultar la flota completa |
| Sin listado de reglas | No hay `GET /api/maintenance-rules` para ver reglas existentes |
| Sin historial de mantenimientos | No hay `GET /api/maintenance` ni endpoint similar para consultar servicios realizados |
| Sin edición ni eliminación | Ninguna entidad soporta PUT, PATCH (excepto status de alerta), ni DELETE |

---

## 6. Alineación Arquitectura vs Código

### 6.1 Arquitectura Hexagonal

| Principio | fleet-service | rules-alerts-service |
|-----------|---------------|---------------------|
| Domain sin anotaciones de framework | ✅ Domain puro (sin `@Entity`, `@Service`, etc.) | ⚠️ Usa `@Getter`, `@AllArgsConstructor`, `@NoArgsConstructor` de Lombok en domain (aceptable, no es framework de infraestructura) |
| Ports In (interfaces de entrada) | ✅ `RegisterVehicleUseCase`, `RegisterMileageUseCase`, `GetVehicleByPlateUseCase` | ⚠️ `EvaluateMaintenanceAlertsService` NO tiene interfaz Port In |
| Ports Out (interfaces de salida) | ✅ `VehicleRepositoryPort`, `MileageLogRepositoryPort`, `EventPublisherPort` | ✅ 7 ports out definidos correctamente |
| Adapters implementan Ports | ✅ `VehicleRepositoryAdapter`, `MileageLogRepositoryAdapter` | ✅ 6 adapters implementan sus respectivos ports |
| Use Case = 1 agregado por transacción | ✅ (Vehicle es modificado atómicamente) | ⚠️ `RegisterMaintenanceService` modifica `MaintenanceRecord` Y `MaintenanceAlert` en misma operación |
| Clase base AggregateRoot | ✅ Presente con `pullDomainEvents()` | ❌ No existe. Los domain models no extienden ninguna base |
| Domain Events | ✅ `MileageRegistered` (producido en `MileageLog.create()`) | ❌ `AlertGenerated` y `MaintenanceRegistered` NO se producen como domain events |
| Integration Events publicados | ✅ `MileageRegisteredEvent` vía RabbitMQ | ❌ `AlertGeneratedEvent` y `MaintenanceRegisteredEvent` NO se publican (definidos en EVENTS.md pero no implementados) |

### 6.2 Microservicios

| Aspecto planificado | Evidencia |
|---------------------|-----------|
| 2 microservicios (fleet + rules-alerts) | ✅ Confirmado en código y docker-compose |
| BD separada por servicio | ✅ `fleet-db` (PostgreSQL, puerto 5433) + `rules-alerts-db` (PostgreSQL, puerto 5434) |
| RabbitMQ como broker | ✅ Exchange `fleetguard.exchange`, Queue `mileage.registered.queue`, Routing Key `mileage.registered` |
| Sin comunicación HTTP directa | ❌ `VehicleQueryAdapter` usa `RestTemplate` para llamar a fleet-service |
| Publicación best-effort | ✅ `MileageEventListener`: si falla publish, el kilometraje queda registrado. `@Retryable` con 3 intentos |

### 6.3 Estructura de paquetes

| Paquete planificado (IMPLEMENTATION.md) | fleet-service | rules-alerts-service |
|----------------------------------------|---------------|---------------------|
| `domain/model/` | ✅ `vehicle/`, `mileage/` | ✅ `rule/`, `alert/`, `maintenance/`, `association/` |
| `domain/valueobject/` | ✅ `Plate`, `Vin`, `Mileage` | ❌ No hay VOs (no tiene `ServiceType` VO planificado) |
| `domain/exception/` | ✅ 8 excepciones | ✅ 5 excepciones |
| `domain/event/` | ✅ `DomainEvent`, `MileageRegistered` | ❌ No existe paquete de eventos |
| `application/ports/in/` | ✅ 3 interfaces | ✅ 5 interfaces (más de lo planificado) |
| `application/ports/out/` | ✅ 3 interfaces | ✅ 7 interfaces (separación queries y commands) |
| `application/usecase/` | ✅ Nombrados como `*Service` no `*UseCaseImpl` | ✅ Nombrados como `*Service` |
| `application/service/` | — (fusionado con usecase) | ✅ `EvaluateMaintenanceAlertsService` (sin interfaz) |
| `application/mapper/` (EventMapper) | ❌ No existe clase EventMapper separada. Mapping está en `MileageEventListener` | ❌ No existe |
| `infrastructure/web/controller/` | ✅ 3 controllers | ✅ 3 controllers |
| `infrastructure/web/dto/` | ✅ request + response | ✅ request + response |
| `infrastructure/web/mapper/` | ✅ `VehicleWebMapper`, `MileageWebMapper` | ✅ 3 web mappers |
| `infrastructure/persistence/entity/` | ✅ 3 JPA entities | ✅ 4 JPA entities |
| `infrastructure/persistence/repository/` | ✅ 3 JPA repos | ✅ 4 JPA repos |
| `infrastructure/persistence/adapter/` | ✅ 2 adapters | ✅ 6 adapters (incluye separación query/command) |
| `infrastructure/persistence/mapper/` | ✅ 2 persistence mappers | ✅ 4 persistence mappers |
| `infrastructure/messaging/publisher/` | ✅ + `EventPublisherStrategy`, `NoOpEventPublisher` | ❌ No hay publishers (no publica eventos) |
| `infrastructure/messaging/consumer/` | — (no consume) | ✅ `MileageRegisteredConsumer` |
| `infrastructure/config/` | ✅ `RabbitMQConfig`, `FlywayConfig`, `RetryConfig`, `WebConfig` | ✅ `RabbitMQConfig`, `FlywayConfig`, `RestTemplateConfig`, `WebConfig` |
| `infrastructure/web/exception/` | ✅ `GlobalExceptionHandler` | ✅ `GlobalExceptionHandler` |

### 6.4 Convenciones de nombres

| Convención planificada | Cumplimiento |
|-----------------------|-------------|
| UseCase: `<Verb><Entity>UseCase` | ⚠️ Interfaces sí usan el patrón. Implementaciones usan `*Service` en lugar de `*UseCaseImpl` |
| Ports In: `<Entity>UseCase` | ✅ `RegisterVehicleUseCase`, `RegisterMileageUseCase`, etc. |
| Ports Out: `<Entity><Action>Port` | ✅ `VehicleRepositoryPort`, `MaintenanceAlertRepositoryPort`, etc. |
| Domain Event: `<Entity><Past>` | ✅ `MileageRegistered` |
| Integration Event: `<Entity><Past>Event` | ✅ `MileageRegisteredEvent` (en fleet-service) |

### 6.5 Topología de despliegue

| Componente | Puerto planificado | Puerto real (docker-compose) |
|-----------|-------------------|------------------------------|
| fleet-service | No especificado | 8082 (externo) → 8080 (interno) |
| rules-alerts-service | No especificado | 8083 (externo) → 8080 (interno) |
| fleet-db | No especificado | 5433 → 5432 |
| rules-alerts-db | No especificado | 5434 → 5432 |
| RabbitMQ | No especificado | 5672 (AMQP), 15672 (management) |
| Frontend | No planificado | 3000 |

**Nota:** En desarrollo local (sin Docker), fleet-service usa 8092 y rules-alerts-service usa 8093.

---

## 7. Posibles inferencias (baja confianza)

> Las siguientes observaciones son inferencias basadas en patrones observados. **No están confirmadas** por documentación explícita ni por evidencia directa. Se incluyen solo como referencia para futuras revisiones.

1. **HU-12 se implementó por necesidad del frontend, no por cambio de plan:** El frontend necesita listar alertas para que el usuario pueda seleccionar una al registrar mantenimiento (HU-13). Sin HU-12, el flujo de UI sería incompleto. Esto sugiere que la implementación de HU-12 fue una decisión reactiva, no planificada desde el inicio.

2. **La comunicación HTTP inter-servicio fue una concesión pragmática:** El plan original decía "sin comunicación HTTP directa". Sin embargo, para resolver `vehicleId` por placa en `rules-alerts-service`, se necesitó llamar a `fleet-service`. La alternativa (incluir vehicleId en el evento) hubiera requerido que el frontend conociera UUIDs internos, lo cual degradaba la UX.

3. **La simplificación del domain model en rules-alerts-service puede indicar presión de tiempo:** La ausencia de `AggregateRoot`, domain events, y VOs en `rules-alerts-service` contrasta con la implementación rigurosa en `fleet-service`. Esto podría indicar que el segundo servicio se desarrolló con restricciones de tiempo más estrictas.

4. **El modo demo del frontend sugiere desarrollo en paralelo:** La existencia de un mecanismo de fallback con datos mock en `api.ts` sugiere que el frontend se desarrolló en paralelo (o antes) de que los backends estuvieran completamente disponibles.

5. **La fórmula de cálculo de `dueAtKm` basada en ciclos (`((currentMileage / intervalKm) + 1) * intervalKm`) es una simplificación respecto al plan:** El PRD describe un reseteo basado en el km del servicio realizado. La implementación usa división entera para calcular el próximo ciclo, lo cual funciona correctamente para mantenimientos "on time" pero puede producir desviaciones si el mantenimiento se realiza con retraso significativo.

---

## Anexo: Inventario técnico resumido

### Backend — fleet-service

| Capa | Clases |
|------|--------|
| Domain | 2 agregados (`Vehicle`, `MileageLog`), 1 entidad (`VehicleType`), 1 enum (`VehicleStatus`), 3 VOs (`Plate`, `Vin`, `Mileage`), 8 excepciones, 1 evento (`MileageRegistered`), 1 base (`AggregateRoot`) |
| Application | 3 ports in, 3 ports out, 3 services |
| Infrastructure | 3 controllers, 3 JPA entities, 3 JPA repos, 2 adapters, 2 persistence mappers, 2 web mappers, 3 publishers, 1 listener, 4 configs, 1 exception handler |
| Tests | 22 clases de test |
| BD | 3 tablas + 15 registros seed |

### Backend — rules-alerts-service

| Capa | Clases |
|------|--------|
| Domain | 4 models (`MaintenanceRule`, `MaintenanceAlert`, `MaintenanceRecord`, `RuleVehicleTypeAssoc`), 1 constantes, 5 excepciones |
| Application | 5 ports in, 7 ports out, 6 services |
| Infrastructure | 3 controllers, 4 JPA entities, 4 JPA repos, 6 adapters, 4 persistence mappers, 3 web mappers, 1 consumer, 1 client REST, 4 configs, 1 exception handler |
| Tests | 19 clases de test |
| BD | 4 tablas |

### Frontend — fleetguard-frontend

| Categoría | Total |
|-----------|-------|
| Páginas | 5 (Home, Register, Mileage, Rules, Services) |
| Componentes | 34 (UI, Layout, Forms, Alerts, etc.) |
| Hooks | 7 custom hooks |
| Servicios API | 4 (vehicle, alert, maintenance, rule) |
| Validadores | 3 (vehicle, mileage, maintenance) |
| Tests unitarios | ~30 archivos |
| Tests integración | ~8 archivos |
| Mock data | 6 archivos |

### Endpoints implementados

| Servicio | Método | Ruta | HU |
|----------|--------|------|-----|
| fleet-service | POST | `/api/vehicles` | HU-01 |
| fleet-service | POST | `/api/vehicles/{plate}/mileage` | HU-04/05 |
| fleet-service | GET | `/api/vehicles/{plate}` | Fuera de alcance MVP |
| rules-alerts-service | POST | `/api/maintenance-rules` | HU-07 |
| rules-alerts-service | POST | `/api/maintenance-rules/{id}/vehicle-types` | HU-09 |
| rules-alerts-service | GET | `/api/alerts` | HU-12 (diferida) |
| rules-alerts-service | GET | `/api/alerts/vehicle/{plate}` | HU-12 (diferida) |
| rules-alerts-service | POST | `/api/maintenance/{plate}` | HU-13 |
| rules-alerts-service | — | `@RabbitListener` (MileageRegisteredEvent) | HU-11 |
