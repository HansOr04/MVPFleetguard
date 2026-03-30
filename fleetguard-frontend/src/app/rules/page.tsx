'use client'

import React, { useState } from 'react';
import { rulesApi } from '@/lib/api';
import { useRules } from '@/hooks/useRules';
import { useAlerts } from '@/hooks/useAlerts';
import { useToast } from '@/hooks/useToast';
import { Toast } from '@/components/ui/Toast';
import { CreateRuleDto } from '@/types';
import { mockMaintenanceTypes } from '@/lib/mocks/mockMaintenanceTypes';

export default function RulesPage() {
  const { toast, showToast } = useToast();
  const { rules, loading: loadingRules, refetch } = useRules();
  const { alerts } = useAlerts('PENDING');

  const [formData, setFormData] = useState<CreateRuleDto>({
    name: '',
    maintenanceType: 'PREVENTIVE',
    intervalKm: 0,
    warningThresholdKm: 0,
  });
  const [submitting, setSubmitting] = useState(false);

  const activeRulesCount = rules.filter((r) => r.status === 'ACTIVE').length;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'intervalKm' || name === 'warningThresholdKm' ? Number(value) : value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await rulesApi.create(formData);
      showToast('Regla de mantenimiento creada exitosamente', 'success');
      setFormData({ name: '', maintenanceType: 'PREVENTIVE', intervalKm: 0, warningThresholdKm: 0 });
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
      <p>Reglas activas: {activeRulesCount}</p>
      <p>Alertas pendientes: {alerts.length}</p>

      <form onSubmit={handleSubmit}>
        <input
          required
          name="name"
          value={formData.name}
          onChange={handleChange}
          placeholder="Nombre de la regla"
        />
        <select
          required
          name="maintenanceType"
          value={formData.maintenanceType}
          onChange={handleChange}
        >
          {mockMaintenanceTypes.map((type) => (
            <option key={type.id} value={type.id}>
              {type.name}
            </option>
          ))}
        </select>
        <input
          required
          name="intervalKm"
          value={formData.intervalKm === 0 ? '' : formData.intervalKm}
          onChange={handleChange}
          type="number"
          min="1"
          placeholder="Intervalo (km)"
        />
        <input
          required
          name="warningThresholdKm"
          value={formData.warningThresholdKm === 0 ? '' : formData.warningThresholdKm}
          onChange={handleChange}
          type="number"
          min="1"
          placeholder="Umbral aviso (km)"
        />
        <button type="submit" disabled={submitting}>
          {submitting ? 'Guardando...' : 'Añadir Regla'}
        </button>
      </form>

      <table>
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Tipo</th>
            <th>Intervalo</th>
            <th>Umbral</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          {loadingRules ? (
            <tr>
              <td colSpan={5}>Cargando...</td>
            </tr>
          ) : (
            rules.map((rule) => (
              <tr key={rule.id}>
                <td>{rule.name}</td>
                <td>{rule.maintenanceType}</td>
                <td>{rule.intervalKm.toLocaleString()} km</td>
                <td>{rule.warningThresholdKm.toLocaleString()} km</td>
                <td>{rule.status}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}