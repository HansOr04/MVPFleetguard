import { http, HttpResponse } from 'msw';

const RULES_URL = 'http://localhost:3002';

export const maintenanceHandlers = [
  http.post(`${RULES_URL}/api/maintenance/:plate`, async ({ params, request }) => {
    const body = (await request.json()) as Record<string, unknown>;
    const plate = params.plate as string;

    if (plate === 'NOT-FOUND') {
      return HttpResponse.json({ message: 'Vehículo no encontrado' }, { status: 404 });
    }

    if (body.alertId === 'alert-not-found') {
      return HttpResponse.json({ message: 'Alerta no encontrada' }, { status: 404 });
    }

    if (body.mileageAtService === 0) {
      return HttpResponse.json(
        { message: 'El kilometraje debe ser mayor a cero', errors: ['mileageAtService: invalid'] },
        { status: 400 },
      );
    }

    return HttpResponse.json(
      {
        id: 'record-new-001',
        plate,
        alertId: body.alertId,
        ruleId: 'rule-001',
        serviceType: body.serviceType,
        description: body.description ?? null,
        cost: body.cost ?? null,
        provider: body.provider ?? null,
        performedAt: body.performedAt ?? new Date().toISOString(),
        mileageAtService: body.mileageAtService,
        recordedBy: body.recordedBy,
        createdAt: new Date().toISOString(),
      },
      { status: 201 },
    );
  }),
];