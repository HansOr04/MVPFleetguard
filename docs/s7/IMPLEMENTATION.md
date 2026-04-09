# 📄 `implementation.md – FleetGuard MVP`

## 1. Propósito del Documento

Este documento define **cómo debe implementarse el código** del sistema FleetGuard.

Su objetivo es:

* Garantizar consistencia en la implementación
* Evitar ambigüedad al usar IA (Copilot)
* Asegurar cumplimiento de arquitectura hexagonal
* Evitar malas prácticas
* Reflejar las decisiones técnicas de las subtasks y HUs

---

## 2. Reglas Generales

* Nunca mezclar capas
* Toda lógica de negocio vive en **Domain**
* Application **orquesta, no decide**
* Infrastructure **implementa, no contiene reglas**
* Todo acceso externo se hace mediante **Ports**
* No se accede directamente a repositorios desde controllers
* Un Use Case debe modificar un único agregado raíz por transacción lógica (cuando sea posible)
* Los eventos se publican **después** de persistir el agregado (después del `save`)
* La publicación de eventos es best-effort y no debe afectar el flujo principal

---

## 3. Estructura de Microservicios

El sistema se compone de 2 microservicios, cada uno con arquitectura hexagonal interna:

### 3.1 fleet-service

**Responsabilidad:** Gestión de vehículos, tipos de vehículo y registro de kilometraje.

**HUs:** HU-01, HU-04, HU-05

**Estructura de paquetes:**

```
com.fleetguard.fleet
├── domain
│   ├── model
│   │   ├── vehicle
│   │   │   ├── Vehicle.java              ← Agregado raíz
│   │   │   ├── VehicleType.java           ← Entidad interna
│   │   │   └── VehicleStatus.java         ← Enum (ACTIVE, INACTIVE)
│   │   └── mileage
│   │       └── MileageLog.java            ← Agregado raíz
│   ├── valueobject
│   │   ├── Plate.java
│   │   ├── Vin.java
│   │   └── Mileage.java
│   ├── exception
│   │   ├── DuplicatePlateException.java
│   │   ├── InvalidVinException.java
│   │   ├── InvalidMileageException.java
│   │   ├── InactiveVehicleException.java
│   │   ├── VehicleNotFoundException.java
│   │   └── MissingRecordedByException.java
│   └── event
│       ├── DomainEvent.java               ← Interface base
│       └── MileageRegistered.java         ← Domain Event
├── application
│   ├── ports
│   │   ├── in
│   │   │   ├── RegisterVehicleUseCase.java
│   │   │   └── RegisterMileageUseCase.java
│   │   └── out
│   │       ├── VehicleRepositoryPort.java
│   │       ├── MileageLogRepositoryPort.java
│   │       └── EventPublisherPort.java
│   ├── usecase
│   │   ├── RegisterVehicleUseCaseImpl.java
│   │   └── RegisterMileageUseCaseImpl.java
│   ├── service
│   │   └── MileageValidatorService.java   ← Validaciones cruzadas (HU-05)
│   └── mapper
│       └── EventMapper.java               ← Domain Event → Integration Event
└── infrastructure
    ├── config
    │   └── SpringBeanConfig.java
    ├── web
    │   ├── controller
    │   │   ├── VehicleController.java
    │   │   └── MileageController.java
    │   ├── dto
    │   │   ├── request
    │   │   │   ├── RegisterVehicleRequest.java
    │   │   │   └── RegisterMileageRequest.java
    │   │   └── response
    │   │       ├── VehicleResponse.java
    │   │       └── MileageResponse.java
    │   └── mapper
    │       └── WebMapper.java
    ├── persistence
    │   ├── entity
    │   │   ├── VehicleJpaEntity.java
    │   │   ├── VehicleTypeJpaEntity.java
    │   │   └── MileageLogJpaEntity.java
    │   ├── repository
    │   │   ├── VehicleJpaRepository.java
    │   │   └── MileageLogJpaRepository.java
    │   ├── adapter
    │   │   ├── VehicleRepositoryAdapter.java
    │   │   └── MileageLogRepositoryAdapter.java
    │   └── mapper
    │       └── JpaMapper.java
    └── messaging
        ├── event
        │   └── MileageRegisteredEvent.java  ← Integration Event
        └── publisher
            └── RabbitMqEventPublisher.java
```

---

### 3.2 rules-alerts-service

**Responsabilidad:** Gestión de reglas, generación de alertas y registro de mantenimientos.

**HUs:** HU-07, HU-09, HU-11, HU-13

**Estructura de paquetes:**

