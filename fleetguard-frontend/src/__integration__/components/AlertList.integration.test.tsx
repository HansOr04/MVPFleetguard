import React from 'react';
import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { AlertList } from '@/components/alerts/AlertList';
import { MaintenanceAlert } from '@/types';

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

const mockAlerts: MaintenanceAlert[] = [
  {
    id: 'a-001',
    vehicleId: 'v-001',
    vehicleTypeId: 'vt-001',
    ruleId: 'r-001',
    ruleName: 'Cambio de aceite motor liviano',
    status: 'PENDING',
    triggeredAt: '2026-01-01T00:00:00Z',
    dueAtKm: 50000,
  },
  {
    id: 'a-002',
    vehicleId: 'v-001',
    vehicleTypeId: 'vt-001',
    ruleId: 'r-002',
    ruleName: 'Rotacion de llantas liviano',
    status: 'WARNING',
    triggeredAt: '2026-01-05T00:00:00Z',
    dueAtKm: 45000,
  },
  {
    id: 'a-003',
    vehicleId: 'v-001',
    vehicleTypeId: 'vt-001',
    ruleId: 'r-003',
    ruleName: 'Revision de frenos',
    status: 'OVERDUE',
    triggeredAt: '2025-12-01T00:00:00Z',
    dueAtKm: 20000,
  },
];

describe('Integración: AlertList + AlertCard', () => {
  it('muestra el conteo plural correcto con múltiples alertas', () => {
    render(<AlertList alerts={mockAlerts} plate="ABC-1234" />);
    expect(
      screen.getByText('3 alertas de mantenimiento activas'),
    ).toBeInTheDocument();
  });

  it('muestra el texto singular con una sola alerta', () => {
    render(<AlertList alerts={[mockAlerts[0]]} plate="ABC-1234" />);
    expect(
      screen.getByText('1 alerta de mantenimiento activa'),
    ).toBeInTheDocument();
  });

  it('traduce el estado PENDING a "Pendiente"', () => {
    render(<AlertList alerts={[mockAlerts[0]]} plate="ABC-1234" />);
    expect(screen.getByText('Pendiente')).toBeInTheDocument();
  });

  it('traduce el estado WARNING a "Advertencia"', () => {
    render(<AlertList alerts={[mockAlerts[1]]} plate="ABC-1234" />);
    expect(screen.getByText('Advertencia')).toBeInTheDocument();
  });

  it('traduce el estado OVERDUE a "Vencida"', () => {
    render(<AlertList alerts={[mockAlerts[2]]} plate="ABC-1234" />);
    expect(screen.getByText('Vencida')).toBeInTheDocument();
  });

  it('muestra el límite de km formateado para cada alerta', () => {
    render(<AlertList alerts={mockAlerts} plate="ABC-1234" />);
    // Cada límite es único en este conjunto de datos → getByText es seguro
    expect(
      screen.getByText((_, el) =>
        el?.textContent?.replace(/\s+/g, ' ').trim() === 'Límite: 50,000 km',
      ),
    ).toBeInTheDocument();
    expect(
      screen.getByText((_, el) =>
        el?.textContent?.replace(/\s+/g, ' ').trim() === 'Límite: 45,000 km',
      ),
    ).toBeInTheDocument();
    expect(
      screen.getByText((_, el) =>
        el?.textContent?.replace(/\s+/g, ' ').trim() === 'Límite: 20,000 km',
      ),
    ).toBeInTheDocument();
  });

  it('muestra el nombre de la regla de cada alerta', () => {
    render(<AlertList alerts={mockAlerts} plate="ABC-1234" />);
    expect(
      screen.getByText('Cambio de aceite motor liviano'),
    ).toBeInTheDocument();
    expect(
      screen.getByText('Rotacion de llantas liviano'),
    ).toBeInTheDocument();
    expect(screen.getByText('Revision de frenos')).toBeInTheDocument();
  });

  it('muestra "—" cuando la alerta no tiene ruleName', () => {
    const alertSinNombre: MaintenanceAlert = {
      ...mockAlerts[0],
      id: 'a-004',
      ruleName: undefined,
    };
    render(<AlertList alerts={[alertSinNombre]} plate="ABC-1234" />);
    expect(screen.getByText('—')).toBeInTheDocument();
  });

  it('todos los links apuntan a /services con la placa correcta', () => {
    render(<AlertList alerts={mockAlerts} plate="GBA-5678" />);
    const links = screen.getAllByRole('link', { name: /registrar servicio/i });
    expect(links).toHaveLength(3);
    links.forEach((link) => {
      expect(link).toHaveAttribute('href', '/services?plate=GBA-5678');
    });
  });
});