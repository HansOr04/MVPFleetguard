# PRD — Sistema de Gestión de Flota y Mantenimiento Preventivo
 
**Sofware:** FleetGuard — Gestión de Flota y Mantenimiento Automotriz  
**Fecha:** 18/03/2026

**Equipo:**
 
| Rol | Integrante | País |
|-----|-----------|------|
| QA  | Hans Ortiz | Ecuador |
| DEV | Javier Luis | Colombia |

## Problema a resolver

El problema que se encontro en Automotive es que al gestionar su flota lo hace de forma manual y desorganizada, no tienen un mecanismo centralizado donde registre el kilometraje de cada vehículo ni que indique cuándo corresponde el mantemiento del vehiculo.
Lo que esto genera que tengan intervenciones tardías, riesgos altos de accidentes o posibles sanciones a nivel vehicular o de empresa, ademas al no hacer los mantimientos en el tiempo correcto, suben los costos de reparacion debido a la gravedad aumentada que se tiene.

## Vision
FleetGuard es un software para Automotive que sistematiza la gestion de mantenimiento de su flota vehicular. A través del registro periódico de kilometraje por vehículo, fleetguard aplica reglas de mantenimiento preventivo y alerta oportunamente cuando un vehiculo requiere intervencion, asi logramos evitar fallas, accidentes o sanciones por un descuido operativo. 

## Objetivo General
Desarrollar una aplicación que permita a Automotive gestionar de forma centralizada y preventiva el mantenimiento de su flota, mediante el seguimiento del Kilometraje y la asignación de reglas de mantenimiento según el tipo de vehículo.

## Objetivos Específicos
1. Registrar y centralizar la información de cada vehículo de la flota, para tener sistematizado de forma clara el inventario operativo.
2. Permitir el registro de forma centralizada y periodica del kilometraje recorrido por cada vehículo, garantizando que los datos ingresados sean válidos antes de ser persistidos.
3. Centralizar las reglas de mantenimiento preventivo según el tipo de vehículo, permitiendo crearlas y asociarlas con facilidad.
4. Generar alertas automáticas cuando un vehículo alcance el límite de kilometraje definido en una regla, previniendo retrasos en el mantenimiento que pueden derivar en multas o accidentes.
5. Registrar el historial de mantenimiento realizados por el vehículo, incluyendo la fecha, el kilometraje del servicio y la regla que lo originó.
6. Reducir costos opertativos con relación a los mantenimientos fuera de tiempo, por medio de intervenciones preventivas planificadas con base en datos reales. 

### 1.3 Objetivos del MVP
 
| # | Objetivo | Métrica de Éxito |
|---|---------|------------------|
| O1 | Centralizar la información de la flota | 100% de vehículos registrados |
| O2 | Registrar kilometraje periódicamente | Al menos 1 vez por semana por vehículo |
| O3 | Alertar mantenimientos antes de que se venzan | ≥ 90% ejecutados antes del umbral |
| O4 | Reducir costos por mantenimiento correctivo | -30% en reparaciones de emergencia |

## Actores del Sistema
### 2.1 Administrador de Flota
 
- **Persona:** Carlos — Jefe de Operaciones, logística (25 vehículos)
- **Dolor:** Revisa Excel para saber cuál vehículo necesita servicio. 3 fallas en carretera este año.
- **Necesidad:** Sistema que le diga qué vehículo necesita mantenimiento y cuándo.
- **Interacción:** Gestiona vehículos, reglas, alertas (consulta) e historial.
- **HU:** HU-01, HU-07, HU-09, HU-12, HU-13, HU-14, HU-16.

### 2.2 Conductor
 
- **Persona:** María — Conductora de reparto
- **Dolor:** No tiene forma rápida de reportar kilometraje de su vehículo.
- **Necesidad:** Registro rápido del kilometraje.
- **Interacción:** Solo registra kilometraje.
- **HU:** HU-04.

### 2.3 Sistema Automático (Actor No Humano)
 