```
com.fleetguard.rulesalerts
├── domain
│   ├── model
│   │   ├── rule
│   │   │   └── MaintenanceRule.java       ← Agregado raíz
│   │   ├── alert
│   │   │   ├── MaintenanceAlert.java      ← Agregado raíz
│   │   │   └── AlertStatus.java           ← Enum (PENDING, WARNING, OVERDUE, RESOLVED)
│   │   └── maintenance
│   │       └── MaintenanceRecord.java     ← Agregado raíz
│   ├── valueobject
│   │   └── ServiceType.java
│   ├── exception
│   │   ├── RuleNotFoundException.java
│   │   ├── DuplicateAssociationException.java
│   │   ├── DuplicateAlertException.java
│   │   └── InvalidMaintenanceException.java
│   └── event
│       ├── DomainEvent.java
│       ├── AlertGenerated.java            ← Domain Event
│       └── MaintenanceRegistered.java     ← Domain Event
├── application
│   ├── ports
│   │   ├── in
│   │   │   ├── CreateMaintenanceRuleUseCase.java
│   │   │   ├── AssociateRuleToVehicleTypeUseCase.java
│   │   │   ├── GenerateAlertUseCase.java
│   │   │   └── RegisterMaintenanceUseCase.java
│   │   └── out
│   │       ├── MaintenanceRuleRepositoryPort.java
│   │       ├── RuleVehicleTypeAssociationRepositoryPort.java
│   │       ├── MaintenanceAlertRepositoryPort.java
│   │       ├── MaintenanceRecordRepositoryPort.java
│   │       └── EventPublisherPort.java
│   ├── usecase
│   │   ├── CreateMaintenanceRuleUseCaseImpl.java
│   │   ├── AssociateRuleToVehicleTypeUseCaseImpl.java
│   │   ├── GenerateAlertUseCaseImpl.java
│   │   └── RegisterMaintenanceUseCaseImpl.java
│   └── mapper
│       └── EventMapper.java
└── infrastructure
    ├── config
    │   └── SpringBeanConfig.java
    ├── web
    │   ├── controller
    │   │   ├── MaintenanceRuleController.java
    │   │   └── MaintenanceController.java
    │   ├── dto
    │   │   ├── request
    │   │   │   ├── CreateRuleRequest.java
    │   │   │   ├── AssociateRuleRequest.java
    │   │   │   └── RegisterMaintenanceRequest.java
    │   │   └── response
    │   │       ├── RuleResponse.java
    │   │       ├── AssociationResponse.java
    │   │       └── MaintenanceResponse.java
    │   └── mapper
    │       └── WebMapper.java
    ├── persistence
    │   ├── entity
    │   │   ├── MaintenanceRuleJpaEntity.java
    │   │   ├── RuleVehicleTypeAssocJpaEntity.java
    │   │   ├── MaintenanceAlertJpaEntity.java
    │   │   └── MaintenanceRecordJpaEntity.java
    │   ├── repository
    │   │   ├── MaintenanceRuleJpaRepository.java
    │   │   ├── RuleVehicleTypeAssocJpaRepository.java
    │   │   ├── MaintenanceAlertJpaRepository.java
    │   │   └── MaintenanceRecordJpaRepository.java
    │   ├── adapter
    │   │   ├── MaintenanceRuleRepositoryAdapter.java
    │   │   ├── RuleVehicleTypeAssocRepositoryAdapter.java
    │   │   ├── MaintenanceAlertRepositoryAdapter.java
    │   │   └── MaintenanceRecordRepositoryAdapter.java
    │   └── mapper
    │       └── JpaMapper.java
    └── messaging
        ├── event
        │   ├── AlertGeneratedEvent.java       ← Integration Event
        │   └── MaintenanceRegisteredEvent.java ← Integration Event
        ├── consumer
        │   └── MileageRegisteredConsumer.java  ← Consume de fleet-service
        └── publisher
            └── RabbitMqEventPublisher.java
```

---

## 4. Implementación por Capas

---

### 4.1 Domain

#### Qué contiene

* Agregados (extienden `AggregateRoot`)
* Entidades
* Value Objects
* Excepciones de negocio
* Domain Events

---

#### Reglas obligatorias

* No usar:
  * `@Entity`
  * `@Service`
  * `@Component`
* No depender de frameworks
* Toda validación de negocio debe estar aquí

---

#### Clase base AggregateRoot

```java
public abstract class AggregateRoot {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }
}
```

---

#### Ejemplo: Vehicle (fleet-service — HU-01)`

```java
public class Vehicle extends AggregateRoot {

    private UUID id;
    private Plate plate;
    private String brand;
    private String model;
    private int year;
    private String fuelType;
    private Vin vin;
    private VehicleStatus status;
    private Mileage currentMileage;
    private VehicleType vehicleType;

    // Constructor de creación (HU-01)
    public static Vehicle create(Plate plate, String brand, String model,
            int year, String fuelType, Vin vin, VehicleType vehicleType) {
        // Reglas HU-01: placa única (validada en Port), VIN 17 chars (Vin VO),
        // tipo obligatorio (parámetro requerido), estado ACTIVE, km inicial 0
        Vehicle vehicle = new Vehicle();
        vehicle.id = UUID.randomUUID();
        vehicle.plate = plate;
        vehicle.brand = brand;
        vehicle.model = model;
        vehicle.year = year;
        vehicle.fuelType = fuelType;
        vehicle.vin = vin;
        vehicle.status = VehicleStatus.ACTIVE;
        vehicle.currentMileage = Mileage.zero();
        vehicle.vehicleType = vehicleType;
        return vehicle;
    }

    // Actualizar kilometraje (HU-04 + HU-05)
    public void updateMileage(Mileage newMileage) {
        if (this.status != VehicleStatus.ACTIVE) {
            throw new InactiveVehicleException(this.id);
        }
        this.currentMileage.validateNotLessThan(newMileage);
        this.currentMileage = newMileage;
    }

    // Getters
    public UUID getId() { return id; }
    public VehicleStatus getStatus() { return status; }
    public Mileage getCurrentMileage() { return currentMileage; }
    public VehicleType getVehicleType() { return vehicleType; }
}
```

---

#### Ejemplo: MileageLog (fleet-service — HU-04)

```java
public class MileageLog extends AggregateRoot {

    private UUID id;
    private UUID vehicleId;
    private Mileage mileageValue;
    private LocalDateTime recordedAt;
    private String recordedBy;          // Conductor obligatorio (HU-04 subtask DEV#5)
    private boolean excessiveIncrement; // Advertencia HU-05

    public static MileageLog create(UUID vehicleId, Mileage mileageValue,
            String recordedBy, boolean excessiveIncrement) {
        // HU-04: recorded_by es obligatorio
        if (recordedBy == null || recordedBy.isBlank()) {
            throw new MissingRecordedByException();
        }
        MileageLog log = new MileageLog();
        log.id = UUID.randomUUID();
        log.vehicleId = vehicleId;
        log.mileageValue = mileageValue;
        log.recordedAt = LocalDateTime.now();
        log.recordedBy = recordedBy;
        log.excessiveIncrement = excessiveIncrement;
        log.addDomainEvent(new MileageRegistered(vehicleId, mileageValue.getValue()));
        return log;
    }
}
```

