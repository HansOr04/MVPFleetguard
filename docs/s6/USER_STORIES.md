# USER_STORIES.md — Historias de Usuario FleetGuard

**Total:** 11 HU | **Actores:** Administrador (7) · Conductor (1) · Sistema (3)

## Módulo 1: Gestión de Vehículos

## HU-01: Registrar vehículo con tipo asociado
`feature` `módulo: vehículos` `priority: high` `SP: 8` `role: DEV` `role: QA`
>**Como** administrador de flota
**Quiero** registrar un nuevo vehículo indicando su placa, marca, modelo, tipo combustible y tipo de vehículo       
**Para** centralizar mi flota con la clasificación necesaria para aplicar reglas de mantenimiento.

## Análisis de Historia de Usuario (QA)

### **Contexto de la HU**
Este es el **punto de entrada del sistema**; sin vehículos no existe flujo operativo. La asociación con el **Tipo** es fundamental, ya que las reglas de negocio se aplican de forma específica según esta categoría.

### **Metadatos**
* **Actor Principal:** Administrador
* **Impacto en el Negocio:** Alto
* **Dificultad:** Alta
* **Dependencia:** Ninguna (Es la HU inicial)
* **Habilita a:** `HU-04`, `HU-06`, `HU-11`, `HU-13`

### **Reglas de Negocio**
1.  **Placa Única:** El sistema no debe permitir el registro de dos vehículos con la misma placa.
2.  **Validación de VIN:** El campo VIN debe contener estrictamente **17 caracteres**.
3.  **Estado Inicial:** Al crearse, el vehículo debe quedar automáticamente en estado `ACTIVE`.
4.  **Tipo Obligatorio:** El campo "Tipo" es mandatorio para proceder con el registro (determina las reglas aplicables).

### Criterios de aceptación

```gherkin
Feature: Registro de vehículo con tipo asociado
 
  Scenario: Registro exitoso con datos válidos
    Given un tipo de vehículo "Camioneta" registrado en el sistema
    And no existe un vehículo con placa "ABC-1234"
    When el administrador registra un vehículo con placa "ABC-1234", marca "Toyota", modelo "Hilux", año 2023, combustible "Diesel", VIN "1HGBH41JXMN109186" y tipo "Camioneta"
    Then el vehículo se crea con estado "ACTIVE" y kilometraje inicial 0
 
  Scenario: Rechazar placa duplicada
    Given ya existe un vehículo con placa "ABC-1234"
    When el administrador intenta registrar otro con placa "ABC-1234"
    Then el sistema rechaza indicando que la placa ya está en uso
 
  Scenario: Rechazar VIN inválido
    When el administrador registra un vehículo con VIN "12345"
    Then el sistema rechaza indicando que el VIN debe tener 17 caracteres
 
  Scenario: Rechazar sin tipo de vehículo
    When el administrador registra un vehículo sin tipo asociado
    Then el sistema rechaza indicando que el tipo es obligatorio
```
## Módulo 2: Registro de Kilometraje

## HU-04 Registrar y acumular kilometraje
`feature` `módulo: kilometraje` `priority: medium` `SP: 5` `role: DEV` `role: QA`
>**Como** conductor     
**Quiero** registrar el kilometraje marcado en el odómetro de mi vehículo al terminar mi recorrido      
**Para** que el sistema mantenga el acumulado al día y pueda detectar cuando requiere mantenimiento.


## Análisis de Historia de Usuario (QA)

### **Contexto de la HU**
Representa la **acción más frecuente del sistema**. Es crítica para el módulo de mantenimiento: sin la actualización constante de los kilómetros (KM), el sistema no puede calcular ni disparar las alertas preventivas.


### **Metadatos**
* **Actor Principal:** Conductor
* **Impacto en el Negocio:** Alto
* **Dificultad:** Media
* **Dependencia:** `HU-01`
* **Habilita a:** `HU-05`, `HU-06`, `HU-11`

