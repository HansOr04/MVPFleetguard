import { http, HttpResponse } from 'msw';

const RULES_URL = 'http://localhost:3002';

const mockAlertsDB = [
  {
    id: 'alert-integration-001',
    vehicleId: 'vehicle-001',
    vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5315',
    ruleId: 'rule-001',
    ruleName: 'Cambio de aceite motor liviano',
    status: 'PENDING',
    triggeredAt: '2026-01-01T00:00:00Z',
    dueAtKm: 50000,
  },
  {
    id: 'alert-integration-002',
    vehicleId: 'vehicle-001',
    vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5315',
    ruleId: 'rule-002',
    ruleName: 'Rotacion de llantas liviano',
    status: 'WARNING',
    triggeredAt: '2026-01-05T00:00:00Z',
    dueAtKm: 45000,
  },
  {
    id: 'alert-integration-003',
    vehicleId: 'vehicle-002',
    vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5311',
    ruleId: 'rule-003',
    ruleName: 'Revision de frenos',
    status: 'OVERDUE',
    triggeredAt: '2025-12-01T00:00:00Z',
    dueAtKm: 20000,
  },
];

export const alertHandlers = [
  // Obtener todas las alertas (con filtro opcional)
  http.get(`${RULES_URL}/api/alerts`, ({ request }) => {
    const url = new URL(request.url);
    const status = url.searchParams.get('status');

    const filtered = status
      ? mockAlertsDB.filter((a) => a.status === status)
      : mockAlertsDB.filter((a) => ['PENDING', 'WARNING', 'OVERDUE'].includes(a.status));

    return HttpResponse.json(filtered, { status: 200 });
  }),

  // Obtener alertas por placa
  http.get(`${RULES_URL}/api/alerts/vehicle/:plate`, ({ params }) => {
    const plate = params.plate as string;

    if (plate === 'ABC-1234') {
      return HttpResponse.json(
        mockAlertsDB.filter(
          (a) => a.vehicleId === 'vehicle-001' && ['PENDING', 'WARNING', 'OVERDUE'].includes(a.status),
        ),
        { status: 200 },
      );
    }

    if (plate === 'NO-ALERTS') {
      return HttpResponse.json([], { status: 200 });
    }

    if (plate === 'NOT-FOUND') {
      return HttpResponse.json({ message: 'Vehículo no encontrado' }, { status: 404 });
    }

    return HttpResponse.json([], { status: 200 });
  }),
];