import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, beforeAll, afterEach, afterAll } from 'vitest';
import { http, HttpResponse } from 'msw';
import { server } from '../setup/server';
import RulesPage from '@/app/rules/page';

beforeAll(() => server.listen({ onUnhandledRequest: 'warn' }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('Integración: Creación de Regla de Mantenimiento', () => {
  it('renderiza el formulario de reglas correctamente', () => {
    render(<RulesPage />);
    expect(screen.getByText('Reglas de Mantenimiento')).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText('Escribe para buscar una regla...'),
    ).toBeInTheDocument();
  });

  it('el botón Crear Regla está deshabilitado si el formulario está incompleto', () => {
    render(<RulesPage />);
    expect(
      screen.getByRole('button', { name: /crear regla/i }),
    ).toBeDisabled();
  });

  it('muestra sugerencias al escribir en el campo de nombre', async () => {
    const user = userEvent.setup();
    render(<RulesPage />);

    await user.type(
      screen.getByPlaceholderText('Escribe para buscar una regla...'),
      'aceite',
    );

    await waitFor(() => {
      expect(
        screen.getByText('Cambio de aceite motor liviano'),
      ).toBeInTheDocument();
    });
  });

  it('rellena el formulario al seleccionar una sugerencia', async () => {
    const user = userEvent.setup();
    render(<RulesPage />);

    await user.type(
      screen.getByPlaceholderText('Escribe para buscar una regla...'),
      'aceite',
    );
    await waitFor(() =>
      screen.getByText('Cambio de aceite motor liviano'),
    );

    await user.click(screen.getByText('Cambio de aceite motor liviano'));

    await waitFor(() => {
      const nameInput = screen.getByPlaceholderText(
        'Escribe para buscar una regla...',
      ) as HTMLInputElement;
      expect(nameInput.value).toBe('Cambio de aceite motor liviano');

      const intervalInput = screen.getByPlaceholderText(
        'Ej: 10000',
      ) as HTMLInputElement;
      expect(intervalInput.value).toBe('5000');
    });
  });

  it('muestra mensaje de error si el nombre no coincide con ninguna regla', async () => {
    const user = userEvent.setup();
    render(<RulesPage />);

    await user.type(
      screen.getByPlaceholderText('Escribe para buscar una regla...'),
      'Regla inventada que no existe',
    );

    await waitFor(() => {
      expect(
        screen.getByText('Selecciona una opción válida de la lista.'),
      ).toBeInTheDocument();
    });
  });

  it('crea la regla y asocia tipos de vehículo correctamente', async () => {
    const user = userEvent.setup();
    render(<RulesPage />);

    await user.type(
      screen.getByPlaceholderText('Escribe para buscar una regla...'),
      'aceite',
    );
    await waitFor(() =>
      screen.getByText('Cambio de aceite motor liviano'),
    );
    await user.click(screen.getByText('Cambio de aceite motor liviano'));

    await userEvent.selectOptions(
      screen.getByDisplayValue('Seleccionar tipo...'),
      'PREVENTIVE',
    );

    await user.click(screen.getByRole('button', { name: 'Sedán' }));

    await user.click(screen.getByRole('button', { name: /crear regla/i }));

    await waitFor(() => {
      expect(
        screen.getByText('Regla de mantenimiento creada exitosamente'),
      ).toBeInTheDocument();
    });
  });

  it('muestra toast de error parcial cuando la asociación falla', async () => {
    server.use(
      http.post(
        'http://localhost:3002/api/maintenance-rules',
        async () =>
          HttpResponse.json(
            {
              id: 'rule-fail-assoc',
              name: 'Cambio de aceite motor liviano',
              maintenanceType: 'PREVENTIVE',
              intervalKm: 5000,
              warningThresholdKm: 500,
              status: 'ACTIVE',
              createdAt: new Date().toISOString(),
              updatedAt: new Date().toISOString(),
            },
            { status: 201 },
          ),
      ),
    );

    const user = userEvent.setup();
    render(<RulesPage />);

    await user.type(
      screen.getByPlaceholderText('Escribe para buscar una regla...'),
      'aceite',
    );
    await waitFor(() =>
      screen.getByText('Cambio de aceite motor liviano'),
    );
    await user.click(screen.getByText('Cambio de aceite motor liviano'));

    await userEvent.selectOptions(
      screen.getByDisplayValue('Seleccionar tipo...'),
      'PREVENTIVE',
    );

    await user.click(screen.getByRole('button', { name: 'Sedán' }));
    await user.click(screen.getByRole('button', { name: /crear regla/i }));

    await waitFor(() => {
      expect(
        screen.getByText(/asociación\(es\) fallaron/i),
      ).toBeInTheDocument();
    });
  });

  it('limpia el formulario después de una creación exitosa', async () => {
    const user = userEvent.setup();
    render(<RulesPage />);

    await user.type(
      screen.getByPlaceholderText('Escribe para buscar una regla...'),
      'aceite',
    );
    await waitFor(() =>
      screen.getByText('Cambio de aceite motor liviano'),
    );
    await user.click(screen.getByText('Cambio de aceite motor liviano'));

    await userEvent.selectOptions(
      screen.getByDisplayValue('Seleccionar tipo...'),
      'PREVENTIVE',
    );

    await user.click(screen.getByRole('button', { name: 'Sedán' }));
    await user.click(screen.getByRole('button', { name: /crear regla/i }));

    await waitFor(() =>
      screen.getByText('Regla de mantenimiento creada exitosamente'),
    );

    const nameInput = screen.getByPlaceholderText(
      'Escribe para buscar una regla...',
    ) as HTMLInputElement;
    expect(nameInput.value).toBe('');
  });
});