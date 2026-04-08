import React from 'react';
import { render, screen, within } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MileageResultPanel } from '@/components/mileage/MileageResultPanel';
import { MileageLog, MaintenanceAlert } from '@/types';

vi.mock('next/link', () => ({
  default: ({
    href,
    children,
    ...props
  }: {
    href: string;
    children: React.ReactNode;
    [key: string]: unknown;
  }) => {
    const React = require('react');
    return React.createElement('a', { href, ...props }, children);
  },
}));

const baseMileageLog: MileageLog = {
  mileageLogId: 'log-001',
  vehicleId: 'v-001',
  plate: 'ABC-1234',
  previousMileage: 40000,
  mileageValue: 45000,
  kmTraveled: 5000,
  currentMileage: 45000,
  recordedBy: 'Juan Pérez',
  recordedAt: '2026-04-08T12:00:00Z',
  excessiveIncrement: false,
  alertId: null,
};

const mockAlerts: MaintenanceAlert[] = [
  {
    id: 'alert-001',
    vehicleId: 'v-001',
    vehicleTypeId: 'vt-001',
    ruleId: 'rule-001',
    ruleName: 'Cambio de aceite motor liviano',
    status: 'PENDING',
    triggeredAt: '2026-01-01T00:00:00Z',
    dueAtKm: 50000,
  },
  {
    id: 'alert-002',
    vehicleId: 'v-001',
    vehicleTypeId: 'vt-001',
    ruleId: 'rule-002',
    ruleName: 'Rotacion de llantas liviano',
    status: 'WARNING',
    triggeredAt: '2026-01-05T00:00:00Z',
    dueAtKm: 45000,
  },
];

describe('Integración: MileageResultPanel', () => {
  it('muestra todos los campos del registro confirmado', () => {
    render(
      <MileageResultPanel
        lastResult={baseMileageLog}
        lastPlate="ABC-1234"
        generatedAlerts={[]}
        loadingAlerts={false}
      />,
    );

    expect(screen.getByText('Registro Confirmado')).toBeInTheDocument();
    expect(screen.getByText('ABC-1234')).toBeInTheDocument();

    // previousMileage (40,000) — único en el DOM
    expect(
      screen.getByText((_, el) =>
        el?.textContent?.replace(/\s+/g, ' ').trim() === '40,000 km',
      ),
    ).toBeInTheDocument();

    // kmTraveled (5,000) — único en el DOM
    expect(
      screen.getByText((_, el) =>
        el?.textContent?.replace(/\s+/g, ' ').trim() === '5,000 km',
      ),
    ).toBeInTheDocument();

    // mileageValue y currentMileage son ambos 45,000 → aparecen dos veces
    const matches45k = screen.getAllByText((_, el) =>
      el?.textContent?.replace(/\s+/g, ' ').trim() === '45,000 km',
    );
    expect(matches45k).toHaveLength(2);

    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
  });

  it('verifica el odómetro actual usando su etiqueta de contexto', () => {
    render(
      <MileageResultPanel
        lastResult={baseMileageLog}
        lastPlate="ABC-1234"
        generatedAlerts={[]}
        loadingAlerts={false}
      />,
    );

    // Encontrar el contenedor que tiene la etiqueta "Odómetro actual"
    const label = screen.getByText('Odómetro actual');
    const row = label.closest('div') as HTMLElement;
    // El valor está en el span hermano dentro de la misma fila
    expect(within(row).getByText((_, el) =>
      el?.textContent?.replace(/\s+/g, ' ').trim() === '45,000 km',
    )).toBeInTheDocument();
  });

  it('NO muestra el banner de advertencia cuando excessiveIncrement=false', () => {
    render(
      <MileageResultPanel
        lastResult={baseMileageLog}
        lastPlate="ABC-1234"
        generatedAlerts={[]}
        loadingAlerts={false}
      />,
    );

    expect(
      screen.queryByText('Incremento inusualmente alto registrado'),
    ).not.toBeInTheDocument();
  });

  it('SÍ muestra el banner de advertencia cuando excessiveIncrement=true', () => {
    render(
      <MileageResultPanel
        lastResult={{ ...baseMileageLog, excessiveIncrement: true }}
        lastPlate="ABC-1234"
        generatedAlerts={[]}
        loadingAlerts={false}
      />,
    );

    expect(
      screen.getByText('Incremento inusualmente alto registrado'),
    ).toBeInTheDocument();
  });

  it('muestra el spinner mientras loadingAlerts=true y no muestra alertas', () => {
    render(
      <MileageResultPanel
        lastResult={baseMileageLog}
        lastPlate="ABC-1234"
        generatedAlerts={mockAlerts}
        loadingAlerts={true}
      />,
    );

    expect(
      screen.getByText('Verificando alertas de mantenimiento...'),
    ).toBeInTheDocument();

    expect(
      screen.queryByText('Cambio de aceite motor liviano'),
    ).not.toBeInTheDocument();
  });

  it('muestra las alertas cuando loadingAlerts=false y hay alertas', () => {
    render(
      <MileageResultPanel
        lastResult={baseMileageLog}
        lastPlate="ABC-1234"
        generatedAlerts={mockAlerts}
        loadingAlerts={false}
      />,
    );

    expect(
      screen.getByText('2 alertas de mantenimiento activas'),
    ).toBeInTheDocument();
    expect(
      screen.getByText('Cambio de aceite motor liviano'),
    ).toBeInTheDocument();
    expect(
      screen.getByText('Rotacion de llantas liviano'),
    ).toBeInTheDocument();
  });

  it('no muestra el AlertList cuando no hay alertas', () => {
    render(
      <MileageResultPanel
        lastResult={baseMileageLog}
        lastPlate="ABC-1234"
        generatedAlerts={[]}
        loadingAlerts={false}
      />,
    );

    // El texto exacto del contador no debe aparecer
    expect(
      screen.queryByText(/\d+ alerta.*mantenimiento/i),
    ).not.toBeInTheDocument();
  });

  it('los links de cada alerta apuntan a la ruta correcta', () => {
    render(
      <MileageResultPanel
        lastResult={baseMileageLog}
        lastPlate="ABC-1234"
        generatedAlerts={mockAlerts}
        loadingAlerts={false}
      />,
    );

    const links = screen.getAllByRole('link', { name: /registrar servicio/i });
    expect(links).toHaveLength(2);
    links.forEach((link) => {
      expect(link).toHaveAttribute('href', '/services?plate=ABC-1234');
    });
  });
});