---

#### Ejemplo: Value Object Mileage (HU-04 + HU-05)

```java
public class Mileage {

    private final long value;

    public Mileage(long value) {
        // HU-05 subtask DEV#1: debe ser mayor a cero
        // Excepción: Mileage.zero() para inicialización (HU-01 km inicial 0)
        if (value < 0) {
            throw new InvalidMileageException("Mileage cannot be negative");
        }
        this.value = value;
    }

    public static Mileage zero() {
        return new Mileage(0);
    }

    /**
     * HU-05: Validar que el nuevo km no sea menor al actual.
     * - Si es menor → rechaza (HU-05 Gherkin: "rechaza indicando que no puede ser menor")
     * - Si es igual → acepta sin cambio (HU-05 Gherkin: "acepta y el acumulado permanece")
     * - Si es mayor → acepta
     *
     * HU-05 subtask DEV#2: Si no tiene km previo (current=0), acepta cualquier valor > 0
     */
    public void validateNotLessThan(Mileage newMileage) {
        if (newMileage.value < this.value) {
            throw new InvalidMileageException(
                "New mileage " + newMileage.value +
                " cannot be less than current " + this.value);
        }
    }

    /**
     * HU-05 subtask DEV#4: Advertir si incremento > 2000 km (sin bloquear)
     */
    public boolean isExcessiveIncrement(Mileage previous) {
        return (this.value - previous.value) > 2000;
    }

    public long getValue() { return value; }

    // equals, hashCode basados en value
}
```

---

#### Ejemplo: Value Object Vin (HU-01)

```java
public class Vin {

    private final String value;

    public Vin(String value) {
        // HU-01: VIN debe tener exactamente 17 caracteres
        if (value == null || value.length() != 17) {
            throw new InvalidVinException(
                "VIN must be exactly 17 characters, got: " +
                (value == null ? "null" : value.length()));
        }
        this.value = value;
    }

    public String getValue() { return value; }
}
```

---

#### Ejemplo: MaintenanceRule (rules-alerts-service — HU-07)

```java
public class MaintenanceRule extends AggregateRoot {

    private UUID id;
    private String name;
    private String maintenanceType;
    private long intervalKm;
    private long warningThresholdKm;
    private String status;

    public static MaintenanceRule create(String name, String maintenanceType,
            long intervalKm, Long warningThresholdKm) {

        // HU-07 subtask DEV#3: nombre obligatorio
        if (name == null || name.isBlank()) {
            throw new InvalidRuleException("Rule name is required");
        }

        // HU-07 subtask DEV#3: interval_km > 0
        if (intervalKm <= 0) {
            throw new InvalidRuleException("Interval km must be greater than zero");
        }

        MaintenanceRule rule = new MaintenanceRule();
        rule.id = UUID.randomUUID();
        rule.name = name;
        rule.maintenanceType = maintenanceType;
        rule.intervalKm = intervalKm;

        // HU-07 subtask DEV#4: warning_threshold_km configurable, con default
        rule.warningThresholdKm = (warningThresholdKm != null) ? warningThresholdKm : 500;

        // HU-07 subtask DEV#5: estado ACTIVE al crear
        rule.status = "ACTIVE";

        return rule;
    }

    public UUID getId() { return id; }
    public long getIntervalKm() { return intervalKm; }
    public long getWarningThresholdKm() { return warningThresholdKm; }
}
```

---

#### Ejemplo: MaintenanceAlert (rules-alerts-service — HU-11)

```java
public class MaintenanceAlert extends AggregateRoot {

    private UUID id;
    private UUID vehicleId;
    private UUID ruleId;
    private AlertStatus status;
    private LocalDateTime triggeredAt;
    private long dueAtKm;

    public static MaintenanceAlert create(UUID vehicleId, UUID ruleId, long dueAtKm) {
        MaintenanceAlert alert = new MaintenanceAlert();
        alert.id = UUID.randomUUID();
        alert.vehicleId = vehicleId;
        alert.ruleId = ruleId;
        alert.status = AlertStatus.PENDING;
        alert.triggeredAt = LocalDateTime.now();
        alert.dueAtKm = dueAtKm;
        alert.addDomainEvent(new AlertGenerated(alert.id, vehicleId, ruleId));
        return alert;
    }

    // HU-13: resolver alerta al registrar mantenimiento
    public void resolve() {
        this.status = AlertStatus.RESOLVED;
    }

    public UUID getId() { return id; }
    public UUID getVehicleId() { return vehicleId; }
    public UUID getRuleId() { return ruleId; }
    public AlertStatus getStatus() { return status; }
}
```

---

#### Ejemplo: MaintenanceRecord (rules-alerts-service — HU-13)

