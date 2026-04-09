# PRD — FleetGuard MVP Core (Taller Semana 7)

**Software:** FleetGuard — Gestión de Flota y Mantenimiento Automotriz

**Equipo:**

| Rol | Integrante | País |
|-----|-----------|------|
| QA  | Hans Ortiz | Ecuador |
| DEV | Javier Luis | Colombia |

---

## Contexto

Este documento representa una reducción del PRD original definido en el proyecto PRD_BACKLOG_SEMANA_6. El equipo decidió priorizar únicamente las 7 HUs que conforman el flujo core del sistema, dejando las 4 HUs complementarias para una siguiente iteración.

---

## Problema a resolver

Automotive gestiona su flota de forma manual y desorganizada, sin un mecanismo centralizado que registre el kilometraje de cada vehículo ni que indique cuándo corresponde realizar un mantenimiento. Esto genera intervenciones tardías, riesgos de accidentes, posibles sanciones y costos elevados por mantenimientos correctivos que pudieron prevenirse.

---

## Visión

FleetGuard sistematiza la gestión de mantenimiento de la flota vehicular de Automotive. A través del registro periódico de kilometraje, el sistema aplica reglas de mantenimiento preventivo y alerta oportunamente cuando un vehículo requiere intervención.

---

## Objetivo General

Entregar un incremento funcional de punta a punta que permita registrar un vehículo, acumular su kilometraje, evaluar reglas de mantenimiento, generar alertas automáticas y registrar el mantenimiento realizado.

---

## Alcance del MVP Core — 7 HUs

| Módulo | HU | Funcionalidad | Actor |
|--------|-----|--------------|-------|
| Vehículos | HU-01 | Registrar vehículo con tipo asociado | Administrador |
| Kilometraje | HU-04 | Registrar y acumular kilometraje | Conductor |
| Kilometraje | HU-05 | Validar coherencia del km | Sistema |
| Reglas | HU-07 | Crear regla con tipo de mantenimiento | Administrador |
| Reglas | HU-09 | Asociar regla a tipo de vehículo | Administrador |
| Alertas | HU-11 | Generar alerta automática por km | Sistema |
| Historial | HU-13 | Registrar mantenimiento | Administrador |

---

## HUs diferidas a siguiente iteración

| HU | Funcionalidad | Razón |
|----|--------------|-------|
| HU-06 | Consultar estado de mantenimiento | Complementaria, no bloquea el ciclo core |
| HU-12 | Consultar y clasificar alertas | Complementaria, la alerta se genera aunque no se visualice |
| HU-14 | Asociar mantenimiento a regla | Complementaria, el registro existe sin la asociación |
| HU-16 | Registrar fecha y km del servicio | Complementaria, los campos existen en HU-13 |

---

## Flujo Core
```
Registrar vehículo (HU-01)
        ↓
Registrar kilometraje (HU-04)
        ↓
Validar coherencia (HU-05)
        ↓
Evaluar reglas y generar alerta (HU-11)
        ↓
Registrar mantenimiento (HU-13)
```

---

## Criterio de Done del MVP Core

El MVP se considera entregado cuando:

- Un vehículo puede registrarse con tipo asociado.
- El conductor puede registrar km y el sistema los acumula correctamente.
- El sistema valida que el km sea coherente antes de persistirlo.
- Las reglas de mantenimiento pueden crearse y asociarse a tipos de vehículo.
- El sistema genera una alerta automáticamente cuando corresponde.
- Un mantenimiento puede registrarse cerrando el ciclo.