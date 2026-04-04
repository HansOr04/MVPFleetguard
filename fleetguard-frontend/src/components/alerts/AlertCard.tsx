import React from 'react';
import Link from 'next/link';
import { MaintenanceAlert } from '@/types';

interface AlertCardProps {
  alert: MaintenanceAlert;
  plate: string;
}

const statusConfig: Record<string, { label: string; color: string }> = {
  PENDING: { label: 'Pendiente', color: 'text-yellow-700' },
  WARNING: { label: 'Advertencia', color: 'text-orange-600' },
  OVERDUE: { label: 'Vencida', color: 'text-error' },
};

export const AlertCard: React.FC<AlertCardProps> = ({ alert, plate }) => {
  const status = statusConfig[alert.status] ?? { label: alert.status, color: 'text-on-surface-variant' };

  return (
    <li className="bg-white/60 rounded-lg px-4 py-3 flex flex-col gap-1">
      <div className="flex justify-between items-center">
        <span className={`text-xs font-bold uppercase tracking-wider ${status.color}`}>
          {status.label}
        </span>
        <span className="text-xs text-on-surface-variant font-medium">
          Límite: {alert.dueAtKm.toLocaleString()} km
        </span>
      </div>
      <div className="flex items-center justify-between">
        <span className="text-xs font-medium text-on-surface-variant">
          {alert.ruleName ?? '—'}
        </span>
        <Link
          href={`/services?plate=${plate}`}
          className="text-[11px] font-bold text-secondary hover:underline"
        >
          Registrar servicio →
        </Link>
      </div>
    </li>
  );
};