```java
public class MaintenanceRecord extends AggregateRoot {

    private UUID id;
    private UUID vehicleId;
    private UUID alertId;           // Opcional (FK a maintenance_alert)
    private UUID ruleId;            // Opcional (FK a maintenance_rule)
    private String description;     // Tipo de servicio (HU-13: obligatorio)
    private BigDecimal cost;
    private String provider;
    private LocalDateTime performedAt;
    private long mileageAtService;

    public static MaintenanceRecord create(UUID vehicleId, String description,
            BigDecimal cost, String provider,
            LocalDateTime performedAt, long mileageAtService) {

        // HU-13 subtask DEV#6: tipo de servicio obligatorio
        if (description == null || description.isBlank()) {
            throw new InvalidMaintenanceException("Service type is required");
        }

        // HU-13 regla de negocio: fecha no puede ser futura
        if (performedAt != null && performedAt.isAfter(LocalDateTime.now())) {
            throw new InvalidMaintenanceException("Service date cannot be in the future");
        }

        // Kilometraje del servicio debe ser válido
        if (mileageAtService <= 0) {
            throw new InvalidMaintenanceException("Mileage at service must be positive");
        }

        MaintenanceRecord record = new MaintenanceRecord();
        record.id = UUID.randomUUID();
        record.vehicleId = vehicleId;
        record.description = description;
        record.cost = cost;
        record.provider = provider;
        record.performedAt = performedAt != null ? performedAt : LocalDateTime.now();
        record.mileageAtService = mileageAtService;

        record.addDomainEvent(new MaintenanceRegistered(
            record.id, vehicleId, mileageAtService));

        return record;
    }

    // Asociar alerta y regla (para resolución — HU-13 subtask DEV#7)
    public void associateAlert(UUID alertId) {
        this.alertId = alertId;
    }

    public void associateRule(UUID ruleId) {
        this.ruleId = ruleId;
    }
}
```

---

### 4.2 Application

---

#### Responsabilidad

Orquestar casos de uso.

---

#### Qué contiene

* Use Cases
* Ports In
* Ports Out
* Servicios de aplicación
* Mappers de eventos (Domain → Integration)

---

#### Ejemplo: RegisterVehicleUseCaseImpl (fleet-service — HU-01)

```java
public class RegisterVehicleUseCaseImpl implements RegisterVehicleUseCase {

    private final VehicleRepositoryPort vehicleRepository;

    @Override
    public RegisterVehicleResponse execute(RegisterVehicleCommand command) {

        // HU-01 subtask DEV#5: placa única
        if (vehicleRepository.existsByPlate(command.getPlate())) {
            throw new DuplicatePlateException(command.getPlate());
        }

        // HU-01: VIN 17 chars (validado en Vin VO), tipo obligatorio
        Plate plate = new Plate(command.getPlate());
        Vin vin = new Vin(command.getVin());
        VehicleType type = vehicleRepository.findVehicleTypeById(command.getVehicleTypeId())
            .orElseThrow(() -> new VehicleTypeNotFoundException(command.getVehicleTypeId()));

        // HU-01: crear vehículo con estado ACTIVE y km 0
        Vehicle vehicle = Vehicle.create(plate, command.getBrand(), command.getModel(),
            command.getYear(), command.getFuelType(), vin, type);

        Vehicle saved = vehicleRepository.save(vehicle);

        return new RegisterVehicleResponse(saved.getId(), saved.getStatus().name());
    }
}
```

---

#### Ejemplo: RegisterMileageUseCaseImpl (fleet-service — HU-04 + HU-05)

```java
public class RegisterMileageUseCaseImpl implements RegisterMileageUseCase {

    private final VehicleRepositoryPort vehicleRepository;
    private final MileageLogRepositoryPort mileageLogRepository;
    private final EventPublisherPort eventPublisher;
    private final EventMapper eventMapper;
    private final MileageValidatorService mileageValidator;

    @Override
    public RegisterMileageResponse execute(RegisterMileageCommand command) {

        // 1. Cargar vehículo (HU-04 subtask DEV#7: error si no existe)
        Vehicle vehicle = vehicleRepository.findByPlate(command.getPlate())
            .orElseThrow(() -> new VehicleNotFoundException(command.getPlate()));

        // 2. Crear value object (HU-05 subtask DEV#1: valida > 0)
        Mileage newMileage = new Mileage(command.getMileageValue());

        // 3. Verificar incremento excesivo (HU-05 subtask DEV#4: advertencia sin bloquear)
        boolean excessiveIncrement = mileageValidator
            .checkExcessiveIncrement(vehicle.getCurrentMileage(), newMileage);

        // 4. Actualizar kilometraje del vehículo (HU-04 subtask DEV#4 + HU-05 validación)
        vehicle.updateMileage(newMileage);

        // 5. Crear registro de kilometraje (HU-04 subtask DEV#5: recorded_by obligatorio)
        MileageLog log = MileageLog.create(
            vehicle.getId(), newMileage,
            command.getRecordedBy(), excessiveIncrement);

        // 6. Persistir (HU-04 subtask DEV#1-2)
        vehicleRepository.save(vehicle);
        mileageLogRepository.save(log);

        // 7. Publicar eventos (best-effort, después del save)
        List<DomainEvent> events = log.pullDomainEvents();
        events.forEach(domainEvent -> {
            IntegrationEvent integrationEvent = eventMapper.toIntegrationEvent(
                domainEvent, vehicle);  // Incluye vehicleStatus y vehicleTypeId
            eventPublisher.publish(integrationEvent);
        });

        // 8. Retornar respuesta (HU-05 subtask DEV#4: incluir advertencia si aplica)
        return new RegisterMileageResponse(
            log.getId(), vehicle.getCurrentMileage().getValue(),
            excessiveIncrement);
    }
}
```

---

#### Ejemplo: CreateMaintenanceRuleUseCaseImpl (rules-alerts-service — HU-07)

```java
public class CreateMaintenanceRuleUseCaseImpl implements CreateMaintenanceRuleUseCase {

    private final MaintenanceRuleRepositoryPort ruleRepository;

    @Override
    public CreateRuleResponse execute(CreateRuleCommand command) {

        // HU-07: validaciones de negocio delegadas al dominio
        MaintenanceRule rule = MaintenanceRule.create(
            command.getName(),
            command.getMaintenanceType(),
            command.getIntervalKm(),
            command.getWarningThresholdKm());

        MaintenanceRule saved = ruleRepository.save(rule);

        return new CreateRuleResponse(saved.getId(), saved.getStatus());
    }
}
```

---

#### Ejemplo: AssociateRuleToVehicleTypeUseCaseImpl (rules-alerts-service — HU-09)

