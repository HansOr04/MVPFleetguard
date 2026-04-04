import React from 'react';
import { MaintenanceAlert } from '@/types';
import { AlertCard } from './AlertCard';

interface AlertListProps {
  alerts: MaintenanceAlert[];
  plate: string;
}

export const AlertList: React.FC<AlertListProps> = ({ alerts, plate }) => {
  return (
    <div className="bg-error-container/20 border-l-4 border-error rounded-xl p-5 space-y-3">
      <div className="flex items-center gap-2">
        <span
          className="material-symbols-outlined text-error"
          style={{ fontVariationSettings: "'FILL' 1" }}
        >
          notification_important
        </span>
        <p className="font-bold text-error text-sm">
          {alerts.length === 1
            ? '1 alerta de mantenimiento activa'
            : `${alerts.length} alertas de mantenimiento activas`}
        </p>
      </div>
      <ul className="space-y-2">
        {alerts.map((alert) => (
          <AlertCard key={alert.id} alert={alert} plate={plate} />
        ))}
      </ul>
    </div>
  );
};