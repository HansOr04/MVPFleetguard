# 📄 `implementation.md – FleetGuard MVP`

## 1. Propósito del Documento

Este documento define **cómo debe implementarse el código** del sistema FleetGuard.

Su objetivo es:

* Garantizar consistencia en la implementación
* Evitar ambigüedad al usar IA (Copilot)
* Asegurar cumplimiento de arquitectura hexagonal
* Evitar malas prácticas

---

## 2. Reglas Generales

* Nunca mezclar capas
* Toda lógica de negocio vive en **Domain**
* Application **orquesta, no decide**
* Infrastructure **implementa, no contiene reglas**
* Todo acceso externo se hace mediante **Ports**
* No se accede directamente a repositorios desde controllers

---

## 3. Implementación por Capas

---

## 3.1 Domain

### Qué contiene

* Agregados
* Entidades
* Value Objects
* Excepciones de negocio
* Domain Events

---

### Reglas obligatorias

* No usar:

  * `@Entity`
  * `@Service`
  * `@Component`
* No depender de frameworks
* Toda validación de negocio debe estar aquí

---

### Ejemplo

```java
public class Vehicle {

    private final Plate plate;
    private Mileage mileage;

    public void registerMileage(Mileage newMileage) {
        if (newMileage.isLessThan(this.mileage)) {
            throw new InvalidMileageException();
        }
        this.mileage = newMileage;
    }
}
```

---

## 3.2 Application

---

### Responsabilidad

Orquestar casos de uso.

---

### Estructura de un Use Case

```java
public class RegisterMileageUseCaseImpl implements RegisterMileageUseCase {

    private final VehicleRepositoryPort vehicleRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    public void execute(RegisterMileageCommand command) {

        Vehicle vehicle = vehicleRepository.findById(command.getVehicleId());

        vehicle.registerMileage(command.getMileage());

        vehicleRepository.save(vehicle);

        eventPublisher.publish(new MileageRegisteredEvent(...));
    }
}
```

---

### Reglas

* No contiene lógica de negocio compleja
* Siempre usa **Ports**
* Nunca usa JPA directamente
* Nunca contiene lógica de validación de reglas (eso es Domain)

---

### Servicios de Application

Se usan cuando:

* La lógica **no pertenece a un agregado**
* Ej: validaciones cruzadas simples

Ejemplo:

```
MileageValidatorService
```

---

## 3.3 Infrastructure

---

### Responsabilidad

Implementar detalles técnicos.

---

## 3.3.1 Controllers

### Reglas

* No contienen lógica de negocio
* Solo:

  * Reciben request
  * Transforman DTO → Command
  * Invocan Use Case
  * Devuelven respuesta

---

### Ejemplo

```java
@PostMapping("/mileage")
public ResponseEntity<?> registerMileage(@RequestBody RegisterMileageRequest request) {

    registerMileageUseCase.execute(mapper.toCommand(request));

    return ResponseEntity.ok().build();
}
```

---

## 3.3.2 Persistencia

---

### Reglas

* JPA solo vive en Infrastructure
* Nunca exponer entidades JPA al dominio

---

### Flujo correcto

```
Domain ↔ Mapper ↔ JPA Entity ↔ Repository
```

---

### Ejemplo Adapter

```java
public class VehicleRepositoryAdapter implements VehicleRepositoryPort {

    private final VehicleJpaRepository repository;
    private final VehicleJpaMapper mapper;

    @Override
    public Vehicle save(Vehicle vehicle) {
        return mapper.toDomain(
            repository.save(mapper.toEntity(vehicle))
        );
    }
}
```

---

## 3.3.3 Eventos (RabbitMQ)

---

### Reglas

* Nunca publicar eventos desde:

  * Controller ❌
  * Repository ❌

* Siempre desde:

  * Use Case ✔

---

### Flujo

```
UseCase → EventPublisherPort → RabbitMQ Adapter
```

---

### Ejemplo

```java
eventPublisher.publish(new MileageRegisteredEvent(...));
```

---

## 4. Modelado del Dominio

---

## 4.1 Cuándo usar cada tipo

### Agregado

* Tiene identidad
* Contiene reglas de negocio
* Ej:

  * Vehicle
  * MaintenanceRule
  * MaintenanceAlert

---

### Entidad

* Tiene identidad
* Vive dentro de un agregado

---

### Value Object

* Inmutable
* Sin identidad
* Ej:

  * Plate
  * Mileage
  * Vin

---

---

## 5. Eventos

---

### Reglas

* Se crean en Domain
* Se publican en Application
* No contienen lógica

---

### Ejemplo

```java
public class MileageRegisteredEvent {
    private final UUID vehicleId;
    private final Long mileage;
}
```

---

## 6. Manejo de Errores

---

### Tipos

#### Domain Exceptions

* Reglas de negocio
* Ej:

  * InvalidMileageException

#### Technical Exceptions

* Infraestructura
* DB, HTTP, etc.

---

### Regla

* Domain lanza excepciones
* Controller las traduce a HTTP

---

---

## 7. Convenciones

---

### Nombres

* UseCase → `RegisterVehicleUseCase`
* Implementación → `RegisterVehicleUseCaseImpl`
* Port → `VehicleRepositoryPort`
* Adapter → `VehicleRepositoryAdapter`

---

### Paquetes

* domain.model
* application.usecase
* infrastructure.web
* infrastructure.persistence

---

---

## 8. Anti-Patrones (Prohibido)

---

❌ Lógica en controllers
❌ Lógica en repositories
❌ Usar entidades JPA en Domain
❌ Saltarse Use Cases
❌ Publicar eventos desde Infrastructure directa

---

## 9. Checklist por Feature (para Copilot y ustedes)

Cada HU debe cumplir:

* Tiene Use Case ✔
* Tiene Port In ✔
* Usa Ports Out ✔
* Tiene entidades de dominio ✔
* No mezcla capas ✔
* Publica eventos correctamente (si aplica) ✔