```java
public class AssociateRuleToVehicleTypeUseCaseImpl implements AssociateRuleToVehicleTypeUseCase {

    private final MaintenanceRuleRepositoryPort ruleRepository;
    private final RuleVehicleTypeAssociationRepositoryPort assocRepository;

    @Override
    public AssociationResponse execute(AssociateRuleCommand command) {

        // HU-09 subtask DEV#7: error si regla no existe
        MaintenanceRule rule = ruleRepository.findById(command.getRuleId())
            .orElseThrow(() -> new RuleNotFoundException(command.getRuleId()));

        // HU-09 subtask DEV#2: restricción única rule_id + vehicle_type_id
        if (assocRepository.existsByRuleIdAndVehicleTypeId(
                command.getRuleId(), command.getVehicleTypeId())) {
            throw new DuplicateAssociationException(
                command.getRuleId(), command.getVehicleTypeId());
        }

        // Crear y persistir asociación
        RuleVehicleTypeAssociation assoc = RuleVehicleTypeAssociation.create(
            command.getRuleId(), command.getVehicleTypeId());

        assocRepository.save(assoc);

        return new AssociationResponse(assoc.getId());
    }
}
```

---

#### Ejemplo: GenerateAlertUseCaseImpl (rules-alerts-service — HU-11)

```java
public class GenerateAlertUseCaseImpl implements GenerateAlertUseCase {

    private final RuleVehicleTypeAssociationRepositoryPort ruleAssocRepository;
    private final MaintenanceAlertRepositoryPort alertRepository;
    private final EventPublisherPort eventPublisher;
    private final EventMapper eventMapper;

    @Override
    public void execute(GenerateAlertCommand command) {

        // HU-11 Gherkin: no evaluar vehículos inactivos
        if (!"ACTIVE".equals(command.getVehicleStatus())) {
            return;
        }

        // 1. Buscar reglas asociadas al tipo de vehículo (HU-09)
        List<MaintenanceRule> rules = ruleAssocRepository
            .findRulesByVehicleTypeId(command.getVehicleTypeId());

        // 2. Evaluar cada regla contra el km actual (HU-11 subtask DEV#4)
        for (MaintenanceRule rule : rules) {

            long currentMileage = command.getMileage();
            long dueAtKm = rule.getIntervalKm();
            long warningThreshold = rule.getWarningThresholdKm();

            // 3. Verificar si está dentro del umbral de advertencia
            if (currentMileage >= (dueAtKm - warningThreshold)) {

                // 4. Verificar no duplicar alertas PENDING (HU-11 subtask DEV#5)
                boolean alertExists = alertRepository
                    .existsPendingAlert(command.getVehicleId(), rule.getId());

                if (!alertExists) {
                    // 5. Crear alerta con estado PENDING
                    MaintenanceAlert alert = MaintenanceAlert.create(
                        command.getVehicleId(), rule.getId(), dueAtKm);

                    // 6. Persistir
                    alertRepository.save(alert);

                    // 7. Publicar evento (best-effort)
                    List<DomainEvent> events = alert.pullDomainEvents();
                    events.forEach(domainEvent -> {
                        IntegrationEvent ie = eventMapper.toIntegrationEvent(domainEvent);
                        eventPublisher.publish(ie);
                    });
                }
            }
        }
    }
}
```

---

#### Ejemplo: RegisterMaintenanceUseCaseImpl (rules-alerts-service — HU-13)

```java
public class RegisterMaintenanceUseCaseImpl implements RegisterMaintenanceUseCase {

    private final MaintenanceRecordRepositoryPort recordRepository;
    private final MaintenanceAlertRepositoryPort alertRepository;
    private final EventPublisherPort eventPublisher;
    private final EventMapper eventMapper;

    @Override
    public RegisterMaintenanceResponse execute(RegisterMaintenanceCommand command) {

        // 1. Crear MaintenanceRecord (HU-13: valida tipo obligatorio, fecha no futura, km válido)
        MaintenanceRecord record = MaintenanceRecord.create(
            command.getVehicleId(),
            command.getDescription(),
            command.getCost(),
            command.getProvider(),
            command.getPerformedAt(),
            command.getMileageAtService());

        // 2. Persistir registro
        recordRepository.save(record);

        // 3. Resolver alertas activas (HU-13 subtask DEV#7: PENDING/WARNING → RESOLVED)
        List<MaintenanceAlert> activeAlerts = alertRepository
            .findActiveAlertsByVehicleId(command.getVehicleId());

        for (MaintenanceAlert alert : activeAlerts) {
            alert.resolve();
            alertRepository.save(alert);
            record.associateAlert(alert.getId());
            record.associateRule(alert.getRuleId());
        }

        // 4. Publicar eventos (best-effort, después del save)
        List<DomainEvent> events = record.pullDomainEvents();
        events.forEach(domainEvent -> {
            IntegrationEvent ie = eventMapper.toIntegrationEvent(domainEvent);
            eventPublisher.publish(ie);
        });

        return new RegisterMaintenanceResponse(record.getId());
    }
}
```

---

#### Reglas de Application

* No contiene lógica de negocio compleja
* Siempre usa **Ports**
* Nunca usa JPA directamente
* Nunca contiene lógica de validación de reglas (eso es Domain)
* Los Domain Events deben publicarse **después del `save`** (evita inconsistencias)
* Un Use Case debe modificar un único agregado raíz por transacción lógica (cuando sea posible)
* La consistencia del negocio no depende de la publicación de eventos

---

#### Servicios de Application

Se usan cuando la lógica no pertenece a un agregado.

```java
public class MileageValidatorService {

    /**
     * HU-05 subtask DEV#4: Verifica si el incremento es excesivo (> 2000 km)
     * Retorna true si es excesivo (advertencia, no bloquea)
     */
    public boolean checkExcessiveIncrement(Mileage current, Mileage newMileage) {
        return newMileage.isExcessiveIncrement(current);
    }
}
```

