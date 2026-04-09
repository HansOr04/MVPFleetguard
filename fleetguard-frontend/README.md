# fleetguard-frontend

Interfaz web del sistema FleetGuard. Permite registrar vehículos, actualizar kilometraje, configurar reglas de mantenimiento, visualizar alertas y registrar servicios de mantenimiento realizados.

---

## Stack tecnológico

| Tecnología | Versión |
|---|---|
| Next.js | 14.2.15 (App Router) |
| React | 18 |
| TypeScript | 5 |
| Tailwind CSS | 3.4.1 |
| Vitest | 3.1.1 |
| Testing Library | 16 |
| MSW (Mock Service Worker) | 2.13.2 |

---

## Páginas

| Ruta | Descripción |
|---|---|
| `/` | Dashboard principal con accesos directos a cada sección |
| `/register` | Formulario para registrar un nuevo vehículo |
| `/mileage` | Actualizar el kilometraje de un vehículo |
| `/rules` | Crear reglas de mantenimiento y asociarlas a tipos de vehículo |
| `/services` | Registrar un servicio de mantenimiento realizado y resolver su alerta |

---

## Variables de entorno

### Build time (prefijo `NEXT_PUBLIC_`)

Estas variables se inyectan en el momento del build y quedan disponibles en el navegador. En Docker se pasan como `build-args`.

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `NEXT_PUBLIC_FLEET_SERVICE_URL` | URL base del proxy interno para fleet-service | `http://localhost:3000/api/fleet` |
| `NEXT_PUBLIC_RULES_SERVICE_URL` | URL base del proxy interno para rules-alerts-service | `http://localhost:3000/api/rules` |

### Runtime (solo servidor Next.js)

| Variable | Descripción | Ejemplo Docker |
|---|---|---|
| `FLEET_SERVICE_URL` | URL real de fleet-service (usada por el proxy de Next.js) | `http://fleet-service:8080` |
| `RULES_SERVICE_URL` | URL real de rules-alerts-service (usada por el proxy de Next.js) | `http://rules-alerts-service:8080` |

> El frontend actúa como proxy: el navegador llama a `/api/fleet/...` y Next.js reenvía la petición al backend real usando `FLEET_SERVICE_URL` / `RULES_SERVICE_URL`. Esto evita problemas de CORS.

Para desarrollo local crea un archivo `.env.local` en esta carpeta:

```env
NEXT_PUBLIC_FLEET_SERVICE_URL=http://localhost:3000/api/fleet
NEXT_PUBLIC_RULES_SERVICE_URL=http://localhost:3000/api/rules
FLEET_SERVICE_URL=http://localhost:8092
RULES_SERVICE_URL=http://localhost:8093
```

---

## Instalación y ejecución

### Prerrequisitos

- Node.js 20+
- Los backends corriendo (ver [README raíz](../README.md) para levantar la infraestructura)

### Desarrollo

```bash
npm install
npm run dev
```

Disponible en `http://localhost:3000`.

### Build de producción

```bash
npm run build
npm start
```

---

## Scripts disponibles

| Script | Descripción |
|---|---|
| `npm run dev` | Servidor de desarrollo con hot-reload |
| `npm run build` | Build de producción |
| `npm start` | Servidor de producción (requiere build previo) |
| `npm run lint` | Linting con ESLint |
| `npm run test` | Ejecuta todos los tests (unit + integration) |
| `npm run test:coverage` | Tests unitarios con reporte de cobertura |
| `npm run test:integration` | Tests de integración |

---

## Tests

El proyecto utiliza **Vitest** con dos proyectos configurados en `vitest.config.ts`:

### Unitarios

Cubren hooks, servicios, validadores y la capa de API. Se ejecutan en entorno `jsdom`.

```bash
npm run test:coverage
```

El reporte de cobertura se genera en `coverage/`. Umbrales mínimos:

| Métrica | Umbral |
|---|---|
| Líneas | 80% |
| Funciones | 80% |
| Ramas | 70% |
| Sentencias | 80% |

### Integración

Prueban flujos completos de componentes con MSW para mockear las llamadas a los backends.

```bash
npm run test:integration
```

Los tests de integración se ubican en `src/__integration__/`.

---

## Estructura de carpetas relevante

```
fleetguard-frontend/
├── src/
│   ├── app/                        # Rutas (App Router de Next.js)
│   │   ├── api/
│   │   │   ├── fleet/[...path]/    # Proxy → fleet-service
│   │   │   └── rules/[...path]/    # Proxy → rules-alerts-service
│   │   ├── register/               # Página de registro de vehículos
│   │   ├── mileage/                # Página de actualización de kilometraje
│   │   ├── rules/                  # Página de reglas de mantenimiento
│   │   └── services/               # Página de registro de servicios
│   ├── components/                 # Componentes React reutilizables
│   ├── hooks/                      # Custom hooks (useRegisterVehicleForm, useMileageForm, etc.)
│   ├── lib/
│   │   ├── api.ts                  # Cliente HTTP centralizado
│   │   └── mocks/                  # Datos mock para modo demo y tests
│   ├── services/                   # Capa de acceso a la API (vehicle, alert, rule, maintenance)
│   ├── validators/                 # Lógica de validación de formularios
│   └── types/                      # Tipos TypeScript compartidos
│   ├── __tests__/                  # Tests unitarios
│   └── __integration__/            # Tests de integración
├── vitest.config.ts                # Configuración de Vitest (proyectos unit + integration)
├── vitest.setup.ts                 # Setup global de tests (jest-dom matchers)
└── Dockerfile                      # Multi-stage build (Node 20 Alpine)
```

---

## Modo demo

Si alguno de los backends no está disponible, `src/lib/api.ts` tiene un mecanismo de fallback que devuelve datos mock predefinidos. Esto permite explorar la interfaz sin necesidad de levantar los servicios backend.

---

## Docker

```bash
# Build de la imagen (desde esta carpeta)
docker build \
  --build-arg NEXT_PUBLIC_FLEET_SERVICE_URL=http://localhost:3000/api/fleet \
  --build-arg NEXT_PUBLIC_RULES_SERVICE_URL=http://localhost:3000/api/rules \
  -t fleetguard-frontend .
```

La imagen usa Next.js en modo `standalone` y expone el puerto `3000`.
