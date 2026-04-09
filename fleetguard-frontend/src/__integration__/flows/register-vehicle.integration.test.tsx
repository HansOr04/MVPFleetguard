import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, beforeAll, afterEach, afterAll } from 'vitest';
import { server } from '../setup/server';
import RegisterVehiclePage from '@/app/register/page';

beforeAll(() => server.listen({ onUnhandledRequest: 'warn' }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

async function fillAndSubmitForm(overrides: Record<string, string> = {}) {
  const user = userEvent.setup();

  const fields: Record<string, string> = {
    plate: 'XYZ-9999',
    vin: '1HGBH41JXMN109200',
    brand: 'Honda',
    model: 'Civic',
    ...overrides,
  };

  await user.type(screen.getByPlaceholderText('Ej: ABC-1234'), fields.plate);
  await user.type(
    screen.getByPlaceholderText('17 caracteres requeridos'),
    fields.vin,
  );
  await user.type(screen.getByPlaceholderText('Ej: Toyota'), fields.brand);
  await user.type(screen.getByPlaceholderText('Ej: Hilux'), fields.model);

  fireEvent.change(screen.getByDisplayValue('Seleccionar año...'), {
    target: { value: '2023' },
  });
  fireEvent.change(screen.getByDisplayValue('Seleccionar tipo...'), {
    target: { value: 'Gasolina' },
  });
  fireEvent.change(screen.getByDisplayValue('Seleccionar categoría...'), {
    target: { value: 'c1a1d13e-b3df-4fab-9584-890b852d5311' },
  });

  await user.click(screen.getByRole('button', { name: /guardar vehículo/i }));
}

describe('Integración: Registro de Vehículo', () => {
  it('renderiza el formulario correctamente', () => {
    render(<RegisterVehiclePage />);
    expect(screen.getByText('Registrar Nuevo Vehículo')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Ej: ABC-1234')).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText('17 caracteres requeridos'),
    ).toBeInTheDocument();
  });

  it('el botón Guardar está deshabilitado si el formulario está incompleto', () => {
    render(<RegisterVehiclePage />);
    expect(
      screen.getByRole('button', { name: /guardar vehículo/i }),
    ).toBeDisabled();
  });

  it('muestra el VIN actualizado en la tarjeta de vista previa mientras se escribe', async () => {
    const user = userEvent.setup();
    render(<RegisterVehiclePage />);
    await user.type(
      screen.getByPlaceholderText('17 caracteres requeridos'),
      '1HGBH41JXMN109200',
    );
    expect(screen.getByText('1HGBH41JXMN109200')).toBeInTheDocument();
  });

  it('muestra error de placa duplicada (409 field=plate)', async () => {
    render(<RegisterVehiclePage />);
    await fillAndSubmitForm({ plate: 'DUP-PLATE' });

    await waitFor(() => {
      expect(
        screen.getByText('Esta placa ya está registrada en el sistema'),
      ).toBeInTheDocument();
    });
  });

  it('muestra error de VIN duplicado (409 field=vin)', async () => {
    render(<RegisterVehiclePage />);
    await fillAndSubmitForm({ vin: '00000000000000DUP' });

    await waitFor(() => {
      expect(
        screen.getByText('Este VIN ya está registrado en el sistema'),
      ).toBeInTheDocument();
    });
  });

  it('muestra toast de éxito y limpia el formulario tras registrar correctamente', async () => {
    render(<RegisterVehiclePage />);
    await fillAndSubmitForm();

    await waitFor(() => {
      expect(
        screen.getByText('Vehículo registrado correctamente'),
      ).toBeInTheDocument();
    });

    await waitFor(() => {
      const plateInput = screen.getByPlaceholderText(
        'Ej: ABC-1234',
      ) as HTMLInputElement;
      expect(plateInput.value).toBe('');
    });
  });
});