---

### 4.3 Infrastructure

---

#### Responsabilidad

Implementar detalles técnicos.

---

### 4.3.1 Controllers

#### Reglas

* No contienen lógica de negocio
* Solo:
  * Reciben request
  * Transforman DTO → Command
  * Invocan Use Case
  * Devuelven respuesta

---

#### Ejemplo: VehicleController (fleet-service — HU-01)

```java
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final RegisterVehicleUseCase registerVehicleUseCase;
    private final WebMapper mapper;

    // HU-01 subtask DEV#6: POST /api/vehicles
    @PostMapping
    public ResponseEntity<VehicleResponse> registerVehicle(
            @RequestBody RegisterVehicleRequest request) {

        RegisterVehicleCommand command = mapper.toCommand(request);
        RegisterVehicleResponse result = registerVehicleUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toResponse(result));
    }
}
```

---

#### Ejemplo: MileageController (fleet-service — HU-04 + HU-05)

```java
@RestController
@RequestMapping("/api/vehicles")
public class MileageController {

    private final RegisterMileageUseCase registerMileageUseCase;
    private final WebMapper mapper;

    // HU-04 subtask DEV#6: POST /api/vehicles/{placa}/mileage
    @PostMapping("/{plate}/mileage")
    public ResponseEntity<MileageResponse> registerMileage(
            @PathVariable String plate,
            @RequestBody RegisterMileageRequest request) {

        RegisterMileageCommand command = mapper.toCommand(plate, request);
        RegisterMileageResponse result = registerMileageUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toResponse(result));
    }
}
```

---

#### Ejemplo: MaintenanceRuleController (rules-alerts-service — HU-07 + HU-09)

```java
@RestController
@RequestMapping("/api/maintenance-rules")
public class MaintenanceRuleController {

    private final CreateMaintenanceRuleUseCase createRuleUseCase;
    private final AssociateRuleToVehicleTypeUseCase associateUseCase;
    private final WebMapper mapper;

    // HU-07 subtask DEV#6: POST /api/maintenance-rules
    @PostMapping
    public ResponseEntity<RuleResponse> createRule(
            @RequestBody CreateRuleRequest request) {

        CreateRuleCommand command = mapper.toCommand(request);
        CreateRuleResponse result = createRuleUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toResponse(result));
    }

    // HU-09 subtask DEV#6: POST /api/maintenance-rules/{id}/vehicle-types
    @PostMapping("/{id}/vehicle-types")
    public ResponseEntity<AssociationResponse> associateToVehicleType(
            @PathVariable UUID id,
            @RequestBody AssociateRuleRequest request) {

        AssociateRuleCommand command = mapper.toCommand(id, request);
        AssociationResponse result = associateUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toResponse(result));
    }
}
```

---

#### Ejemplo: MaintenanceController (rules-alerts-service — HU-13)

```java
@RestController
@RequestMapping("/api/vehicles")
public class MaintenanceController {

    private final RegisterMaintenanceUseCase registerMaintenanceUseCase;
    private final WebMapper mapper;

    // HU-13 subtask DEV#8: POST /api/vehicles/{placa}/maintenance
    @PostMapping("/{plate}/maintenance")
    public ResponseEntity<MaintenanceResponse> registerMaintenance(
            @PathVariable String plate,
            @RequestBody RegisterMaintenanceRequest request) {

        RegisterMaintenanceCommand command = mapper.toCommand(plate, request);
        RegisterMaintenanceResponse result = registerMaintenanceUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toResponse(result));
    }
}
```

---

### 4.3.2 Persistencia

#### Reglas

* JPA solo vive en Infrastructure
* Nunca exponer entidades JPA al dominio

#### Flujo correcto

```
Domain ↔ JpaMapper ↔ JPA Entity ↔ JpaRepository
```

#### Ejemplo Adapter (fleet-service)

```java
@Component
public class VehicleRepositoryAdapter implements VehicleRepositoryPort {

    private final VehicleJpaRepository repository;
    private final JpaMapper mapper;

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleJpaEntity entity = mapper.toEntity(vehicle);
        VehicleJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        return repository.findByPlate(plate)
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByPlate(String plate) {
        return repository.existsByPlate(plate);
    }
}
```

---

### 4.3.3 Mensajería (RabbitMQ)

#### Reglas

* Nunca publicar eventos desde:
  * Controller ❌
  * Repository ❌
* Siempre desde:
  * Use Case ✔

#### Flujo

```
UseCase → EventPublisherPort → RabbitMqEventPublisher → RabbitMQ
```

#### Ejemplo Publisher (fleet-service)

```java
@Component
public class RabbitMqEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(IntegrationEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(
                event.getExchangeName(),
                event.getRoutingKey(),
                payload);
        } catch (Exception e) {
            // Best-effort: log error but don't fail the use case
            log.error("Failed to publish event: {}", event.getClass().getSimpleName(), e);
        }
    }
}
```

#### Ejemplo Consumer (rules-alerts-service — HU-11)

```java
@Component
public class MileageRegisteredConsumer {

    private final GenerateAlertUseCase generateAlertUseCase;

    @RabbitListener(queues = "${rabbitmq.queue.mileage-registered}")
    public void handle(String message) {
        MileageRegisteredEvent event = parseEvent(message);

        // HU-11: pasa vehicleStatus para verificar que sea ACTIVE
        GenerateAlertCommand command = new GenerateAlertCommand(
            event.getVehicleId(),
            event.getVehicleTypeId(),
            event.getVehicleStatus(),
            event.getMileage());

        generateAlertUseCase.execute(command);
    }
}
```

---

## 5. Modelo de Datos por Servicio

