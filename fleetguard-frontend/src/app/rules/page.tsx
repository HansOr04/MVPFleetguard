'use client'

import React from 'react';
import { useToast } from '@/hooks/useToast';
import { Toast } from '@/components/ui/Toast';

export default function RulesPage() {
  const { toast, showToast } = useToast();

  return (
    <div>
      <h1>Reglas de Mantenimiento</h1>

      <form>
        <input name="name" placeholder="Nombre de la regla" />
        <select name="maintenanceType">
          <option value="PREVENTIVE">Preventivo</option>
          <option value="CORRECTIVE">Correctivo</option>
        </select>
        <input name="intervalKm" type="number" placeholder="Intervalo (km)" />
        <input name="warningThresholdKm" type="number" placeholder="Umbral aviso (km)" />
        <button type="submit">Añadir Regla</button>
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
          <tr>
            <td colSpan={5}>Cargando...</td>
          </tr>
        </tbody>
      </table>

      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}