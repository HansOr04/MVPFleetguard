'use client'

import React, { useState } from 'react';
import { maintenanceApi } from '@/lib/api';
import { useToast } from '@/hooks/useToast';
import { Toast } from '@/components/ui/Toast';
import { CreateMaintenanceDto } from '@/types';
import { useSearchParams } from 'next/navigation';
import { mockServiceTypes } from '@/lib/mocks/mockServiceTypes';

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

  const [formData, setFormData] = useState({
    plate: searchParams.get('plate') || '',
    alertId: '',
    ruleId: '',
    serviceType: '',
    description: '',
    cost: '',
    provider: '',
    performedAt: new Date().toISOString().split('T')[0],
    mileageAtService: '',
  });

  const [submitting, setSubmitting] = useState(false);
  const [lastRecord, setLastRecord] = useState<{
    plate: string;
    serviceType: string;
    performedAt: string;
    provider: string | null;
    cost: number | null;
    mileageAtService: number;
  } | null>(null);

  const isFormValid =
    formData.plate.trim().length > 0 &&
    formData.serviceType.trim().length > 0 &&
    formData.mileageAtService !== '' &&
    Number(formData.mileageAtService) > 0;

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;
    setSubmitting(true);
    try {
      const dto: CreateMaintenanceDto = {
        plate: formData.plate.trim().toUpperCase(),
        alertId: formData.alertId.trim() || null,
        ruleId: formData.ruleId.trim() || null,
        serviceType: formData.serviceType.trim(),
        description: formData.description.trim() || null,
        cost: formData.cost !== '' ? parseFloat(formData.cost) : null,
        provider: formData.provider.trim() || null,
        performedAt: formData.performedAt
          ? `${formData.performedAt}T00:00:00`
          : null,
        mileageAtService: parseInt(formData.mileageAtService, 10),
      };
      await maintenanceApi.register(dto);
      showToast('Servicio registrado correctamente', 'success');
      setLastRecord({
        plate: dto.plate,
        serviceType: dto.serviceType,
        performedAt: dto.performedAt ?? new Date().toISOString(),
        provider: dto.provider,
        cost: dto.cost,
        mileageAtService: dto.mileageAtService,
      });
      setFormData((prev) => ({
        ...prev,
        alertId: '',
        ruleId: '',
        serviceType: '',
        description: '',
        cost: '',
        provider: '',
        performedAt: new Date().toISOString().split('T')[0],
        mileageAtService: '',
      }));
    } catch (error: unknown) {
      const e = error as { status?: number; message?: string; errors?: string[] };
      if (e.status === 0) {
        showToast('Sin conexión con el servidor', 'error');
      } else if (e.status === 400 && e.errors) {
        showToast(`Error de validación: ${e.errors.join(', ')}`, 'error');
      } else if (e.status === 404) {
        showToast('Vehículo no encontrado. Verifica la placa.', 'error');
      } else {
        showToast(e.message || 'Error al registrar el servicio', 'error');
      }
    } finally {
      setSubmitting(false);
    }
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

              {/* Identificación */}
              <section>
                <div className="flex items-center gap-3 mb-6">
                  <span className="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center text-secondary-container">
                    <span className="material-symbols-outlined text-sm" style={{ fontVariationSettings: "'FILL' 1" }}>badge</span>
                  </span>
                  <h3 className="text-xl font-bold text-primary">Identificación del Vehículo</h3>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-2">
                    <label className="block text-sm font-semibold text-on-surface-variant px-1">
                      Placa <span className="text-error">*</span>
                    </label>
                    <input
                      name="plate"
                      value={formData.plate}
                      onChange={handleChange}
                      required
                      placeholder="Ej: ABC-1234"
                      type="text"
                      className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all font-mono tracking-widest text-lg outline-none uppercase"
                    />
                  </div>
                  <div className="space-y-2">
                    <label className="block text-sm font-semibold text-on-surface-variant px-1">
                      ID de Alerta <span className="text-on-surface-variant/40 font-normal">(opcional)</span>
                    </label>
                    <input
                      name="alertId"
                      value={formData.alertId}
                      onChange={handleChange}
                      placeholder="UUID de la alerta generada"
                      type="text"
                      className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all font-mono text-sm outline-none"
                    />
                    <p className="text-[11px] text-on-surface-variant/60 px-1">
                      Si este servicio resuelve una alerta activa, ingresa su ID.
                    </p>
                  </div>
                </div>
              </section>

              {/* Datos del servicio */}
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
                      Tipo de Servicio <span className="text-error">*</span>
                    </label>
                    <select
                      name="serviceType"
                      value={formData.serviceType}
                      onChange={handleChange}
                      required
                      className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all outline-none cursor-pointer"
                    >
                      <option value="">Seleccionar tipo...</option>
                      {mockServiceTypes.map((type) => (
                        <option key={type.id} value={type.name}>{type.name}</option>
                      ))}
                    </select>
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
                      Fecha del Servicio
                    </label>
                    <input
                      name="performedAt"
                      value={formData.performedAt}
                      onChange={handleChange}
                      type="date"
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

              {/* Trazabilidad */}
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
                  <div className="space-y-2">
                    <label className="block text-sm font-semibold text-on-surface-variant px-1">
                      ID de Regla <span className="text-on-surface-variant/40 font-normal">(opcional)</span>
                    </label>
                    <input
                      name="ruleId"
                      value={formData.ruleId}
                      onChange={handleChange}
                      placeholder="UUID de la regla asociada"
                      type="text"
                      className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all font-mono text-sm outline-none"
                    />
                    <p className="text-[11px] text-on-surface-variant/60 px-1">
                      Disponible en el listado de reglas de mantenimiento.
                    </p>
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
            </form>
          </div>

          {/* Panel lateral */}
          <div className="col-span-12 lg:col-span-4 space-y-6">

            {lastRecord ? (
              <div className="bg-surface-container-lowest rounded-xl shadow-sm p-6 border-l-4 border-secondary">
                <div className="flex items-center gap-2 mb-5">
                  <span className="material-symbols-outlined text-secondary" style={{ fontVariationSettings: "'FILL' 1" }}>
                    check_circle
                  </span>
                  <h3 className="text-base font-bold text-primary">Último Registro</h3>
                </div>
                <div className="space-y-3 text-sm">
                  <div className="flex justify-between">
                    <span className="text-on-surface-variant font-medium">Placa</span>
                    <span className="font-bold text-primary font-mono">{lastRecord.plate}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-on-surface-variant font-medium">Servicio</span>
                    <span className="font-bold text-on-surface">{lastRecord.serviceType}</span>
                  </div>
                  {lastRecord.provider && (
                    <div className="flex justify-between">
                      <span className="text-on-surface-variant font-medium">Taller</span>
                      <span className="font-medium text-on-surface">{lastRecord.provider}</span>
                    </div>
                  )}
                  {lastRecord.cost !== null && (
                    <div className="flex justify-between">
                      <span className="text-on-surface-variant font-medium">Costo</span>
                      <span className="font-bold text-on-surface">${lastRecord.cost.toLocaleString()}</span>
                    </div>
                  )}
                  <div className="flex justify-between">
                    <span className="text-on-surface-variant font-medium">Odómetro</span>
                    <span className="font-bold text-secondary">{lastRecord.mileageAtService.toLocaleString()} km</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-on-surface-variant font-medium">Fecha</span>
                    <span className="font-medium text-on-surface">
                      {new Date(lastRecord.performedAt).toLocaleDateString('es-CO')}
                    </span>
                  </div>
                </div>
              </div>
            ) : (
              <div className="bg-surface-container-low rounded-xl p-6 border-l-4 border-secondary">
                <h4 className="font-bold text-primary mb-4 flex items-center gap-2">
                  <span className="material-symbols-outlined text-secondary" style={{ fontVariationSettings: "'FILL' 1" }}>info</span>
                  Guía de Registro
                </h4>
                <ul className="text-sm text-on-surface-variant leading-relaxed space-y-3">
                  <li><span>• Ingresa la <strong>placa exacta</strong> del vehículo al que se le realizó el servicio.</span></li>
                  <li><span>• El <strong>tipo de servicio</strong> y el <strong>kilometraje</strong> son obligatorios.</span></li>
                  <li><span>• El <strong>ID de alerta</strong> se obtiene cuando el sistema genera una alerta al actualizar el kilometraje.</span></li>
                  <li><span>• El <strong>ID de regla</strong> está disponible en el módulo de Reglas de Mantenimiento.</span></li>
                  <li><span>• Costo, proveedor y descripción son opcionales pero mejoran la trazabilidad.</span></li>
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
                  design_services
                </span>
                <p className="text-sm font-medium opacity-80 leading-relaxed">
                  Documentar cada intervención garantiza la trazabilidad completa del historial de mantenimiento de tu flota.
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
