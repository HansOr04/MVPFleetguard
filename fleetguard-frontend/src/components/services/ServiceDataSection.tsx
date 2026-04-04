import React from 'react';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { InputField } from '@/components/ui/InputField';

interface ServiceDataSectionProps {
  recordedBy: string;
  performedAt: string;
  provider: string;
  cost: string;
  description: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => void;
}

export const ServiceDataSection: React.FC<ServiceDataSectionProps> = ({
  recordedBy,
  performedAt,
  provider,
  cost,
  description,
  onChange,
}) => {
  return (
    <section>
      <SectionHeader icon="build" title="Datos del Servicio" />
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <InputField
          label="Registrado por"
          name="recordedBy"
          value={recordedBy}
          onChange={onChange}
          placeholder="Nombre del técnico o responsable"
          required
        />
        <InputField
          label="Fecha del Servicio"
          name="performedAt"
          value={performedAt}
          onChange={onChange}
          type="date"
          required
        />
        <InputField
          label="Proveedor / Taller"
          name="provider"
          value={provider}
          onChange={onChange}
          placeholder="Taller autorizado"
        />
        <InputField
          label="Costo (USD)"
          name="cost"
          value={cost}
          onChange={onChange}
          type="number"
          placeholder="0.00"
          min={0}
          step="0.01"
          onWheel={(e) => e.currentTarget.blur()}
        />
        <div className="md:col-span-2 space-y-2">
          <label className="block text-sm font-semibold text-on-surface-variant px-1">
            Descripción del Trabajo Realizado
          </label>
          <textarea
            name="description"
            value={description}
            onChange={onChange}
            rows={3}
            placeholder="Detalle los repuestos utilizados y la mano de obra..."
            className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all outline-none resize-none"
          />
        </div>
      </div>
    </section>
  );
};