- **Descripción:** Backend que valida datos, evalúa estado de mantenimiento y genera alertas sin intervención humana.
- **Interacción:** Vía scheduler o eventos post-registro.
- **HU:** HU-05, HU-06, HU-11.

## 3. Alcance del MVP
 
### 3.1 IN — 11 Historias de Usuario

| Módulo | HU | Funcionalidad | Actor |
|--------|-----|--------------|-------|
| **Vehículos** | HU-01 | Registrar vehículo con tipo asociado | Administrador |
| **Kilometraje** | HU-04 | Registrar y acumular kilometraje | Conductor |
| | HU-05 | Validar coherencia del km | Sistema |
| | HU-06 | Consultar estado de mantenimiento del vehículo | Sistema |
| **Reglas** | HU-07 | Crear regla con tipo de mantenimiento | Administrador |
| | HU-09 | Asociar regla a tipo de vehículo | Administrador |
| **Alertas** | HU-11 | Generar alerta automática por km | Sistema |
| | HU-12 | Consultar y clasificar alertas por estado | Administrador |
| **Historial** | HU-13 | Registrar mantenimiento realizado | Administrador |
| | HU-14 | Asociar mantenimiento a regla y resetear contador | Administrador |
| | HU-16 | Registrar fecha y km del servicio | Administrador |

### 3.3 OUT — Futuras versiones
 
| Funcionalidad | Razón |
|--------------|-------|
| Consultar vehículo por placa | Se difiere al Sprint 1, el listado general cubre la necesidad básica  |
| Listar vehículos disponibles | Se difiere al Sprint 1 por alcance del MVP  |
| Consultar reglas disponibles | Se difiere al Sprint 1, no bloquea el flujo de creación |
| Modificar reglas existentes | Se difiere al Sprint 1 por alcance del MVP  |
| Eliminar reglas | Riesgo de perder trazabilidad de mantenimientos anteriores |
| Obtener historial por placa | Trazabilidad completa para Sprint 1 |
| Generar alerta por tiempo | Requiere lógica de scheduler, complejidad alta para MVP |
| Deshabilitar/habilitar vehículo | No bloquea el flujo preventivo del MVP |
| Modificar vehículo por placa | MVP prioriza el registro inicial, los datos no son editables por ahora |
| GPS / Telemetría | Requiere integración con hardware externo |
| Gestión de combustible | MMódulo independiente, no relacionado al mantenimiento preventivo |
| App móvil nativa | MVP opera como aplicación web |
| Integración ERP | Depende de adaptadores específicos por cliente  |
| SMS / WhatsApp | MVP no contempla notificaciones externas |
| Reportes PDF | Funcionalidad de valor agregado para versiones futuras  |
| Multi-tenancy | MVP opera para una sola organización |

## 4. Reglas de Negocio
 
| ID | Regla | Ejemplo | HU |
|----|-------|---------|-----|
| RN-01 | Km registrado nunca menor al último valor | Último: 45,000 → no acepta 44,500 | HU-05 |
| RN-02 | Alerta al alcanzar umbral de advertencia por km | Mant. a 10,000 km, umbral 500 → alerta a 9,500 | HU-11 |
| RN-03 | Mantenimiento registrado resetea contador | Aceite a 10,200 → próximo a 20,200 | HU-14 |
| RN-04 | Registro de km asociado a conductor | Trazabilidad y auditoría | HU-04 |
| RN-05 | Reglas se asocian a tipos de vehículo | "Aceite 10,000 km" → todos tipo "Camioneta" | HU-09 |
| RN-06 | Alertas clasificadas por estado | PENDING → WARNING → OVERDUE → RESOLVED | HU-12 |


## 5. Riesgos
 
### 5.1 Riesgos de Negocio
 
