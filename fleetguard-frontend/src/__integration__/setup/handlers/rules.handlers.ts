import { http, HttpResponse } from 'msw';

const RULES_URL = 'http://localhost:3002';

export const rulesHandlers = [
  // Crear regla
  http.post(`${RULES_URL}/api/maintenance-rules`, async ({ request }) => {
    const body = (await request.json()) as Record<string, unknown>;

    if (!body.name || !body.maintenanceType || !body.intervalKm) {
      return HttpResponse.json(
        { message: 'Datos inválidos', errors: ['name, maintenanceType, intervalKm son requeridos'] },
        { status: 400 },
      );
    }

    return HttpResponse.json(
      {
        id: 'rule-new-integration-001',
        name: body.name,
        maintenanceType: body.maintenanceType,
        intervalKm: body.intervalKm,
        warningThresholdKm: body.warningThresholdKm ?? 0,
        status: 'ACTIVE',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
      { status: 201 },
    );
  }),

  // Asociar tipo de vehículo a una regla
  http.post(`${RULES_URL}/api/maintenance-rules/:ruleId/vehicle-types`, async ({ params, request }) => {
    const body = (await request.json()) as Record<string, unknown>;
    const ruleId = params.ruleId as string;

    if (ruleId === 'rule-fail-assoc') {
      return HttpResponse.json(
        { message: 'Error al asociar tipo de vehículo' },
        { status: 500 },
      );
    }

    if (!body.vehicleTypeId) {
      return HttpResponse.json({ message: 'vehicleTypeId requerido' }, { status: 400 });
    }

    return new HttpResponse(null, { status: 204 });
  }),
];