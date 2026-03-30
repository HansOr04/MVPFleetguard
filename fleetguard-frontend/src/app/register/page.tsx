'use client'

import React, { useState } from 'react';
import { vehicleApi } from '@/lib/api';
import { useToast } from '@/hooks/useToast';
import { Toast } from '@/components/ui/Toast';
import { CreateVehicleDto } from '@/types';
import { mockVehicleTypes } from '@/lib/mockData';

export default function RegisterVehiclePage() {
  const { toast, showToast } = useToast();
  const [formData, setFormData] = useState<CreateVehicleDto>({
    plate: '',
    vin: '',
    brand: '',
    model: '',
    year: new Date().getFullYear(),
    fuelType: 'Diésel',
    vehicleTypeId: '',
  });
  const [plateError, setPlateError] = useState('');
  const [loading, setLoading] = useState(false);

  const isVinValid = formData.vin.length === 17;
  const isPlateValid = formData.plate.length > 0;
  const isFormValid = isVinValid && isPlateValid && !!formData.brand && !!formData.model && !!formData.year && !!formData.vehicleTypeId;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
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
        year: new Date().getFullYear(),
        fuelType: 'Diésel',
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
    <div>
      <h1>Registrar Nuevo Vehículo</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <input name="plate" value={formData.plate} onChange={handleChange} required placeholder="Placa" />
          {plateError && <p>{plateError}</p>}
        </div>
        <div>
          <input name="vin" value={formData.vin} onChange={handleChange} required maxLength={17} placeholder="VIN (17 caracteres)" />
          {formData.vin.length > 0 && !isVinValid && <p>Debe contener exactamente 17 caracteres.</p>}
        </div>
        <input name="brand" value={formData.brand} onChange={handleChange} required placeholder="Marca" />
        <input name="model" value={formData.model} onChange={handleChange} required placeholder="Modelo" />
        <input name="year" value={formData.year} onChange={handleChange} required type="number" />
        <select name="fuelType" value={formData.fuelType} onChange={handleChange} required>
          <option value="Gasolina">Gasolina</option>
          <option value="Diésel">Diésel</option>
          <option value="Híbrido">Híbrido</option>
          <option value="Eléctrico">Eléctrico</option>
        </select>
        <select name="vehicleTypeId" value={formData.vehicleTypeId} onChange={handleChange} required>
          <option value="">Seleccionar tipo...</option>
          {mockVehicleTypes.map((type) => (
            <option key={type.id} value={type.id}>
              {type.name}
            </option>
          ))}
        </select>
        <button type="submit" disabled={!isFormValid || loading}>
          {loading ? 'Guardando...' : 'Guardar Vehículo'}
        </button>
      </form>
      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}
