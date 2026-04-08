import React from 'react';
import { MaintenanceAlert } from '@/types';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { EmptyState } from '@/components/feedback/EmptyState';

interface AlertSelectorSectionProps {
  alerts: MaintenanceAlert[];
  selectedAlert: MaintenanceAlert | null;
  onSelectAlert: (alert: MaintenanceAlert) => void;
}

const statusLabel: Record<string, { label: string; color: string }> = {
  PENDING: { label: 'Pendiente', color: 'text-yellow-600 bg-yellow-50' },
  WARNING: { label: 'Advertencia', color: 'text-orange-600 bg-orange-50' },
  OVERDUE: { label: 'Vencida', color: 'text-error bg-error-container/30' },
};

export const AlertSelectorSection: React.FC<AlertSelectorSectionProps> = ({
  alerts,
  selectedAlert,
  onSelectAlert,
}) => {
  return (
    <section>
      <SectionHeader icon="notifications_active" title="Alerta a Resolver" />

      {alerts.length === 0 ? (
        <EmptyState
          icon="check_circle"
          title="No hay alertas activas para este vehículo."
        />
      ) : (
        <div className="space-y-3">
          {alerts.map((alert) => {
            const st = statusLabel[alert.status] ?? {
              label: alert.status,
              color: 'text-on-surface-variant bg-surface-container',
            };
            const isSelected = selectedAlert?.id === alert.id;

            return (
              <button
                key={alert.id}
                type="button"
                onClick={() => onSelectAlert(alert)}
                className={`w-full text-left rounded-xl p-4 border-2 transition-all ${
                  isSelected
                    ? 'border-secondary bg-secondary/5'
                    : 'border-transparent bg-surface-container-high hover:border-secondary/30'
                }`}
              >
                <div className="flex items-center justify-between gap-4">
                  <div className="flex-1">
                    <p className="font-bold text-on-surface">
                      {alert.ruleName ?? 'Alerta de mantenimiento'}
                    </p>
                    <p className="text-sm text-on-surface-variant mt-1">
                      Vence a los{' '}
                      <span className="font-semibold">
                        {alert.dueAtKm.toLocaleString()} km
                      </span>
                    </p>
                  </div>
                  <div className="flex items-center gap-3 shrink-0">
                    <span className={`text-xs font-bold px-3 py-1 rounded-full ${st.color}`}>
                      {st.label}
                    </span>
                    {isSelected && (
                      <span
                        className="material-symbols-outlined text-secondary"
                        style={{ fontVariationSettings: "'FILL' 1" }}
                      >
                        check_circle
                      </span>
                    )}
                  </div>
                </div>
              </button>
            );
          })}
        </div>
      )}
    </section>
  );
};