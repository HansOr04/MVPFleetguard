import React from 'react';
import { MileageLog, MaintenanceAlert } from '@/types';
import { AlertList } from '@/components/alerts/AlertList';
import { InfoCard } from '@/components/feedback/InfoCard';

interface MileageResultPanelProps {
  lastResult: MileageLog;
  lastPlate: string;
  generatedAlerts: MaintenanceAlert[];
  loadingAlerts: boolean;
}

export const MileageResultPanel: React.FC<MileageResultPanelProps> = ({
  lastResult,
  lastPlate,
  generatedAlerts,
  loadingAlerts,
}) => {
  const previousMileage = lastResult.previousMileage;
  const kmTraveled = lastResult.kmTraveled;

  return (
    <div className="space-y-4">
      {lastResult.excessiveIncrement && (
        <div className="bg-[#FEF9C3] border-l-4 border-[#D69E2E] p-5 rounded-xl flex items-start gap-4">
          <span
            className="material-symbols-outlined text-[#D69E2E] mt-0.5"
            style={{ fontVariationSettings: "'FILL' 1" }}
          >
            warning
          </span>
          <div>
            <p className="font-bold text-[#856404] text-sm">
              Incremento inusualmente alto registrado
            </p>
            <p className="text-[#856404] text-xs mt-1 opacity-80">
              El sistema detectó un incremento elevado. El registro fue guardado. Se recomienda verificar el odómetro físico del vehículo.
            </p>
          </div>
        </div>
      )}

      {loadingAlerts && (
        <div className="bg-surface-container-low rounded-xl p-4 flex items-center gap-3">
          <span className="material-symbols-outlined animate-spin text-secondary text-sm">sync</span>
          <p className="text-sm text-on-surface-variant font-medium">
            Verificando alertas de mantenimiento...
          </p>
        </div>
      )}

      {!loadingAlerts && generatedAlerts.length > 0 && (
        <AlertList alerts={generatedAlerts} plate={lastPlate} />
      )}

      <div className="bg-surface-container-lowest rounded-xl shadow-sm p-8 border-l-4 border-secondary">
        <div className="flex items-center gap-2 mb-6">
          <span
            className="material-symbols-outlined text-secondary"
            style={{ fontVariationSettings: "'FILL' 1" }}
          >
            check_circle
          </span>
          <h3 className="text-lg font-bold text-primary">Registro Confirmado</h3>
        </div>
        <div className="space-y-4 text-sm">
          <div className="flex justify-between">
            <span className="text-on-surface-variant font-medium">Placa</span>
            <span className="font-bold text-primary font-mono">{lastResult.plate}</span>
          </div>
          <div className="flex justify-between border-b border-slate-100 pb-4">
            <span className="text-on-surface-variant font-medium">Odómetro anterior</span>
            <span className="font-bold text-on-surface">{previousMileage.toLocaleString()} km</span>
          </div>
          <div className="flex justify-between">
            <span className="text-on-surface-variant font-medium">Km registrados</span>
            <span className="font-bold text-on-surface">{lastResult.mileageValue.toLocaleString()} km</span>
          </div>
          <div className="flex justify-between">
            <span className="text-on-surface-variant font-medium">Km recorridos</span>
            <span className="font-bold text-on-surface">{kmTraveled.toLocaleString()} km</span>
          </div>
          <div className="flex justify-between border-t border-slate-100 pt-4">
            <span className="text-on-surface-variant font-medium">Odómetro actual</span>
            <span className="font-bold text-secondary text-base">{lastResult.currentMileage.toLocaleString()} km</span>
          </div>
          <div className="flex justify-between">
            <span className="text-on-surface-variant font-medium">Registrado por</span>
            <span className="font-bold text-on-surface">{lastResult.recordedBy}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-on-surface-variant font-medium">Fecha</span>
            <span className="font-medium text-on-surface">
              {new Date(lastResult.recordedAt).toLocaleString('es-CO')}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};