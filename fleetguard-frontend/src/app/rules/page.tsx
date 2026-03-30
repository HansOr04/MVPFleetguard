'use client'

import React, { useState } from 'react';
import { rulesApi } from '@/lib/api';
import { useRules } from '@/hooks/useRules';
import { useAlerts } from '@/hooks/useAlerts';
import { useToast } from '@/hooks/useToast';
import { Toast } from '@/components/ui/Toast';
import { mockVehicleTypes } from '@/lib/mocks/mockVehicleTypes';
import { mockMaintenanceTypes } from '@/lib/mocks/mockMaintenanceTypes';
import { CreateRuleDto } from '@/types';

interface RuleFormData extends CreateRuleDto {
  intervalKm: number;
  warningThresholdKm: number;
}

export default function RulesPage() {
  const { toast, showToast } = useToast();
  const { rules, loading: loadingRules, refetch } = useRules();
  const { alerts } = useAlerts('PENDING');

  const [formData, setFormData] = useState<RuleFormData>({
    name: '',
    maintenanceType: 'PREVENTIVE',
    intervalKm: 0,
    warningThresholdKm: 0,
  });
  const [selectedVehicleTypes, setSelectedVehicleTypes] = useState<string[]>([]);
  const [submitting, setSubmitting] = useState(false);

  const activeRulesCount = rules.filter((r) => r.status === 'ACTIVE').length;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'intervalKm' || name === 'warningThresholdKm' ? Number(value) : value,
    }));
  };

  const handleCheckboxChange = (vehicleTypeId: string) => {
    setSelectedVehicleTypes((prev) =>
      prev.includes(vehicleTypeId)
        ? prev.filter((id) => id !== vehicleTypeId)
        : [...prev, vehicleTypeId]
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || formData.intervalKm <= 0) return;

    setSubmitting(true);
    try {
      const createdRule = await rulesApi.create(formData);

      let associationErrors = 0;
      for (const vehicleTypeId of selectedVehicleTypes) {
        try {
          await rulesApi.associateVehicleType(createdRule.id, { vehicleTypeId });
        } catch {
          associationErrors++;
        }
      }

      if (associationErrors > 0) {
        showToast(
          `Regla creada, pero ${associationErrors} asociación(es) fallaron`,
          'error'
        );
      } else {
        showToast('Regla de mantenimiento creada exitosamente', 'success');
      }

      setFormData({ name: '', maintenanceType: 'PREVENTIVE', intervalKm: 0, warningThresholdKm: 0 });
      setSelectedVehicleTypes([]);
      refetch();
    } catch (error: unknown) {
      const e = error as { message?: string };
      showToast(e.message || 'Error al crear la regla', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h1>Reglas de Mantenimiento</h1>
      <p>Reglas activas: {activeRulesCount} | Próximas alertas: {alerts.length}</p>

      <form onSubmit={handleSubmit}>
        <div>
          <label>Nombre de la regla</label>
          <input
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            placeholder="Ej: Cambio de Aceite"
            type="text"
          />
        </div>
        <div>
          <label>Tipo de mantenimiento</label>
          <select name="maintenanceType" value={formData.maintenanceType} onChange={handleChange} required>
            {mockMaintenanceTypes.map((type) => (
              <option key={type.id} value={type.id}>{type.name}</option>
            ))}
          </select>
        </div>
        <div>
          <label>Intervalo (km)</label>
          <input
            name="intervalKm"
            value={formData.intervalKm || ''}
            onChange={handleChange}
            required
            min={1}
            placeholder="10000"
            type="number"
          />
        </div>
        <div>
          <label>Umbral de aviso (km)</label>
          <input
            name="warningThresholdKm"
            value={formData.warningThresholdKm || ''}
            onChange={handleChange}
            min={0}
            placeholder="500"
            type="number"
          />
        </div>

        <fieldset>
          <legend>Tipos de vehículo a asociar</legend>
          {mockVehicleTypes.map((type) => (
            <label key={type.id}>
              <input
                type="checkbox"
                checked={selectedVehicleTypes.includes(type.id)}
                onChange={() => handleCheckboxChange(type.id)}
              />
              {type.name}
            </label>
          ))}
        </fieldset>

        <button type="submit" disabled={submitting}>
          {submitting ? 'Guardando...' : 'Crear Regla'}
        </button>
      </form>

      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}