### 5.1 fleet-service (PostgreSQL A)

```sql
-- Tabla: vehicle_type (HU-01 subtask DEV#2)
CREATE TABLE vehicle_type (
    id              UUID PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(255)
);

-- Tabla: vehicle (HU-01 subtask DEV#1)
CREATE TABLE vehicle (
    id              UUID PRIMARY KEY,
    plate           VARCHAR(20) NOT NULL UNIQUE,
    brand           VARCHAR(100) NOT NULL,
    model           VARCHAR(100) NOT NULL,
    year            INT NOT NULL,
    fuel_type       VARCHAR(50) NOT NULL,
    vin             VARCHAR(17) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    current_mileage BIGINT NOT NULL DEFAULT 0,
    vehicle_type_id UUID NOT NULL REFERENCES vehicle_type(id)
);

-- Tabla: mileage_log (HU-04 subtask DEV#1)
CREATE TABLE mileage_log (
    id              UUID PRIMARY KEY,
    vehicle_id      UUID NOT NULL REFERENCES vehicle(id),
    mileage_value   BIGINT NOT NULL,
    recorded_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    recorded_by     VARCHAR(100) NOT NULL
);
```

### 5.2 rules-alerts-service (PostgreSQL B)

```sql
-- Tabla: maintenance_rule (HU-07 subtask DEV#1)
CREATE TABLE maintenance_rule (
    id                    UUID PRIMARY KEY,
    name                  VARCHAR(100) NOT NULL,
    maintenance_type      VARCHAR(100) NOT NULL,
    interval_km           BIGINT NOT NULL CHECK (interval_km > 0),
    warning_threshold_km  BIGINT,
    status                VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

-- Tabla: rule_vehicle_type_assoc (HU-09 subtask DEV#1)
CREATE TABLE rule_vehicle_type_assoc (
    id              UUID PRIMARY KEY,
    rule_id         UUID NOT NULL REFERENCES maintenance_rule(id),
    vehicle_type_id UUID NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(rule_id, vehicle_type_id)
);

-- Tabla: maintenance_alert (HU-11 subtask DEV#1)
CREATE TABLE maintenance_alert (
    id              UUID PRIMARY KEY,
    vehicle_id      UUID NOT NULL,
    rule_id         UUID NOT NULL REFERENCES maintenance_rule(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    triggered_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    due_at_km       BIGINT NOT NULL
);

-- Tabla: maintenance_record (HU-13 subtask DEV#1)
CREATE TABLE maintenance_record (
    id                  UUID PRIMARY KEY,
    vehicle_id          UUID NOT NULL,
    alert_id            UUID REFERENCES maintenance_alert(id),
    rule_id             UUID REFERENCES maintenance_rule(id),
    description         VARCHAR(255) NOT NULL,
    cost                DECIMAL(10,2),
    provider            VARCHAR(100),
    performed_at        TIMESTAMP NOT NULL,
    mileage_at_service  BIGINT NOT NULL
);
```

**Nota sobre datos compartidos:**
- `rule_vehicle_type_assoc.vehicle_type_id` y `maintenance_alert.vehicle_id` son UUIDs que referencian entidades de `fleet-service`
- No hay FK cross-database; la integridad se mantiene a nivel de aplicación
- `rules-alerts-service` recibe `vehicleId`, `vehicleTypeId`, `vehicleStatus` y `mileage` a través de `MileageRegisteredEvent`

---

## 6. API Contracts por Servicio

### 6.1 fleet-service

| Método | Endpoint | HU | Subtask | Descripción |
|--------|----------|-----|---------|-------------|
| POST | `/api/vehicles` | HU-01 | DEV#6 | Registrar vehículo con tipo asociado |
| POST | `/api/vehicles/{plate}/mileage` | HU-04, HU-05 | DEV#6 | Registrar kilometraje (incluye validación de coherencia) |

**Nota HU-05:** Las validaciones de coherencia del km se ejecutan dentro del endpoint de HU-04, no exponen un endpoint independiente (SUBTASKS.md nota explícita).

### 6.2 rules-alerts-service

| Método | Endpoint | HU | Subtask | Descripción |
|--------|----------|-----|---------|-------------|
| POST | `/api/maintenance-rules` | HU-07 | DEV#6 | Crear regla de mantenimiento |
| POST | `/api/maintenance-rules/{id}/vehicle-types` | HU-09 | DEV#6 | Asociar regla a tipo de vehículo |
| POST | `/api/vehicles/{plate}/maintenance` | HU-13 | DEV#8 | Registrar mantenimiento |

**Nota HU-11:** La generación de alertas no expone endpoint REST; se dispara al consumir `MileageRegisteredEvent` de RabbitMQ.

---

## 7. Eventos

---

El sistema maneja dos tipos de eventos:

### Domain Events

* Se crean en Domain
* Representan cambios de estado internos
* No conocen infraestructura
* Se registran mediante `addDomainEvent()` en el agregado

### Integration Events

* Se crean en Application (a partir de Domain Events mediante `EventMapper`)
* Se publican mediante `EventPublisherPort`
* Son los definidos en `events.md`
* Se publican **después del `save`**

### Flujo de transformación

```
Agregado.addDomainEvent(new MileageRegistered(...))
        ↓
UseCase: repository.save(aggregate)
        ↓
UseCase: List<DomainEvent> events = aggregate.pullDomainEvents()
        ↓
EventMapper.toIntegrationEvent(domainEvent, vehicle) → MileageRegisteredEvent
        ↓
EventPublisherPort.publish(integrationEvent)
        ↓
RabbitMqEventPublisher → RabbitMQ
```

### Ejemplo Domain Event

```java
public class MileageRegistered implements DomainEvent {
    private final UUID vehicleId;
    private final long mileage;
    private final LocalDateTime occurredAt;
}
```

