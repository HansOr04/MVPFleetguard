'use client'

import React, { useState } from 'react';
import { vehicleApi, alertsApi } from '@/lib/api';
import { useToast } from '@/hooks/useToast';
import { Toast } from '@/components/ui/Toast';
import { MileageLog, MaintenanceAlert } from '@/types';
import Link from 'next/link';

export default function UpdateMileagePage() {
  const { toast, showToast } = useToast();
  const [plate, setPlate] = useState('');
  const [newMileage, setNewMileage] = useState<number | ''>('');
  const [recordedBy, setRecordedBy] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [lastResult, setLastResult] = useState<MileageLog | null>(null);
  const [generatedAlerts, setGeneratedAlerts] = useState<MaintenanceAlert[]>([]);

  const isNegative = typeof newMileage === 'number' && newMileage < 0;
  const isZero = newMileage === 0;
  const isExcessive = lastResult !== null && lastResult.excessiveIncrement;
  const isFormValid =
    plate.length > 0 &&
    newMileage !== '' &&
    !isNegative &&
    !isZero &&
    recordedBy.trim().length > 0;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;
    setSubmitting(true);
    setGeneratedAlerts([]);
    try {
      const result = await vehicleApi.updateMileage(plate.toUpperCase(), {
        mileageValue: newMileage as number,
        recordedBy: recordedBy,
      });
      setLastResult(result);
      showToast('Odómetro actualizado correctamente', 'success');
      setNewMileage('');
      setRecordedBy('');

      // Consultar alertas activas del vehículo tras actualizar km
      try {
        const alerts = await alertsApi.getByVehicleId(result.vehicleId);
        setGeneratedAlerts(alerts);
      } catch {
        // silencioso — las alertas son informativas, no bloquean el flujo
      }

    } catch (error: unknown) {
      const e = error as { status?: number; message?: string; errors?: string[] };
      if (e.status === 0) {
        showToast('Sin conexión con el servidor', 'error');
      } else if (e.status === 404) {
        showToast('Vehículo no encontrado. Verifica la placa.', 'error');
      } else if (e.status === 400 && e.errors) {
        showToast(`Error de validación: ${e.errors.join(', ')}`, 'error');
      } else {
        showToast(e.message || 'Error al actualizar el kilometraje', 'error');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen">
      <div className="p-12 pt-24 max-w-5xl mx-auto">

        <div className="mb-12">
          <h2 className="text-4xl font-extrabold tracking-tight text-primary mb-2">
            Actualizar Kilometraje
          </h2>
          <p className="text-on-surface-variant font-medium">
            Ingresa la placa del vehículo y el nuevo valor del odómetro para mantener el registro actualizado.
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">

          <div className="bg-surface-container-lowest rounded-xl shadow-sm p-8">
            <form onSubmit={handleSubmit} className="space-y-6">

              <div className="space-y-2">
                <label className="block text-sm font-semibold text-on-surface-variant px-1">
                  Placa del Vehículo
                </label>
                <input
                  value={plate}
                  onChange={(e) => setPlate(e.target.value)}
                  placeholder="Ej: ABC-1234"
                  type="text"
                  required
                  className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all font-mono tracking-widest text-lg outline-none uppercase"
                />
              </div>

              <div className="space-y-2">
                <label className="block text-sm font-semibold text-on-surface-variant px-1">
                  Nuevo Kilometraje
                </label>
                <div className="relative">
                  <input
                    value={newMileage}
                    onChange={(e) =>
                      setNewMileage(e.target.value === '' ? '' : Number(e.target.value))
                    }
                    placeholder="0"
                    type="number"
                    min="1"
                    required
                    onWheel={(e) => e.currentTarget.blur()}
                    className={`w-full border-none rounded-lg py-3 pl-4 pr-4 focus:ring-2 transition-all outline-none text-xl font-bold ${isNegative
                        ? 'bg-error-container/30 focus:ring-error/20'
                        : 'bg-surface-container-highest focus:ring-secondary/20'
                      }`}
                  />
                </div>
                {isNegative && (
                  <p className="text-[11px] text-error font-medium px-1">
                    El kilometraje no puede ser negativo.
                  </p>
                )}
              </div>

              <div className="space-y-2">
                <label className="block text-sm font-semibold text-on-surface-variant px-1">
                  Registrado por
                </label>
                <input
                  value={recordedBy}
                  onChange={(e) => setRecordedBy(e.target.value)}
                  placeholder="Nombre del conductor o técnico"
                  type="text"
                  required
                  className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all outline-none"
                />
              </div>

              <div className="pt-4 flex justify-end border-t border-slate-100">
                <button
                  type="submit"
                  disabled={!isFormValid || submitting}
                  className="px-10 py-3 rounded-lg bg-secondary text-white font-bold shadow-lg shadow-secondary/20 hover:bg-on-secondary-container transition-all flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {submitting ? (
                    <span className="material-symbols-outlined animate-spin text-sm">sync</span>
                  ) : (
                    <span className="material-symbols-outlined text-sm">speed</span>
                  )}
                  Actualizar Odómetro
                </button>
              </div>
            </form>
          </div>

          <div className="space-y-4">

            {lastResult ? (
              <>
                {isExcessive && (
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

                {generatedAlerts.length > 0 && (
                  <div className="bg-error-container/20 border-l-4 border-error rounded-xl p-5 space-y-3">
                    <div className="flex items-center gap-2">
                      <span
                        className="material-symbols-outlined text-error"
                        style={{ fontVariationSettings: "'FILL' 1" }}
                      >
                        notification_important
                      </span>
                      <p className="font-bold text-error text-sm">
                        {generatedAlerts.length === 1
                          ? 'Se generó 1 alerta de mantenimiento'
                          : `Se generaron ${generatedAlerts.length} alertas de mantenimiento`}
                      </p>
                    </div>
                    <ul className="space-y-2">
                      {generatedAlerts.map((alert) => (
                        <li
                          key={alert.id}
                          className="bg-white/60 rounded-lg px-4 py-3 flex flex-col gap-1"
                        >
                          <div className="flex justify-between items-center">
                            <span className="text-xs font-bold text-error uppercase tracking-wider">
                              {alert.status}
                            </span>
                            <span className="text-xs text-on-surface-variant font-medium">
                              Límite: {alert.dueAtKm.toLocaleString()} km
                            </span>
                          </div>
                          <div className="flex items-center justify-between">
                            <p className="text-[11px] font-mono text-on-surface-variant/70 truncate max-w-[180px]">
                              ID: {alert.id}
                            </p>
                            <Link
                              href={`/services?plate=${lastResult.plate}`}
                              className="text-[11px] font-bold text-secondary hover:underline"
                            >
                              Registrar servicio →
                            </Link>
                          </div>
                        </li>
                      ))}
                    </ul>
                  </div>
                )}

                {/* Resultado del registro */}
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
                    <div className="flex justify-between">
                      <span className="text-on-surface-variant font-medium">Kilometraje registrado</span>
                      <span className="font-bold text-primary">{lastResult.mileageValue.toLocaleString()} km</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-on-surface-variant font-medium">Odómetro actual</span>
                      <span className="font-bold text-secondary">{lastResult.currentMileage.toLocaleString()} km</span>
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
              </>
            ) : (
              <div className="bg-surface-container-low rounded-xl p-6 border-l-4 border-secondary">
                <h4 className="font-bold text-primary mb-3 flex items-center gap-2">
                  <span
                    className="material-symbols-outlined text-secondary"
                    style={{ fontVariationSettings: "'FILL' 1" }}
                  >
                    info
                  </span>
                  Guía de Registro
                </h4>
                <ul className="text-sm text-on-surface-variant leading-relaxed space-y-2">
                  <li>• Ingresa la <strong>placa exacta</strong> tal como está registrada en el sistema.</li>
                  <li>• El kilometraje debe ser <strong>mayor a cero</strong>.</li>
                  <li>• El sistema avisará si el incremento es inusualmente alto una vez guardado.</li>
                </ul>
              </div>
            )}

            <div className="bg-primary text-white rounded-xl p-8 shadow-xl relative overflow-hidden">
              <div className="absolute -right-12 -bottom-12 w-48 h-48 bg-secondary/10 rounded-full blur-3xl" />
              <div className="relative z-10">
                <span
                  className="material-symbols-outlined text-4xl text-secondary-container mb-4 block"
                  style={{ fontVariationSettings: "'FILL' 1" }}
                >
                  speed
                </span>
                <p className="text-sm font-medium opacity-80 leading-relaxed">
                  Mantener el odómetro actualizado garantiza que las alertas de mantenimiento preventivo se generen en el momento correcto.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}