### **Reglas de Negocio (Criterios de Aceptación)**
1.  **Asociación de Conductor:** El registro de kilometraje debe estar vinculado obligatoriamente a un **Conductor**.
2.  **Cálculo de Alertas:** La actualización de los KM debe disparar automáticamente el recalculado de los próximos mantenimientos.
3.  **Frecuencia Operativa:** El sistema debe estar optimizado para este ingreso de datos, dado que es la acción de mayor concurrencia.

### Criterios de aceptación
```gherkin
Feature: Registro y acumulación de kilometraje
 
  Scenario: Registro exitoso
    Given un vehículo "ACTIVE" con placa "ABC-1234" y km actual 45000
    When el conductor "María Torres" registra 45350 km
    Then se crea registro con valor 45350, fecha actual y conductor "María Torres"
    And el km acumulado del vehículo se actualiza a 45350
 
  Scenario: Registro asocia conductor responsable
    Given un vehículo activo
    When el conductor "María Torres" registra 46000 km
    Then el registro queda asociado a "María Torres" con fecha y hora actual
```


## HU-05 Validar coherencia del kilometraje
`feature` `módulo: kilometraje` `priority: medium` `SP: 3` `role: DEV` `role: QA`
>**Como** sistema     
**Quiero** validar la coherencia del kilometraje ingresado por el conductor      
**Para** evitar que datos incorrectos dañen el acumulado del vehículo y provoquen alertas equivocadas


## Análisis de Historia de Usuario (QA)

### Contexto de la HU
Esta historia de usuario se desglosa de la anterior debido a su **lógica de validación compleja**. La precisión en el ingreso del kilometraje es crítica, ya que un dato incorrecto compromete la integridad de todos los cálculos y alertas posteriores del sistema.


### Metadatos
* **Actor Principal:** Sistema
* **Impacto en el Negocio:** Alto
* **Dificultad:** Media
* **Dependencia:** `HU-04`
* **Habilita a:** Integridad de `HU-06` y `HU-11`


