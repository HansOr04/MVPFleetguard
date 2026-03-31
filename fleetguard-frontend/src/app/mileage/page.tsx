'use client'

import React, { useState } from 'react';
import { vehicleApi } from '@/lib/api';
import { useToast } from '@/hooks/useToast';
import { Toast } from '@/components/ui/Toast';
import { MileageLog } from '@/types';

export default function UpdateMileagePage() {
  const { toast, showToast } = useToast();
  const [plate, setPlate] = useState('');
  const [newMileage, setNewMileage] = useState<number | ''>('');
  const [recordedBy, setRecordedBy] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [lastResult, setLastResult] = useState<MileageLog | null>(null);

  const isNegative = typeof newMileage === 'number' && newMileage < 0;
  const isZero = newMileage === 0;
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
    try {
      const result = await vehicleApi.updateMileage(plate.toUpperCase(), {
        mileageValue: newMileage as number,
        recordedBy: recordedBy,
      });
      setLastResult(result);
      showToast('Odómetro actualizado correctamente', 'success');
      setNewMileage('');
      setRecordedBy('');
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
      <div className="p-12 pt-24">
        <h1 className="text-4xl font-bold mb-4">Actualizar Kilometraje</h1>
        <p className="text-gray-600 mb-8">
          Ingresa la placa del vehículo y el nuevo valor del odómetro.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4 max-w-md">
          <div>
            <label>Placa del Vehículo</label>
            <input
              value={plate}
              onChange={(e) => setPlate(e.target.value)}
              placeholder="Ej: ABC-1234"
              type="text"
              required
            />
          </div>

          <div>
            <label>Nuevo Kilometraje</label>
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
            />
            {isNegative && (
              <p style={{ color: 'red' }}>El kilometraje no puede ser negativo.</p>
            )}
          </div>

          <div>
            <label>Registrado por</label>
            <input
              value={recordedBy}
              onChange={(e) => setRecordedBy(e.target.value)}
              placeholder="Nombre del conductor o técnico"
              type="text"
              required
            />
          </div>

          <button type="submit" disabled={!isFormValid || submitting}>
            {submitting ? 'Guardando...' : 'Actualizar Odómetro'}
          </button>
        </form>

        {lastResult && (
          <div style={{ marginTop: '2rem' }}>
            {lastResult.excessiveIncrement && (
              <p style={{ color: 'orange' }}>
                Advertencia: Incremento inusualmente alto registrado. Verifica el odómetro físico.
              </p>
            )}
            <p><strong>Placa:</strong> {lastResult.plate}</p>
            <p><strong>Kilometraje registrado:</strong> {lastResult.mileageValue.toLocaleString()} km</p>
            <p><strong>Odómetro actual:</strong> {lastResult.currentMileage.toLocaleString()} km</p>
            <p><strong>Registrado por:</strong> {lastResult.recordedBy}</p>
            <p><strong>Fecha:</strong> {new Date(lastResult.recordedAt).toLocaleString('es-CO')}</p>
          </div>
        )}
      </div>
      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}
