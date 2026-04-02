'use client'

import React, { useState } from 'react';
import { vehicleApi } from '@/lib/api';
import { useToast } from '@/hooks/useToast';
import { Toast } from '@/components/ui/Toast';
import { CreateVehicleDto } from '@/types';
import { mockVehicleTypes } from '@/lib/mocks/mockVehicleTypes';
import { mockFuelTypes } from '@/lib/mocks/mockFuelTypes';

export default function RegisterVehiclePage() {
  const { toast, showToast } = useToast();
  const [formData, setFormData] = useState<CreateVehicleDto>({
    plate: '',
    vin: '',
    brand: '',
    model: '',
    year: '' as unknown as number,
    fuelType: '',
    vehicleTypeId: '',
  });
  const [plateError, setPlateError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const isVinValid = formData.vin.length === 17;
  const isPlateValid = formData.plate.length > 0;
  const isFormValid =
  isVinValid &&
  isPlateValid &&
  !!formData.brand &&
  !!formData.model &&
  !!formData.year &&
  !!formData.vehicleTypeId &&
  !!formData.fuelType;
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    const parsedValue =
      name === 'plate'
        ? value.toUpperCase()
        : name === 'year'
          ? (value === '' ? '' : Number(value))
          : value;

    setFormData({ ...formData, [name]: parsedValue });
    if (e.target.name === 'plate') setPlateError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;
    setLoading(true);
    try {
      await vehicleApi.register(formData);
      showToast('Vehículo registrado correctamente', 'success');
      setFormData({
        plate: '',
        vin: '',
        brand: '',
        model: '',
        year: '' as unknown as number,
        fuelType: '',
        vehicleTypeId: '',
      });
    } catch (error: unknown) {
      const e = error as { status?: number; message?: string; errors?: string[] };
      if (e.status === 409) {
        setPlateError('Esta placa ya está registrada');
        showToast('Error de registro: Placa duplicada', 'error');
      } else if (e.status === 0) {
        showToast('Sin conexión con el servidor', 'error');
      } else if (e.status === 400 && e.errors) {
        showToast(`Error de validación: ${e.errors.join(', ')}`, 'error');
      } else {
        showToast(e.message || 'Error al registrar vehículo', 'error');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen">
      <div className="p-12 pt-24 max-w-[1600px] mx-auto">
        <div className="mb-12">
          <h2 className="text-4xl font-extrabold tracking-tight text-primary mb-2">
            Registrar Nuevo Vehículo
          </h2>
          <p className="text-on-surface-variant text-lg max-w-2xl">
            Completa la información técnica para integrar la nueva unidad al sistema central de monitoreo de flota.
          </p>
        </div>

        <div className="grid grid-cols-12 gap-8">
          <div className="col-span-12 lg:col-span-8 bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden p-8">
            <form onSubmit={handleSubmit} className="space-y-10">

              <section>
                <div className="flex items-center gap-3 mb-6">
                  <span className="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center text-secondary-container">
                    <span className="material-symbols-outlined text-sm" style={{ fontVariationSettings: "'FILL' 1" }}>badge</span>
                  </span>
                  <h3 className="text-xl font-bold text-primary">Identificación del Vehículo</h3>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-6">
                  <div className="space-y-2">
                    <label className="block text-sm font-semibold text-on-surface-variant px-1">Placa <span className="text-error">*</span></label>
                    <div className="relative group">
                      <input
                        name="plate"
                        value={formData.plate}
                        onChange={handleChange}
                        required
                        className={`w-full ${plateError ? 'bg-error-container/30 focus:ring-error/20' : 'bg-surface-container-highest focus:ring-secondary/20'} border-none rounded-lg py-3 px-4 focus:ring-2 transition-all font-mono tracking-widest text-lg outline-none uppercase`}
                        placeholder="Ej: ABC-1234"
                        type="text"
                      />
                      {isPlateValid && !plateError && (
                        <div className="absolute right-3 top-1/2 -translate-y-1/2">
                          <span className="material-symbols-outlined text-secondary" style={{ fontVariationSettings: "'FILL' 1" }}>check_circle</span>
                        </div>
                      )}
                    </div>
                    {plateError ? (
                      <p className="text-[11px] text-error font-medium px-1">{plateError}</p>
                    ) : (
                      <p className="text-[11px] text-secondary font-medium px-1">Formato de placa válido</p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <label className="block text-sm font-semibold text-on-surface-variant px-1">Número de Serie (VIN) <span className="text-error">*</span></label>
                    <div className="relative group">
                      <input
                        name="vin"
                        value={formData.vin}
                        onChange={handleChange}
                        required
                        maxLength={17}
                        className={`w-full ${!isVinValid && formData.vin.length > 0 ? 'bg-error-container/30 focus:ring-error/20' : 'bg-surface-container-highest focus:ring-secondary/20'} border-none rounded-lg py-3 px-4 focus:ring-2 transition-all font-mono uppercase outline-none`}
                        placeholder="17 caracteres requeridos"
                        type="text"
                      />
                      <div className="absolute right-3 top-1/2 -translate-y-1/2">
                        <span className={`text-[11px] font-bold ${!isVinValid ? 'text-error' : 'text-secondary'}`}>
                          {formData.vin.length}/17
                        </span>
                      </div>
                    </div>
                    {!isVinValid && formData.vin.length > 0 && (
                      <p className="text-[11px] text-error font-medium px-1">Debe contener exactamente 17 caracteres alfanuméricos.</p>
                    )}
                  </div>
                </div>
              </section>

              <section>
                <div className="flex items-center gap-3 mb-6">
                  <span className="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center text-secondary-container">
                    <span className="material-symbols-outlined text-sm" style={{ fontVariationSettings: "'FILL' 1" }}>settings</span>
                  </span>
                  <h3 className="text-xl font-bold text-primary">Especificaciones Técnicas</h3>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-x-6 gap-y-6">
                  <div className="md:col-span-2">
                    <div className="grid grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <label className="block text-sm font-semibold text-on-surface-variant px-1">Marca <span className="text-error">*</span></label>
                        <input name="brand" value={formData.brand} onChange={handleChange} required className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all outline-none" placeholder="Ej: Toyota" type="text" />
                      </div>
                      <div className="space-y-2">
                        <label className="block text-sm font-semibold text-on-surface-variant px-1">Modelo <span className="text-error">*</span></label>
                        <input name="model" value={formData.model} onChange={handleChange} required className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all outline-none" placeholder="Ej: Hilux" type="text" />
                      </div>
                    </div>
                  </div>
                  <div className="space-y-2">
                    <label className="block text-sm font-semibold text-on-surface-variant px-1">Año <span className="text-error">*</span></label>
                    <select
                      name="year"
                      value={formData.year}
                      onChange={handleChange}
                      required
                      className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all cursor-pointer outline-none"
                    >
                      <option value="">Seleccionar año...</option>
                      {Array.from({ length: 30 }, (_, i) => {
                        const year = new Date().getFullYear() - i;
                        return (
                          <option key={year} value={year}>
                            {year}
                          </option>
                        );
                      })}
                    </select>
                  </div>
                  <div className="space-y-2">
                    <label className="block text-sm font-semibold text-on-surface-variant px-1">Tipo Combustible <span className="text-error">*</span></label>
                    <select name="fuelType" value={formData.fuelType} onChange={handleChange} required className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all cursor-pointer outline-none">
                      <option value="">Seleccionar tipo...</option>
                      {mockFuelTypes.map((type) => (
                        <option key={type.id} value={type.name}>{type.name}</option>
                      ))}
                    </select>
                  </div>
                  <div className="md:col-span-2 space-y-2">
                    <label className="block text-sm font-semibold text-on-surface-variant px-1">Tipo de Vehículo <span className="text-error">*</span></label>
                    <select name="vehicleTypeId" value={formData.vehicleTypeId} onChange={handleChange} required className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all cursor-pointer outline-none">
                      <option value="">Seleccionar categoría...</option>
                      {mockVehicleTypes.map((type) => (
                        <option key={type.id} value={type.id}>{type.name}</option>
                      ))}
                    </select>
                  </div>
                </div>
              </section>

              <div className="pt-8 flex items-center justify-end gap-4 border-t border-slate-100">
                <button
                  type="submit"
                  disabled={!isFormValid || loading}
                  className="px-10 py-3 rounded-lg bg-secondary text-white font-bold shadow-lg shadow-secondary/20 hover:bg-on-secondary-container transition-all flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading
                    ? <span className="material-symbols-outlined animate-spin text-sm">sync</span>
                    : <span className="material-symbols-outlined text-sm">save</span>}
                  Guardar Vehículo
                </button>
              </div>
            </form>
          </div>

          <div className="col-span-12 lg:col-span-4 space-y-8">
            <div className="bg-primary text-white rounded-2xl p-8 shadow-xl relative overflow-hidden group">
              <div className="absolute -right-20 -bottom-20 w-72 h-72 bg-secondary/10 rounded-full blur-3xl group-hover:bg-secondary/20 transition-colors" />

              <div className="relative z-10 flex flex-col gap-8">

                <div className="flex justify-between items-start">
                  <div className="w-14 h-9 rounded-md bg-white/10 backdrop-blur flex items-center justify-center text-[10px] tracking-widest font-bold">
                    CHIP
                  </div>
                  <span className="material-symbols-outlined text-4xl text-secondary-container">
                    contactless
                  </span>
                </div>

                <div>
                  <p className="text-[10px] uppercase tracking-[0.25em] opacity-40 mb-2">
                    Identificador de Flota
                  </p>
                  <p className="text-3xl font-mono tracking-widest font-extrabold">
                    {formData.plate || '------'}
                  </p>
                </div>

                <div className="flex flex-wrap gap-x-10 gap-y-4 text-sm">

                  <div>
                    <p className="text-[10px] uppercase tracking-wider opacity-40">Vehículo</p>
                    <p className="font-semibold">
                      {(formData.brand || formData.model)
                        ? `${formData.brand || '---'} ${formData.model || '---'}`
                        : '------'}
                    </p>
                  </div>

                  <div>
                    <p className="text-[10px] uppercase tracking-wider opacity-40">Año</p>
                    <p className="font-semibold">{formData.year || '------'}</p>
                  </div>

                  <div>
                    <p className="text-[10px] uppercase tracking-wider opacity-40">Combustible</p>
                    <p className="font-semibold">{formData.fuelType || '---'}</p>
                  </div>

                  <div>
                    <p className="text-[10px] uppercase tracking-wider opacity-40">Tipo</p>
                    <p className="font-semibold">
                      {mockVehicleTypes.find((t) => t.id === formData.vehicleTypeId)?.name || '------'}
                    </p>
                  </div>

                </div>

                <div className="flex justify-between items-end">

                  <div>
                    <p className="text-[10px] uppercase tracking-[0.2em] opacity-40 mb-1">
                      VIN
                    </p>
                    <p className="text-xs font-mono tracking-wider opacity-70">
                      {formData.vin || '-----------------'}
                    </p>
                  </div>

                  <div className="flex items-center gap-2 bg-white/10 px-3 py-1.5 rounded-full backdrop-blur">
                    <span className="w-2 h-2 rounded-full bg-secondary" />
                    <span className="text-xs font-semibold">Pendiente</span>
                  </div>

                </div>

              </div>
            </div>

            <div className="bg-surface-container-low rounded-xl p-6 border-l-4 border-secondary">
              <h4 className="font-bold text-primary mb-3 flex items-center gap-2">
                <span className="material-symbols-outlined text-secondary" style={{ fontVariationSettings: "'FILL' 1" }}>info</span>
                Guía de Registro
              </h4>
              <ul className="text-sm text-on-surface-variant leading-relaxed space-y-3">
                <li>• Asegúrate de que el <strong>VIN</strong> coincida exactamente con la placa física del vehículo.
                Este dato es inmutable una vez registrado y se utiliza para el seguimiento de garantías y seguros.</li>
                <li>• Selecciona el <strong>tipo de vehículo</strong> más adecuado para el registro.</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}