### Ejemplo Integration Event

```java
public class MileageRegisteredEvent implements IntegrationEvent {
    private final UUID vehicleId;
    private final UUID vehicleTypeId;
    private final String vehicleStatus;  // ACTIVE | INACTIVE (HU-11)
    private final long mileage;
    private final LocalDateTime timestamp;

    @Override
    public String getExchangeName() { return "fleet.mileage"; }
    @Override
    public String getRoutingKey() { return "mileage.registered"; }
}
```

---

## 8. Modelado del Dominio

---

### Cuándo usar cada tipo

#### Agregado (extends AggregateRoot)

* Tiene identidad
* Contiene reglas de negocio
* Registra Domain Events
* Ej:
  * Vehicle (fleet-service)
  * MileageLog (fleet-service)
  * MaintenanceRule (rules-alerts-service)
  * MaintenanceAlert (rules-alerts-service)
  * MaintenanceRecord (rules-alerts-service)

---

#### Entidad

* Tiene identidad
* Vive dentro de un agregado
* Ej:
  * VehicleType (dentro de Vehicle)

---

#### Value Object

* Inmutable
* Sin identidad
* Valida invariantes en constructor
* Ej:
  * Plate (placa única)
  * Vin (17 caracteres)
  * Mileage (no negativo, creciente)
  * ServiceType

---

## 9. Manejo de Errores

---

### Tipos

#### Domain Exceptions

* Reglas de negocio
* Ej:
  * `InvalidMileageException` — km negativo o menor al actual (HU-05)
  * `DuplicatePlateException` — placa ya existe (HU-01)
  * `InvalidVinException` — VIN no tiene 17 chars (HU-01)
  * `InactiveVehicleException` — vehículo no está ACTIVE (HU-04)
  * `MissingRecordedByException` — conductor no especificado (HU-04)
  * `InvalidRuleException` — nombre vacío o intervalo ≤ 0 (HU-07)
  * `DuplicateAssociationException` — asociación ya existe (HU-09)
  * `DuplicateAlertException` — alerta ya existe para vehículo+regla (HU-11)
  * `InvalidMaintenanceException` — tipo vacío, fecha futura, km inválido (HU-13)
  * `RuleNotFoundException` — regla no encontrada (HU-09)
  * `VehicleNotFoundException` — vehículo no encontrado (HU-01, HU-04)
  * `VehicleTypeNotFoundException` — tipo de vehículo no encontrado (HU-01)

#### Technical Exceptions

* Infraestructura
* DB, HTTP, RabbitMQ, etc.

---

### Regla

* Domain lanza excepciones
* Controller las traduce a HTTP mediante `@ExceptionHandler`

#### Ejemplo GlobalExceptionHandler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // HU-01 subtask DEV#7, HU-04 subtask DEV#7, HU-07 subtask DEV#7
    @ExceptionHandler({
        DuplicatePlateException.class,
        InvalidVinException.class,
        InvalidMileageException.class,
        InactiveVehicleException.class,
        MissingRecordedByException.class,
        InvalidRuleException.class,
        DuplicateAssociationException.class,
        InvalidMaintenanceException.class
    })
    public ResponseEntity<ErrorResponse> handleDomainException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler({
        VehicleNotFoundException.class,
        RuleNotFoundException.class,
        VehicleTypeNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
}
```

---

## 10. Convenciones

---

### Nombres

* UseCase → `RegisterVehicleUseCase`
* Implementación → `RegisterVehicleUseCaseImpl`
* Port In → `RegisterVehicleUseCase`
* Port Out → `VehicleRepositoryPort`
* Adapter → `VehicleRepositoryAdapter`
* Domain Event → `MileageRegistered`
* Integration Event → `MileageRegisteredEvent`
* Exception → `InvalidMileageException`

---

### Paquetes

* `domain.model.<aggregate>`
* `domain.valueobject`
* `domain.exception`
* `domain.event`
* `application.ports.in`
* `application.ports.out`
* `application.usecase`
* `application.service`
* `application.mapper`
* `infrastructure.web.controller`
* `infrastructure.web.dto.request`
* `infrastructure.web.dto.response`
* `infrastructure.web.mapper`
* `infrastructure.persistence.entity`
* `infrastructure.persistence.repository`
* `infrastructure.persistence.adapter`
* `infrastructure.persistence.mapper`
* `infrastructure.messaging.publisher`
* `infrastructure.messaging.consumer`
* `infrastructure.messaging.event`

---

## 11. Anti-Patrones (Prohibido)

❌ Lógica en controllers
❌ Lógica en repositories
❌ Usar entidades JPA en Domain
❌ Saltarse Use Cases
❌ Publicar eventos desde Infrastructure directamente
❌ FK cross-database entre microservicios
❌ Comunicación HTTP síncrona entre servicios
❌ Domain Events publicados directamente sin transformar a Integration Events
❌ Publicar eventos antes del save

---

## 12. Checklist por Feature (para Copilot y equipo)

Cada HU debe cumplir:

* ✔ Tiene Use Case con Port In
* ✔ Usa Ports Out para acceso a datos
* ✔ Tiene entidades de dominio con validaciones de negocio
* ✔ No mezcla capas
* ✔ Publica eventos correctamente (si aplica): después del save, con EventMapper, best-effort
* ✔ Tiene DTOs de request/response separados del dominio
* ✔ Tiene JPA entities separadas del dominio con mappers
* ✔ Tiene mappers (Web DTO↔Command y JPA Entity↔Domain)
* ✔ Maneja errores con excepciones de dominio traducidas a HTTP
* ✔ Endpoints consistentes con SUBTASKS.md
* ✔ Campos de BD consistentes con SUBTASKS.md
* ✔ Reglas de negocio consistentes con USER_STORIES.md y criterios Gherkin