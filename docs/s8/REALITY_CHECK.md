# REALITY CHECK — Estimación vs Realidad

## 1. Resumen

El MVP de FleetGuard lo planificamos definiendo el alcance, las historias de usuario y una estimación inicial enfocada principalmente en el backend.

Sin embargo, durante la ejecución el proyecto evolucionó más de lo esperado. No solo se implementó lo planeado, sino que se agregaron cosas que no estaban contempladas inicialmente, como el frontend completo, la infraestructura y ciertos ajustes técnicos necesarios para que todo funcionara correctamente.

En términos generales:

* Lo planificado se cumplió en su mayoría
* Se agregaron funcionalidades y decisiones técnicas no contempladas
* El esfuerzo real terminó siendo aproximadamente el doble de lo estimado

---

## 2. ¿Qué subestimamos y por qué?

### 2.1 La arquitectura

Cuando hicimos la estimación, pensamos las tareas desde lo funcional:

* crear endpoints
* validar datos
* guardar información

Pero al momento de implementar, decidí llevar el proyecto a una arquitectura más estructurada con hexagonal.

Esto implicó:

* separar dominio, aplicación e infraestructura
* crear ports y adapters
* usar mappers
* manejar errores de forma centralizada

Lo que parecía una tarea simple, en la práctica requería varias piezas.

**Qué pasó:**
Subestimamos porque la estimación no consideró cómo se iba a construir internamente el sistema.

---

### 2.2 La comunicación entre servicios

Inicialmente no dimensionamos completamente cómo se iban a comunicar los servicios.

Durante el desarrollo terminé implementando:

* comunicación por eventos (RabbitMQ)
* comunicación directa por HTTP entre servicios

Esto fue necesario para resolver cosas como:

* consultar vehículos desde otro servicio
* procesar alertas correctamente

**Qué pasó:**
Subestimamos porque asumimos una arquitectura más simple de la que realmente se necesitó.

---

### 2.3 El flujo de alertas (más complejo de lo esperado)

Algo que parecía directo como:

> generar alertas a partir del kilometraje

terminó siendo más complejo porque implicaba:

* escuchar eventos
* evaluar reglas
* evitar duplicados
* manejar estados (pendiente, advertencia, vencido)

**Qué pasó:**
Subestimamos porque no vimos toda la lógica que había detrás del comportamiento esperado.

---

### 2.4 El frontend directamente no lo consideramos

Este fue el mayor desfase.

En la planificación inicial no incluimos frontend, pero durante el desarrollo terminé construyendo:

* varias pages completas (registro de vehículos, registro de kilometraje, registro de reglas, sistema de mantenimiento)
* formularios completos
* integración con backend
* validaciones
* pruebas

**Qué pasó:**
No fue una subestimación, fue algo que simplemente no contemplamos directamente en el alcance inicial.

---

### 2.5 La infraestructura

Tampoco consideramos el esfuerzo de:

* dockerizar servicios
* configurar entornos
* levantar todo el sistema integrado

**Qué pasó:**
Subestimamos porque dimos por hecho que esto sería algo menor, cuando en realidad toma tiempo y cuidado.

---

## 3. ¿Qué estimamos bien?

Hubo partes donde la estimación fue bastante acertada:

* Validaciones simples (como las del kilometraje)
* Operaciones CRUD sin mucha lógica
* Asociaciones básicas entre entidades

**Entonces:**
Cuando el problema era claro y sin muchas dependencias, la estimación fue bastante precisa.

---

## 4. ¿Qué cambió durante el desarrollo?

Durante la implementación tomamos varias decisiones que no estaban en la planificación inicial:

* Añadir comunicación real entre servicios, comunicación asincrónica por eventos
* Ajustar la arquitectura para hacerla más limpia y mantenible
* Implementar funcionalidades que estaban diferidas pero eran necesarias (como consultar alertas)

Estas decisiones hicieron que el sistema quedara más completo, pero también aumentaron el esfuerzo.

---

## 5. ¿Qué se agregó fuera del alcance inicial?

Algunas cosas que no estaban planeadas pero terminaron siendo necesarias:

* Consulta de alertas (necesaria para el flujo de mantenimiento)
* Consulta de vehículos por placa entre servicios
* Validaciones adicionales (como VIN único, reglas únicas)


De esta manera, el alcance creció de forma natural para poder tener un flujo funcional completo.

---

## 6. ¿El MVP final es realmente valioso?

Sí.

A pesar de los cambios y el aumento en el esfuerzo, el sistema permite:

* Registrar vehículos
* Definir reglas de mantenimiento
* Asociar vehículos a reglas
* Registrar kilometraje
* Generar alertas automáticamente
* Registrar mantenimientos
* Visualizar todo desde una interfaz intuitiva

Es decir, el flujo principal está completo de inicio a fin.

Por esta razón, el MVP es funcional y demuestra claramente el valor del sistema.

---

## 7. Qué aprendimos de esta estimación

1. Estimar solo por funcionalidades no es suficiente
2. La arquitectura impacta directamente el esfuerzo
3. La integración entre servicios siempre agrega complejidad
4. El frontend pensado en la experiencia debe considerarse desde el inicio
5. Siempre hay trabajo que no es visible, por ejemplo, las configuraciones

---

## 8. Cómo mejoraríamos futuras estimaciones

Para próximos desarrollos:

* Separar estimación por:

  * backend
  * frontend
  * integración
  * infraestructura

* Considerar explícitamente:

  * arquitectura a utilizar
  * configuraciones necesarias
  * comunicación entre servicios
  * flujos de datos y reglas de negocio
  * Agregar un margen de incertidumbre técnica (por ejemplo, +30%) para cubrir aspectos no visibles inicialmente

* Agregar margen para incertidumbre técnica

---

## 9. Conclusión final

La estimación inicial no fue incorrecta, pero sí incompleta.

El proyecto terminó siendo más robusto y completo de lo que planeamos, lo cual incrementó el esfuerzo. Aun así, esto permitió construir un sistema mejor estructurado y más cercano a un entorno real.