### Reglas de Negocio
1. **Validación de Secuencialidad (Regla #1):** El sistema debe impedir el registro de un kilometraje que sea inferior al último valor registrado para ese vehículo.
2. **Integridad de Datos:** Cualquier intento de ingreso de un valor menor debe disparar una excepción de validación y bloquear la actualización de alertas dependientes.
3. **Consistencia de Alertas:** El cálculo de las historias habilitadas (`HU-06`, `HU-11`) solo debe ejecutarse tras la validación exitosa de la regla de negocio número 1.

### Criterios de aceptación
```gherkin
Feature: Validación de coherencia del kilometraje
 
  Scenario: Rechazar km menor al último
    Given un vehículo con km actual 45000
    When se registra 44500
    Then rechaza indicando que no puede ser menor a 45000
 
  Scenario: Rechazar km negativo
    When se registra -100
    Then rechaza indicando que debe ser positivo
 
  Scenario: Aceptar km igual al actual
    Given km actual 45000
    When se registra 45000
    Then acepta y el acumulado permanece en 45000
 
  Scenario: Advertir incremento excesivo
    Given km actual 45000
    When se registra 48000 (incremento > 2000 en un día)
    Then acepta con advertencia de incremento inusual
```


## HU-06 Consultar estado de mantenimiento del vehículo
`feature` `módulo: kilometraje` `priority: high` `SP: 8` `role: DEV` `role: QA`
>**Como** administrador de flota o conductor     
**Quiero** consultar si existen mantenimientos pendientes del vehículo      
**Para** tomar acciones oportunas antes de que se venza el límite definido en la regla


##   Análisis de Historia de Usuario (QA)

### Contexto de la HU
Esta historia de usuario responde a la necesidad de implementar una **consulta activa del estado de mantenimiento**. El objetivo es eliminar la dependencia exclusiva de las alertas programadas, permitiendo una verificación proactiva en cualquier momento para determinar si un vehículo requiere servicio inmediato.

### Metadatos
* **Actores Principales:** Administrador / Conductor
* **Impacto en el Negocio:** Alto
* **Dificultad:** Alta
* **Dependencias:** `HU-01`, `HU-04`, `HU-07` + `HU-09`
* **Habilita a:** Complementa la funcionalidad de `HU-11`

### Reglas de Negocio
1. **Consulta bajo Demanda:** El sistema debe permitir al usuario (Administrador o Conductor) solicitar el estado actual de servicio de un vehículo de forma manual y en tiempo real.
2. **Sincronización de Datos:** La consulta debe integrar los datos de kilometraje (`HU-04`) y las reglas de validación (`HU-07/09`) para reflejar el estado más reciente.
3. **Visibilidad de Estado:** El resultado de la consulta debe indicar claramente si el vehículo está en estado "Apto", "Próximo a Servicio" o "Servicio Requerido", basándose en los parámetros calculados.

### Criterios de aceptación
```gherkin
Feature: Evaluación de estado de mantenimiento
 
  Scenario: Vehículo al día en mantenimiento
    Given un vehículo tipo "Camioneta" con km actual 5000
    And una regla "Cambio de aceite" cada 10000 km con umbral de 500 km asociada a "Camioneta"
    When el sistema evalúa el estado de mantenimiento del vehículo
    Then el estado es "AL_DIA"
    And indica que faltan 5000 km para el próximo servicio
 
  Scenario: Vehículo próximo a mantenimiento
    Given un vehículo tipo "Camioneta" con km actual 9600
    And una regla cada 10000 km con umbral de 500 km
    When el sistema evalúa el estado
    Then el estado es "PROXIMO"
    And indica que faltan 400 km para el servicio
 
  Scenario: Vehículo con mantenimiento vencido
    Given un vehículo tipo "Camioneta" con km actual 10500
    And una regla cada 10000 km
    When el sistema evalúa el estado
    Then el estado es "VENCIDO"
    And indica que se excedió por 500 km
 
  Scenario: Vehículo sin reglas asociadas a su tipo
    Given un vehículo tipo "Moto" sin reglas asociadas
    When el sistema evalúa el estado
    Then indica que no hay reglas de mantenimiento configuradas para ese tipo
```

## Módulo 3: Reglas de Mantenimiento

### HU-07 Crear regla con tipo de mantenimiento
`feature` `módulo: reglas` `priority: media` `SP: 5` `role: DEV` `role: QA`

>**Como** administrador de flota     
**Quiero** crear una regla de mantenimiento preventivo indicando el tipo de mantenimiento y el intervalo de kilometros en que debe realizarse      
**Para** tener definidas las condiciones bajo las cuales el sistema debe alertar que un vehículo requiere intervención 

## Análisis de Historia de Usuario (QA)

### Contexto de la HU
Esta historia de usuario representa el núcleo del sistema. Define la configuración de las reglas de negocio necesarias para el funcionamiento de todo el módulo de notificaciones; sin estas reglas, la generación de alertas es inexistente.

---

### Metadatos
* **Actor Principal:** Administrador
* **Impacto en el Negocio:** Alto
* **Dificultad:** Media
* **Dependencia:** Ninguna
* **Habilita a:** `HU-09`, `HU-06`, `HU-11`, `HU-14`

---

### Reglas de Negocio
1. **Definición de Parámetros:** El sistema debe permitir al Administrador configurar las reglas base que dispararán las alertas posteriores.
2. **Disponibilidad Global:** Las reglas configuradas deben estar disponibles para ser consumidas por los módulos dependientes (`HU-09`, `HU-06`, `HU-11`, `HU-14`).
3. **Persistencia de Configuración:** Cualquier cambio en las reglas debe validarse antes de guardarse para asegurar que el motor de alertas no quede inactivo.

### Criterios de aceptación
```gherkin
Feature: Creación de regla de mantenimiento
 
  Scenario: Crear regla con intervalo km y umbral
    When el administrador crea "Cambio de aceite" con intervalo 10000 km y umbral 500 km
    Then la regla se crea con estado activo y queda disponible para asociar
 
  Scenario: Rechazar regla sin nombre
    When crea regla sin nombre
    Then rechaza indicando que es obligatorio
 
  Scenario: Rechazar intervalo en cero
    When crea regla con intervalo km 0
    Then rechaza indicando que debe ser mayor a cero
 
  Scenario: Crear regla con umbral personalizado
    When crea "Rotación de llantas" con 15000 km y umbral 1000 km
    Then se crea con umbral de advertencia de 1000 km
```

## HU-09 Asociar regla a tipo de vehículo
`feature` `módulo: reglas` `priority: medium` `SP: 5` `role: DEV` `role: QA`

>**Como** administrador de flota     
**Quiero** asociar un regla de mantenimiento a un tipo de vehículo      
**Para** que el sistema sepa qué condiciones de mantenimiento aplican a cada vehículo de la flota 

## Análisis de Historia de Usuario (QA)

### Contexto de la HU
Esta historia de usuario se enfoca en la **reutilización de reglas de negocio**. Su propósito es permitir que, una vez configurada una regla, esta pueda aplicarse de manera masiva a todos los vehículos que pertenezcan al mismo tipo, optimizando la gestión y consistencia de los datos.

### Metadatos
* **Actor Principal:** Administrador
* **Impacto en el Negocio:** Alto
* **Dificultad:** Media
* **Dependencia:** `HU-07`, `HU-01`
* **Habilita a:** `HU-06`, `HU-11`

### Reglas de Negocio
1. **Asociación por Tipo:** El sistema debe obligar a que toda regla configurada esté asociada a un tipo de vehículo específico.
2. **Herencia de Reglas:** Al crear o actualizar un vehículo (`HU-01`), el sistema debe asignarle automáticamente las reglas correspondientes a su tipo.
3. **Validación de Aplicabilidad:** Cualquier cambio en las reglas de un tipo debe impactar a todos los vehículos vinculados, garantizando la integridad de las historias habilitadas (`HU-06`, `HU-11`).

### Criterios de aceptación
```gherkin
Feature: Asociación regla a tipo de vehículo
 
  Scenario: Asociar regla a un tipo
    Given regla "Cambio de aceite" y tipo "Camioneta" existentes
    When el administrador los asocia
    Then todos los vehículos tipo "Camioneta" quedan sujetos a esa regla

 
  Scenario: Rechazar asociación duplicada
    Given regla ya asociada a "Camioneta"
    When intenta asociar la misma regla al mismo tipo
    Then rechaza indicando que ya existe esa vinculación
```
## Módulo 4: Alertas de Mantenimiento

## HU-11 Generar alerta automática por kilometraje
`feature` `módulo: alertas` `priority: High` `SP: 13` `role: DEV` `role: QA`
>**Como** sistema     
**Quiero** generar una alerta automática cuando el kilometraje total de un vehículo alcance el límite definido de una regla      
**Para** notificar oportunamente que ese vehículo requiere mantenimiento

## Análisis de Historia de Usuario (QA)

### Contexto de la HU
Esta historia de usuario representa el núcleo del valor del producto. Su implementación permite la transición de una gestión de mantenimiento reactiva a una **preventiva**, siendo el componente crítico para la generación automatizada de alertas de servicio.


### Metadatos
* **Actor Principal:** Sistema
* **Impacto en el Negocio:** Crítico
* **Dificultad:** Alta
* **Dependencias:** `HU-01`, `HU-04`, `HU-07` + `HU-09`
* **Habilita a:** `HU-12`


### Reglas de Negocio
1. **Generación de Alertas Preventivas:** El sistema debe disparar automáticamente las alertas de mantenimiento antes de que ocurra la falla, basándose en los parámetros de kilometraje y reglas configuradas.
2. **Procesamiento de Reglas Combinadas:** El motor de cálculo debe integrar la información del vehículo (`HU-01`), el kilometraje actualizado (`HU-04`) y las reglas de negocio por tipo (`HU-07/09`) para determinar la proximidad del servicio.
3. **Persistencia del Estado Preventivo:** El sistema debe actualizar el tablero de control indicando los vehículos que requieren atención inmediata según los umbrales definidos, habilitando la gestión en `HU-12`.

### Criterios de aceptación
```gherkin
Feature: Alerta automática por kilómetros
 
  Scenario: Generar alerta al alcanzar umbral
    Given vehículo con km 9500, regla cada 10000 km, umbral 500, último servicio a 0 km
    When el sistema evalúa las reglas
    Then genera alerta "PENDING" indicando servicio a 10000 km
 
  Scenario: No generar si está lejos del umbral
    Given vehículo con km 7000, regla cada 10000 km, umbral 500
    When evalúa
    Then no genera alerta
 
  Scenario: No duplicar alertas existentes
    Given vehículo con km 9600 y alerta "PENDING" existente para esa regla
    When evalúa
    Then no crea nueva alerta
 
  Scenario: No evaluar vehículos inactivos
    Given vehículo "INACTIVE" con km que supera umbral
    When evalúa
    Then no genera alerta
 
  Scenario: Alerta en umbral exacto
    Given vehículo con km 9500, regla cada 10000 km, umbral exacto 500
    When evalúa
    Then genera alerta "PENDING"
```

## HU-12 Consultar y clasificar alertas
`feature` `módulo: alertas` `priority: medium` `SP: 3` `role: DEV` `role: QA`
>**Como** administrador de flota o conductor     
**Quiero** consultar las alertas activas que tiene un vehículo y poderlas clasificar según su estado      
**Para** priorizar los mantenimientos más urgentes y mantener el vehículo al día y en operación

## Análisis de Historia de Usuario (QA)

### Contexto de la HU
Esta historia de usuario tiene como objetivo proporcionar una clasificación visual y jerárquica para el Administrador cuando existen múltiples vehículos en el sistema. Al diferenciar los vehículos urgentes de aquellos que aún tienen margen de operación, se facilita la toma de decisiones rápida y la priorización de recursos.

### Metadatos
* **Actor Principal:** Administrador
* **Impacto en el Negocio:** Alto
* **Dificultad:** Alta
* **Dependencia:** `HU-11`
* **Habilita a:** `HU-13`


### Reglas de Negocio
1. **Clasificación por Estado:** El sistema debe agrupar y filtrar automáticamente las alertas basándose en su nivel de urgencia o estado de mantenimiento.
2. **Jerarquización de Visualización:** El Administrador debe poder distinguir claramente entre vehículos con servicio "Urgente" y vehículos con "Margen", permitiendo una lectura rápida del inventario.
3. **Consistencia con el Motor de Alertas:** Los estados mostrados en esta vista deben ser coherentes con los cálculos preventivos generados en la `HU-11`.

### Criterios de aceptación
```gherkin
Feature: Consulta y clasificación de alertas
 
  Scenario: Filtrar alertas por estado PENDING
    Given existen 5 alertas PENDING, 3 WARNING y 2 OVERDUE
    When el administrador filtra por estado "PENDING"
    Then el sistema retorna las 5 alertas pendientes
 
  Scenario: Consultar todas las alertas activas
    Given existen alertas en diferentes estados
    When el administrador consulta sin filtro de estado
    Then retorna todas las alertas no resueltas con vehículo, regla, estado y fecha
 
  Scenario: Alerta escala de WARNING a OVERDUE
    Given una alerta en estado "WARNING" para un vehículo que superó el límite de km
    When el sistema re-evalúa las alertas
    Then el estado cambia a "OVERDUE"
 
  Scenario: No mostrar alertas resueltas por defecto
    Given existen 3 alertas "RESOLVED" y 2 "PENDING"
    When el administrador consulta alertas activas
    Then solo muestra las 2 "PENDING"
```
## Módulo 5: Historial de Mantenimientos
## HU-13 Registrar mantenimiento
`feature` `módulo: historial` `priority: Alta` `SP: 8` `role: DEV` `role: QA`
>**Como** administrador de flota     
**Quiero** registrar que un mantenimiento fue realizado sobre un vehículo       
**Para** dejar constancia del mantenimiento y cerrar la alerta generada

## Análisis de Historia de Usuario (QA)

### Contexto de la HU
Esta historia de usuario es crítica para el cierre del ciclo de mantenimiento. Sin el registro formal del servicio realizado, las alertas del sistema permanecerían abiertas de forma indefinida, invalidando la utilidad del motor de notificaciones y la precisión de los estados de los vehículos.

### Metadatos
* **Actor Principal:** Administrador
* **Impacto en el Negocio:** Alto
* **Dificultad:** Alta
* **Dependencias:** `HU-01` y `HU-12`
* **Habilita a:** `HU-14` y `HU-16`

### Reglas de Negocio (Criterios de Aceptación)
1. **Cierre de Alertas:** Al registrar formalmente un servicio de mantenimiento, el sistema debe cerrar automáticamente todas las alertas activas asociadas a ese evento específico.
2. **Actualización de Historial:** El registro debe quedar vinculado permanentemente al vehículo (`HU-01`) para permitir el seguimiento en auditorías futuras.
3. **Reinicio de Ciclo:** Una vez guardado el servicio, el sistema debe habilitar el flujo para el cálculo del próximo mantenimiento, permitiendo la operatividad de las historias `HU-14` y `HU-16`.

### Criterios de aceptación
```gherkin
Feature: Registro de mantenimiento
 
  Scenario: Registrar mantenimiento con datos completos
    Given un vehículo con placa "ABC-1234"
    When registra "Cambio de aceite" con costo 85.00, proveedor "Taller Central" y observaciones "Sin novedades"
    Then se crea el registro de mantenimiento asociado al vehículo
 
  Scenario: Rechazar registro sin tipo de servicio
    When registra mantenimiento sin indicar tipo
    Then rechaza indicando que el tipo es obligatorio
 
  Scenario: Registrar mantenimiento y resolver alerta pendiente
    Given vehículo con alerta "PENDING" para "Cambio de aceite"
    When registra el servicio de "Cambio de aceite"
    Then la alerta cambia a estado "RESOLVED"
```

## HU-14 Asociar mantenimiento a regla aplicada
`feature` `módulo: historial` `priority: medium` `SP: 5` `role: DEV` `role: QA`
>**Como** administrador de flota     
**Quiero** vincular el mantenimiento registrado a la regla que lo originó       
**Para** que el sistema reinicie el contador y sepa cuándo volver a generar una alerta para ese vehículo.

## Análisis de Historia de Usuario (QA)

### Contexto de la HU
Esta historia de usuario representa la finalización y el reinicio del flujo operativo. Al permitir el **cierre del ciclo preventivo**, el sistema se transforma en un modelo cíclico donde la ejecución de un mantenimiento habilita automáticamente la programación de la siguiente intervención, garantizando la continuidad operativa del vehículo.


### Metadatos
* **Actor Principal:** Administrador
* **Impacto en el Negocio:** Alto
* **Dificultad:** Media
* **Dependencia:** `HU-13`
* **Habilita a:** Generación de un nuevo ciclo de alertas

### Reglas de Negocio (Criterios de Aceptación)
1. **Reinicio de Parámetros:** Tras el cierre del ciclo actual, el sistema debe resetear los contadores o umbrales necesarios para iniciar el seguimiento del próximo intervalo de mantenimiento.
2. **Generación de Siguiente Alerta:** El sistema debe calcular y programar automáticamente la nueva fecha o kilometraje de servicio basado en las reglas de negocio vigentes.
3. **Continuidad del Flujo:** El estado del vehículo debe transicionar de "Servicio Realizado" a "Operativo / Ciclo Iniciado", permitiendo que el motor de alertas vuelva a monitorear el activo sin intervención manual adicional.

### Criterios de aceptación
```gherkin
Feature: Asociación a regla y reseteo de contador
 
  Scenario: Asociar y resetear contador
    Given vehículo con km 10200 y regla "Cambio de aceite" cada 10000 km
    When se asocia el mantenimiento a esa regla
    Then el próximo servicio se programa a 20200 km
 
  Scenario: Asociar sin alerta previa (mantenimiento voluntario)
    Given vehículo sin alerta pendiente para "Rotación de llantas"
    When registra servicio y asocia a regla "Rotación de llantas"
    Then el contador se resetea desde el km actual
 
  Scenario: Resolver alerta al asociar
    Given vehículo con alerta "PENDING" para "Cambio de aceite"
    When se asocia el mantenimiento a esa regla
    Then la alerta cambia a "RESOLVED"
    And el contador se resetea
 
  Scenario: Verificar cálculo correcto del próximo servicio
    Given vehículo con km 15300 y regla cada 15000 km
    When asocia mantenimiento a la regla
    Then próximo servicio programado a 30300 km
```

## HU-16 Registrar fecha y km del servicio
`feature` `módulo: historial` `priority: Medium` `SP: 3` `role: DEV` `role: QA`
>**Como** administrador de flota     
**Quiero** registrar la fecha y los kilometros que llevaba al momento del mantenimiento     
**Para** que el sistema calcule correctamente cuándo debe generarse la próxima alerta

## Análisis de Historia de Usuario (QA)

### Contexto de la HU
Esta historia de usuario aborda la **desincronización temporal** de los datos. Reconoce que el servicio físico puede realizarse en una fecha distinta a su registro en el sistema, lo que genera una discrepancia entre el kilometraje (KM) registrado en el momento del mantenimiento y el KM actual del vehículo al momento de ingresar la información.

### Metadatos
* **Actor Principal:** Administrador
* **Impacto en el Negocio:** Medio
* **Dificultad:** Media
* **Dependencia:** `HU-13`
* **Habilita a:** `HU-14`


### Reglas de Negocio (Criterios de Aceptación)
1. **Ingreso Retroactivo:** El sistema debe permitir al Administrador especificar la fecha real y el kilometraje exacto en el que se realizó el servicio, independientemente de la fecha actual de registro.
2. **Validación de Consistencia:** El kilometraje ingresado para el servicio debe ser coherente con el historial; no puede ser inferior a un registro de mantenimiento previo, pero puede ser menor al kilometraje actual de circulación.
3. **Ajuste de Próxima Alerta:** El cálculo para el siguiente ciclo de mantenimiento (`HU-14`) debe basarse en el kilometraje reportado del servicio realizado, y no en el kilometraje actual del vehículo, para mantener la frecuencia técnica correcta.

### Criterios de aceptación
```gherkin
Feature: Registro de fecha y km del servicio
 
  Scenario: Registrar con fecha y km específicos del servicio
    Given un vehículo con km actual 10500
    When el administrador registra un servicio con fecha "2026-03-15" y km al servicio de 10200
    Then el registro almacena fecha "2026-03-15" y km 10200
    And el próximo mantenimiento se calcula desde los 10200 km
 
  Scenario: Rechazar km del servicio mayor al km actual del vehículo
    Given un vehículo con km actual 10500
    When el administrador registra con km al servicio de 11000
    Then el sistema rechaza indicando que el km no puede superar el actual
 
  Scenario: Rechazar fecha futura
    When el administrador registra un servicio con fecha "2027-01-01"
    Then el sistema rechaza indicando que la fecha no puede ser futura
```