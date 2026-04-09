import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, beforeAll, afterEach, afterAll } from 'vitest';
import { http, HttpResponse } from 'msw';
import { server } from '../setup/server';
import ServicesPage from '@/app/services/page';

beforeAll(() => server.listen({ onUnhandledRequest: 'warn' }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

async function searchAlerts(plate: string) {
  const user = userEvent.setup();
  await user.type(screen.getByPlaceholderText('Ej: ABC-1234'), plate);
  await user.click(
    screen.getByRole('button', { name: /consultar alertas/i }),
  );
  return user;
}

describe('Integración: Registro de Servicio de Mantenimiento', () => {
  it('renderiza el formulario correctamente', () => {
    render(<ServicesPage />);
    expect(screen.getByText('Registro de Servicios')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Ej: ABC-1234')).toBeInTheDocument();
  });

  it('el botón de consulta está deshabilitado cuando la placa está vacía', () => {
    render(<ServicesPage />);
    expect(
      screen.getByRole('button', { name: /consultar alertas/i }),
    ).toBeDisabled();
  });

  it('muestra las alertas activas después de buscar por placa', async () => {
    render(<ServicesPage />);
    await searchAlerts('ABC-1234');

    await waitFor(() => {
      expect(
        screen.getByText('Cambio de aceite motor liviano'),
      ).toBeInTheDocument();
      expect(
        screen.getByText('Rotacion de llantas liviano'),
      ).toBeInTheDocument();
    });
  });

  it('muestra mensaje cuando no hay alertas activas', async () => {
    render(<ServicesPage />);
    await searchAlerts('NO-ALERTS');

    await waitFor(() => {
      expect(
        screen.getByText('No hay alertas activas para este vehículo.'),
      ).toBeInTheDocument();
    });
  });

  it('muestra los campos de servicio al seleccionar una alerta', async () => {
    render(<ServicesPage />);
    const user = await searchAlerts('ABC-1234');

    await waitFor(() =>
      screen.getByText('Cambio de aceite motor liviano'),
    );

    await user.click(screen.getByText('Cambio de aceite motor liviano'));

    await waitFor(() => {
      expect(screen.getByText('Datos del Servicio')).toBeInTheDocument();
      expect(screen.getByText('Trazabilidad')).toBeInTheDocument();
    });
  });

  it('registra el servicio correctamente y muestra el toast de éxito', async () => {
    render(<ServicesPage />);
    const user = await searchAlerts('ABC-1234');

    await waitFor(() =>
      screen.getByText('Cambio de aceite motor liviano'),
    );
    await user.click(screen.getByText('Cambio de aceite motor liviano'));
    await waitFor(() =>
      screen.getByPlaceholderText('Nombre del técnico o responsable'),
    );

    await user.type(
      screen.getByPlaceholderText('Nombre del técnico o responsable'),
      'Juan Pérez',
    );

    const dateInput = screen.getByDisplayValue(
      /\d{4}-\d{2}-\d{2}/,
    ) as HTMLInputElement;
    await user.clear(dateInput);
    await user.type(dateInput, '2026-04-08');

    await user.type(screen.getByPlaceholderText('0'), '50000');

    await user.click(
      screen.getByRole('button', { name: /registrar servicio/i }),
    );

    await waitFor(() => {
      expect(
        screen.getByText('Servicio registrado correctamente'),
      ).toBeInTheDocument();
    });
  });

  it('muestra toast de error cuando el servidor responde 404', async () => {
    server.use(
      http.post(
        'http://localhost:3002/api/maintenance/:plate',
        () =>
          HttpResponse.json(
            { message: 'Alerta o vehículo no encontrado' },
            { status: 404 },
          ),
      ),
    );

    render(<ServicesPage />);
    const user = await searchAlerts('ABC-1234');

    await waitFor(() =>
      screen.getByText('Cambio de aceite motor liviano'),
    );
    await user.click(screen.getByText('Cambio de aceite motor liviano'));
    await waitFor(() =>
      screen.getByPlaceholderText('Nombre del técnico o responsable'),
    );

    await user.type(
      screen.getByPlaceholderText('Nombre del técnico o responsable'),
      'Juan',
    );
    const dateInput = screen.getByDisplayValue(/\d{4}-\d{2}-\d{2}/);
    await user.clear(dateInput);
    await user.type(dateInput, '2026-04-08');
    await user.type(screen.getByPlaceholderText('0'), '50000');

    await user.click(
      screen.getByRole('button', { name: /registrar servicio/i }),
    );

    await waitFor(() => {
      expect(
        screen.getByText('Alerta o vehículo no encontrado.'),
      ).toBeInTheDocument();
    });
  });

  it('deselecciona la alerta al hacer clic dos veces', async () => {
    render(<ServicesPage />);
    const user = await searchAlerts('ABC-1234');

    await waitFor(() =>
      screen.getByText('Cambio de aceite motor liviano'),
    );

    await user.click(screen.getByText('Cambio de aceite motor liviano'));
    await waitFor(() => screen.getByText('Datos del Servicio'));

    await user.click(screen.getByText('Cambio de aceite motor liviano'));
    await waitFor(() => {
      expect(
        screen.queryByText('Datos del Servicio'),
      ).not.toBeInTheDocument();
    });
  });
});