| ID | Riesgo | Prob. | Impacto |Descripción| Mitigación |
|----|--------|-------|---------|---------|-----------|
| RB-01 | Conductores no registran km | Alta | Alto |Si los conductores no reportan el km recorrido de forma periódica, el sistema no tiene datos reales para evaluar cuándo generar una alerta, la aplicación no cumpliría su función. |El flujo de registro debe ser simple y tomar menos de 30 segundos. |
| RB-02 | Reglas no reflejan realidad operativa | Media | Medio | Si el administrador configura reglas con intervalos incorrectos, el sistema generará alertas equivocados, perdiendo confianza el producto. |Las reglas deben ser flexibles y editables por tipo de vehículo. |
| RB-03 | Admin ignora alertas | Media | Alto | Si las alertas no se atienden, el sistema funciona correctamente pero el mantenimiento igual se retrasa, que es exactamente el problema que se quiere resolver.  |Las alertas deben tener escalamiento visual según su nivel de urgencia.|
| RB-04 |  Resistencia al cambio | Alta | Medio | Al pasar de un proceso manual a uno sistematizado, es probable que conductores y administradores no adopten el sistema de inmediato.   |Acompañamiento en la migración inicial y capacitación básica de uso. |


### 5.2 Riesgos Técnicos
 
| ID | Riesgo | Prob. | Impacto | Descripción|Mitigación |
|----|--------|-------|---------|---------|-----------|
| RT-01 | Datos inconsistentes de km | Alta | Alto | Se tiene que tener en cuenta la importancia de las comas o los puntos al momento de colocar el kilometraje, por que en algunas localidades suele variar al contar decimales o al contar miles.|Necesitamos realizar una validacion estricta que coloque directamente el formato sin que el usuario se preocupe por ello |
| RT-02 | Scheduler lento con flotas grandes | Baja | Alto | Al momento de realizar una consulta o una alerta si no se realiza una optimizacion de la informacion con normalizacion de la base de datos, se podria generar un flujo demasiado lento lo que podria ocasionar fallos   | Se debe realizar querys optimizadas y una evaluacion incremental para estar optimizando cada vez las mismas |
| RT-03 | Pérdida de datos | Baja | Crítico |Si en algun momento la base de datos se ve comprometida por medidas de seguridad o por ejecuciones de Querys no debidas. |BackUps diarios debido a la perseveracion de los datos y la integridad de los mismos|
| RT-04 | Vulnerabilidades API | Media | Crítico | Situaciones donde la informacion se puede filtrar o visualizar porque no hay un medio de seguridad aplicado |Generacion de tokens, roles o validacion de la informacion en inputs para evitar riesgos de seguridad |
| RT-05 | Alertas falsas por reglas mal configuradas | Media | Alto |Al generar una alerta por no realizar bien el analisis de lo que se necesita puede dar falsos positivos | Es un testing exhaustivo con las reglas de negocio para poder validar cada una de estas, ademas de una UI intuitiva que no genere ambiguedad al momento de colocar la regla |
| RT-06 | Race conditions en registro concurrente km | Baja | Medio |Al momento de registrar un valor de km si dos condutores mandan al mismo tiempo un valor esto puede perjudicar al registro de cada vehiculo |Se realiza un locking Optimista con el cual si dos usuarios mandan un dato al mismo vehiculo a la vez guarda el primero que entro y despues ingresa nuevamente el otro valor y lo suma al nuevo valor|

### 5.3 Riesgos QA
 
| ID | Riesgo | Mitigación |
|----|--------|-----------|
| RQ-01 | Cobertura insuficiente del motor de reglas | Matriz: km variados, umbrales exactos, sin reglas |
| RQ-02 | Escenarios de borde en validación km (HU-05) | Probar: igual, negativo, excesivo |
| RQ-03 | Alertas duplicadas o perdidas (HU-11) | Pruebas de idempotencia del scheduler |
| RQ-04 | Reseteo incorrecto del contador (HU-14) | Verificar cálculo con datos controlados |
| RQ-05 | Clasificación incorrecta de alertas (HU-12) | Probar transiciones PENDING→WARNING→OVERDUE→RESOLVED |
| RQ-06 | Evaluación de estado inconsistente (HU-06) | Probar: al día, próximo, vencido, sin reglas, múltiples reglas |