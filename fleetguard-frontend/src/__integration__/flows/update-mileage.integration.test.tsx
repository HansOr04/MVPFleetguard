import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, beforeAll, afterEach, afterAll } from 'vitest';
import { http, HttpResponse } from 'msw';
import { server } from '../setup/server';
import UpdateMileagePage from '@/app/mileage/page';

beforeAll(() => server.listen({ onUnhandledRequest: 'warn' }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('Integración: Actualización de Kilometraje', () => {
  it('renderiza el formulario de kilometraje', () => {
    render(<UpdateMileagePage />);
    expect(screen.getByText('Actualizar Kilometraje')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Ej: ABC-1234')).toBeInTheDocument();
  });

  it('el botón está deshabilitado cuando el formulario está vacío', () => {
    render(<UpdateMileagePage />);
    expect(
      screen.getByRole('button', { name: /actualizar odómetro/i }),
    ).toBeDisabled();
  });

  it('muestra el panel de resultado con los datos correctos tras actualizar', async () => {
    const user = userEvent.setup();
    render(<UpdateMileagePage />);

    await user.type(screen.getByPlaceholderText('Ej: ABC-1234'), 'ABC-1234');
    await user.type(screen.getByPlaceholderText('0'), '45000');
    await user.type(
      screen.getByPlaceholderText('Nombre del conductor o técnico'),
      'Juan Pérez',
    );

    await user.click(
      screen.getByRole('button', { name: /actualizar odómetro/i }),
    );

    await waitFor(() => {
      expect(screen.getByText('Registro Confirmado')).toBeInTheDocument();
    });

    expect(screen.getByText('ABC-1234')).toBeInTheDocument();
    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
    expect(
      screen.getByText('Odómetro actualizado correctamente'),
    ).toBeInTheDocument();
  });

  it('muestra las alertas generadas después del registro', async () => {
    const user = userEvent.setup();
    render(<UpdateMileagePage />);

    await user.type(screen.getByPlaceholderText('Ej: ABC-1234'), 'ABC-1234');
    await user.type(screen.getByPlaceholderText('0'), '45000');
    await user.type(
      screen.getByPlaceholderText('Nombre del conductor o técnico'),
      'Juan',
    );

    await user.click(
      screen.getByRole('button', { name: /actualizar odómetro/i }),
    );

    // Esperar el spinner con texto exacto del párrafo (no el del botón)
    await waitFor(() => {
      expect(
        screen.getByText('Verificando alertas de mantenimiento...'),
      ).toBeInTheDocument();
    });

    // Esperar el conteo exacto de alertas activas
    await waitFor(
      () => {
        expect(
          screen.getByText('2 alertas de mantenimiento activas'),
        ).toBeInTheDocument();
      },
      { timeout: 4000 },
    );
  });

  it('muestra banner de incremento excesivo cuando excessiveIncrement=true', async () => {
    server.use(
      http.post(
        'http://localhost:3001/api/vehicles/:plate/mileage',
        async ({ request }) => {
          const body = (await request.json()) as Record<string, unknown>;
          return HttpResponse.json({
            mileageLogId: 'log-excessive',
            vehicleId: 'v-001',
            plate: 'ABC-1234',
            previousMileage: 1000,
            mileageValue: body.mileageValue,
            kmTraveled: (body.mileageValue as number) - 1000,
            currentMileage: body.mileageValue,
            recordedBy: body.recordedBy,
            recordedAt: new Date().toISOString(),
            excessiveIncrement: true,
            alertId: null,
          });
        },
      ),
    );

    const user = userEvent.setup();
    render(<UpdateMileagePage />);

    await user.type(screen.getByPlaceholderText('Ej: ABC-1234'), 'ABC-1234');
    await user.type(screen.getByPlaceholderText('0'), '200000');
    await user.type(
      screen.getByPlaceholderText('Nombre del conductor o técnico'),
      'Juan',
    );

    await user.click(
      screen.getByRole('button', { name: /actualizar odómetro/i }),
    );

    await waitFor(() => {
      expect(
        screen.getByText('Incremento inusualmente alto registrado'),
      ).toBeInTheDocument();
    });
  });

  it('muestra toast de error cuando el vehículo no existe (404)', async () => {
    const user = userEvent.setup();
    render(<UpdateMileagePage />);

    await user.type(screen.getByPlaceholderText('Ej: ABC-1234'), 'NOT-FOUND');
    await user.type(screen.getByPlaceholderText('0'), '5000');
    await user.type(
      screen.getByPlaceholderText('Nombre del conductor o técnico'),
      'Juan',
    );

    await user.click(
      screen.getByRole('button', { name: /actualizar odómetro/i }),
    );

    await waitFor(() => {
      expect(
        screen.getByText('Vehículo no encontrado. Verifica la placa.'),
      ).toBeInTheDocument();
    });
  });

  it('muestra el mensaje de error personalizado del backend (ej: kilometraje inválido)', async () => {
    const user = userEvent.setup();
    render(<UpdateMileagePage />);

    await user.type(screen.getByPlaceholderText('Ej: ABC-1234'), 'INV-MILE');
    await user.type(screen.getByPlaceholderText('0'), '5000');
    await user.type(
      screen.getByPlaceholderText('Nombre del conductor o técnico'),
      'Juan',
    );

    await user.click(
      screen.getByRole('button', { name: /actualizar odómetro/i }),
    );

    await waitFor(() => {
      expect(
        screen.getByText('El nuevo kilometraje debe ser mayor al actual'),
      ).toBeInTheDocument();
    });
  });
});