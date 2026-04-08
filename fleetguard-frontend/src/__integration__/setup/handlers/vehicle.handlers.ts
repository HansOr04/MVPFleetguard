import { http, HttpResponse } from 'msw';

const FLEET_URL = 'http://localhost:3001';

export const vehicleHandlers = [
  // Registro exitoso
  http.post(`${FLEET_URL}/api/vehicles`, async ({ request }) => {
    const body = (await request.json()) as Record<string, unknown>;

    // Simular duplicado de placa
    if (body.plate === 'DUP-PLATE') {
      return HttpResponse.json(
        { message: 'La placa ya está registrada', field: 'plate' },
        { status: 409 },
      );
    }

    // Simular duplicado de VIN
    if (body.vin === '00000000000000DUP') {
      return HttpResponse.json(
        { message: 'El VIN ya está registrado', field: 'vin' },
        { status: 409 },
      );
    }

    return HttpResponse.json(
      {
        id: 'new-vehicle-id',
        plate: body.plate,
        brand: body.brand,
        model: body.model,
        year: body.year,
        fuelType: body.fuelType,
        vin: body.vin,
        status: 'ACTIVE',
        currentMileage: 0,
        vehicleTypeName: 'Sedán',
      },
      { status: 201 },
    );
  }),

  // Actualizar kilometraje
  http.post(`${FLEET_URL}/api/vehicles/:plate/mileage`, async ({ params, request }) => {
    const body = (await request.json()) as Record<string, unknown>;
    const plate = params.plate as string;

    if (plate === 'NOT-FOUND') {
      return HttpResponse.json({ message: 'Vehículo no encontrado' }, { status: 404 });
    }

    if (plate === 'INV-MILE') {
      return HttpResponse.json(
        { message: 'El nuevo kilometraje debe ser mayor al actual' },
        { status: 400 },
      );
    }

    return HttpResponse.json(
      {
        mileageLogId: 'log-new-001',
        vehicleId: 'vehicle-001',
        plate,
        previousMileage: 40000,
        mileageValue: body.mileageValue,
        kmTraveled: (body.mileageValue as number) - 40000,
        currentMileage: body.mileageValue,
        recordedBy: body.recordedBy,
        recordedAt: new Date().toISOString(),
        excessiveIncrement: (body.mileageValue as number) > 100000,
        alertId: null,
      },
      { status: 200 },
    );
  }),
];