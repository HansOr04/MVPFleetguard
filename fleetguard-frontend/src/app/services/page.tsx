'use client'

import React, { useState, useCallback } from 'react';
import { maintenanceApi, alertsApi } from '@/lib/api';
import { useToast } from '@/hooks/useToast';
import { Toast } from '@/components/ui/Toast';
import { CreateMaintenanceDto, MaintenanceAlert } from '@/types';
import { useSearchParams } from 'next/navigation';

export default function ServicesPage() {
  return (
    <React.Suspense fallback={<div className="p-12 pt-24 text-center text-on-surface-variant">Cargando...</div>}>
      <ServicesContent />
    </React.Suspense>
  );
}

function ServicesContent() {
  const { toast, showToast } = useToast();
  const searchParams = useSearchParams();

  const [plate, setPlate] = useState(searchParams.get('plate') || '');
  const [alerts, setAlerts] = useState<MaintenanceAlert[]>([]);
  const [loadingAlerts, setLoadingAlerts] = useState(false);
  const [selectedAlert, setSelectedAlert] = useState<MaintenanceAlert | null>(null);
  const [alertSearchDone, setAlertSearchDone] = useState(false);

  const [formData, setFormData] = useState({
    description: '',
    cost: '',
    provider: '',
    performedAt: new Date().toISOString().split('T')[0],
    mileageAtService: '',
  });

  const [submitting, setSubmitting] = useState(false);

  const isFormValid =
    plate.trim().length > 0 &&
    selectedAlert !== null &&
    formData.performedAt.trim().length > 0 &&
    formData.mileageAtService !== '' &&
    Number(formData.mileageAtService) > 0;

  const handlePlateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPlate(e.target.value.toUpperCase());
    setAlerts([]);
    setSelectedAlert(null);
    setAlertSearchDone(false);
  };

  const handleSearchAlerts = useCallback(async () => {
    if (!plate.trim()) return;
    setLoadingAlerts(true);
    setSelectedAlert(null);
    setAlerts([]);
    try {
      const result = await alertsApi.getByPlate(plate.trim());
      setAlerts(result);
      setAlertSearchDone(true);
    } catch {
      showToast('Error al buscar alertas del vehículo', 'error');
      setAlertSearchDone(true);
    } finally {
      setLoadingAlerts(false);
    }
  }, [plate, showToast]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid || !selectedAlert) return;
    setSubmitting(true);
    try {
      const dto: CreateMaintenanceDto = {
        plate: plate.trim().toUpperCase(),
        alertId: selectedAlert.id,
        serviceType: selectedAlert.ruleName ?? selectedAlert.ruleId,
        description: formData.description.trim() || null,
        cost: formData.cost !== '' ? parseFloat(formData.cost) : null,
        provider: formData.provider.trim() || null,
        performedAt: `${formData.performedAt}T00:00:00`,
        mileageAtService: parseInt(formData.mileageAtService, 10),
      };
      await maintenanceApi.register(dto);
      showToast('Servicio registrado correctamente', 'success');
      setPlate('');
      setAlerts([]);
      setSelectedAlert(null);
      setAlertSearchDone(false);
      setFormData({
        description: '',
        cost: '',
        provider: '',
        performedAt: new Date().toISOString().split('T')[0],
        mileageAtService: '',
      });
    } catch (error: unknown) {
      const e = error as { status?: number; message?: string; errors?: string[] };
      if (e.status === 0) {
        showToast('Sin conexión con el servidor', 'error');
      } else if (e.status === 400 && e.errors) {
        showToast(`Error de validación: ${e.errors.join(', ')}`, 'error');
      } else if (e.status === 404) {
        showToast('Alerta o vehículo no encontrado.', 'error');
      } else {
        showToast(e.message || 'Error al registrar el servicio', 'error');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const statusLabel: Record<string, { label: string; color: string }> = {
    PENDING: { label: 'Pendiente', color: 'text-yellow-600 bg-yellow-50' },
    WARNING: { label: 'Advertencia', color: 'text-orange-600 bg-orange-50' },
    OVERDUE: { label: 'Vencida', color: 'text-error bg-error-container/30' },
  };

  return (
    <div className="min-h-screen">
      <div className="p-12 pt-24 max-w-[1600px] mx-auto">

        <div className="mb-12">
          <h2 className="text-4xl font-extrabold text-primary tracking-tight mb-2">
            Registro de Servicios
          </h2>
          <p className="text-on-surface-variant text-lg max-w-2xl">
            Documenta las intervenciones de mantenimiento realizadas sobre los vehículos de la flota.
          </p>
        </div>

        <div className="grid grid-cols-12 gap-8">
          <div className="col-span-12 lg:col-span-8 bg-surface-container-lowest rounded-xl shadow-sm p-8">
            <form onSubmit={handleSubmit} className="space-y-8">

              <section>
                <div className="flex items-center gap-3 mb-6">
                  <span className="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center text-secondary-container">
                    <span className="material-symbols-outlined text-sm" style={{ fontVariationSettings: "'FILL' 1" }}>badge</span>
                  </span>
                  <h3 className="text-xl font-bold text-primary">Identificación del Vehículo</h3>
                </div>
                <div className="space-y-2">
                  <label className="block text-sm font-semibold text-on-surface-variant px-1">
                    Placa <span className="text-error">*</span>
                  </label>
                  <div className="relative">
                    <span className="absolute left-3 top-1/2 -translate-y-1/2 material-symbols-outlined text-on-surface-variant/50 text-xl pointer-events-none">
                      search
                    </span>
                    <input
                      value={plate}
                      onChange={handlePlateChange}
                      onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); handleSearchAlerts(); } }}
                      required
                      placeholder="Ej: ABC-1234"
                      type="text"
                      className="w-full bg-surface-container-highest border-none rounded-lg py-3 pl-11 pr-4 focus:ring-2 focus:ring-secondary/20 transition-all font-mono tracking-widest text-lg outline-none uppercase"
                    />
                  </div>
                  <div className="flex justify-end pt-2">
                    <button
                      type="button"
                      onClick={handleSearchAlerts}
                      disabled={!plate.trim() || loadingAlerts}
                      className="px-6 py-2.5 rounded-lg bg-secondary text-white font-bold shadow-sm hover:bg-on-secondary-container transition-all flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {loadingAlerts
                        ? <span className="material-symbols-outlined animate-spin text-sm">sync</span>
                        : null}
                      Consultar alertas
                    </button>
                  </div>
                </div>
              </section>

              {alertSearchDone && (
                <section>
                  <div className="flex items-center gap-3 mb-6">
                    <span className="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center text-secondary-container">
                      <span className="material-symbols-outlined text-sm" style={{ fontVariationSettings: "'FILL' 1" }}>notifications_active</span>
                    </span>
                    <h3 className="text-xl font-bold text-primary">Alerta a Resolver</h3>
                  </div>

                  {alerts.length === 0 ? (
                    <div className="bg-surface-container-high rounded-xl p-6 text-center">
                      <span className="material-symbols-outlined text-4xl text-on-surface-variant/40 mb-2 block">
                        check_circle
                      </span>
                      <p className="text-on-surface-variant font-medium">
                        No hay alertas activas para este vehículo.
                      </p>
                    </div>
                  ) : (
                    <div className="space-y-3">
                      {alerts.map((alert) => {
                        const st = statusLabel[alert.status] ?? { label: alert.status, color: 'text-on-surface-variant bg-surface-container' };
                        const isSelected = selectedAlert?.id === alert.id;
                        return (
                          <button
                            key={alert.id}
                            type="button"
                            onClick={() => setSelectedAlert(isSelected ? null : alert)}
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
                                  Vence a los <span className="font-semibold">{alert.dueAtKm.toLocaleString()} km</span>
                                </p>
                              </div>
                              <div className="flex items-center gap-3 shrink-0">
                                <span className={`text-xs font-bold px-3 py-1 rounded-full ${st.color}`}>
                                  {st.label}
                                </span>
                                {isSelected && (
                                  <span className="material-symbols-outlined text-secondary" style={{ fontVariationSettings: "'FILL' 1" }}>
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
              )}

              {selectedAlert && (
                <>
                  <section>
                    <div className="flex items-center gap-3 mb-6">
                      <span className="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center text-secondary-container">
                        <span className="material-symbols-outlined text-sm" style={{ fontVariationSettings: "'FILL' 1" }}>build</span>
                      </span>
                      <h3 className="text-xl font-bold text-primary">Datos del Servicio</h3>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <div className="space-y-2">
                        <label className="block text-sm font-semibold text-on-surface-variant px-1">
                          Fecha del Servicio <span className="text-error">*</span>
                        </label>
                        <input
                          name="performedAt"
                          value={formData.performedAt}
                          onChange={handleChange}
                          required
                          type="date"
                          className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all outline-none"
                        />
                      </div>
                      <div className="space-y-2">
                        <label className="block text-sm font-semibold text-on-surface-variant px-1">
                          Proveedor / Taller
                        </label>
                        <input
                          name="provider"
                          value={formData.provider}
                          onChange={handleChange}
                          placeholder="Taller autorizado"
                          type="text"
                          className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all outline-none"
                        />
                      </div>
                      <div className="space-y-2">
                        <label className="block text-sm font-semibold text-on-surface-variant px-1">
                          Costo (USD)
                        </label>
                        <input
                          name="cost"
                          value={formData.cost}
                          onChange={handleChange}
                          min="0"
                          step="0.01"
                          placeholder="0.00"
                          type="number"
                          onWheel={(e) => e.currentTarget.blur()}
                          className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all outline-none"
                        />
                      </div>
                      <div className="md:col-span-2 space-y-2">
                        <label className="block text-sm font-semibold text-on-surface-variant px-1">
                          Descripción del Trabajo Realizado
                        </label>
                        <textarea
                          name="description"
                          value={formData.description}
                          onChange={handleChange}
                          rows={3}
                          placeholder="Detalle los repuestos utilizados y la mano de obra..."
                          className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all outline-none resize-none"
                        />
                      </div>
                    </div>
                  </section>

                  <section>
                    <div className="flex items-center gap-3 mb-6">
                      <span className="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center text-secondary-container">
                        <span className="material-symbols-outlined text-sm" style={{ fontVariationSettings: "'FILL' 1" }}>speed</span>
                      </span>
                      <h3 className="text-xl font-bold text-primary">Trazabilidad</h3>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <div className="space-y-2">
                        <label className="block text-sm font-semibold text-on-surface-variant px-1">
                          Kilometraje al Momento del Servicio <span className="text-error">*</span>
                        </label>
                        <div className="relative">
                          <input
                            name="mileageAtService"
                            value={formData.mileageAtService}
                            onChange={handleChange}
                            required
                            min="1"
                            placeholder="0"
                            type="number"
                            onWheel={(e) => e.currentTarget.blur()}
                            className="w-full bg-surface-container-highest border-none rounded-lg py-3 pl-4 pr-16 focus:ring-2 focus:ring-secondary/20 transition-all outline-none text-xl font-bold"
                          />
                          <span className="absolute right-4 top-1/2 -translate-y-1/2 text-sm font-bold text-on-surface-variant pointer-events-none">
                            KM
                          </span>
                        </div>
                      </div>
                    </div>
                  </section>

                  <div className="pt-6 flex items-center justify-end border-t border-slate-100">
                    <button
                      type="submit"
                      disabled={!isFormValid || submitting}
                      className="px-10 py-3 rounded-lg bg-secondary text-white font-bold shadow-lg shadow-secondary/20 hover:bg-on-secondary-container transition-all flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {submitting
                        ? <span className="material-symbols-outlined animate-spin text-sm">sync</span>
                        : <span className="material-symbols-outlined text-sm">save</span>}
                      Registrar Servicio
                    </button>
                  </div>
                </>
              )}
            </form>
          </div>

          <div className="col-span-12 lg:col-span-4 space-y-6">
            <div className="bg-surface-container-low rounded-xl p-6 border-l-4 border-secondary">
              <h4 className="font-bold text-primary mb-4 flex items-center gap-2">
                <span className="material-symbols-outlined text-secondary" style={{ fontVariationSettings: "'FILL' 1" }}>info</span>
                Guía de Registro
              </h4>
              <ul className="text-sm text-on-surface-variant leading-relaxed space-y-3">
                <li>• Escribe la <strong>placa exacta</strong> del vehículo en el campo de búsqueda y presiona <strong>Consultar alertas</strong> para ver las alertas de mantenimiento activas asociadas a esa unidad.</li>
                <li>• Selecciona la <strong>alerta que este servicio resuelve</strong>. Cada alerta muestra el tipo de mantenimiento pendiente y el kilometraje límite para realizarlo.</li>
                <li>• Ingresa la <strong>fecha exacta</strong> en que se realizó el servicio. Este dato es obligatorio para mantener la trazabilidad del historial.</li>
                <li>• Registra el <strong>kilometraje actual</strong> del vehículo al momento del servicio. Este valor actualiza el odómetro de la flota.</li>
                <li>• El <strong>proveedor, costo y descripción</strong> son opcionales pero recomendados para un historial completo y auditable.</li>
                <li>• Al guardar, la alerta seleccionada quedará marcada automáticamente como <strong>RESUELTA</strong>.</li>
              </ul>
            </div>

            <div className="bg-primary text-white rounded-xl p-8 shadow-xl relative overflow-hidden">
              <div className="absolute -right-12 -bottom-12 w-48 h-48 bg-secondary/10 rounded-full blur-3xl" />
              <div className="relative z-10">
                <span
                  className="material-symbols-outlined text-4xl text-secondary-container mb-4 block"
                  style={{ fontVariationSettings: "'FILL' 1" }}
                >
                  build_circle
                </span>
                <p className="text-sm font-medium opacity-80 leading-relaxed">
                  Cada registro de servicio cierra el ciclo de una alerta activa, manteniendo el historial de mantenimiento completo y preciso.
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