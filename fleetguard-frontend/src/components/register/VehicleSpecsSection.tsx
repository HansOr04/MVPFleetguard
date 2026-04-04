import React from 'react';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { InputField } from '@/components/ui/InputField';
import { mockVehicleTypes } from '@/lib/mocks/mockVehicleTypes';
import { mockFuelTypes } from '@/lib/mocks/mockFuelTypes';

interface VehicleSpecsSectionProps {
  brand: string;
  model: string;
  year: number | '';
  fuelType: string;
  vehicleTypeId: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void;
}

export const VehicleSpecsSection: React.FC<VehicleSpecsSectionProps> = ({
  brand,
  model,
  year,
  fuelType,
  vehicleTypeId,
  onChange,
}) => {
  return (
    <section>
      <SectionHeader icon="settings" title="Especificaciones Técnicas" />
      <div className="grid grid-cols-1 md:grid-cols-3 gap-x-6 gap-y-6">
        <div className="md:col-span-2 grid grid-cols-2 gap-4">
          <InputField
            label="Marca"
            name="brand"
            value={brand}
            onChange={onChange}
            placeholder="Ej: Toyota"
            required
          />
          <InputField
            label="Modelo"
            name="model"
            value={model}
            onChange={onChange}
            placeholder="Ej: Hilux"
            required
          />
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-semibold text-on-surface-variant px-1">
            Año <span className="text-error">*</span>
          </label>
          <select
            name="year"
            value={year}
            onChange={onChange}
            required
            className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all cursor-pointer outline-none"
          >
            <option value="">Seleccionar año...</option>
            {Array.from({ length: 30 }, (_, i) => {
              const y = new Date().getFullYear() - i;
              return <option key={y} value={y}>{y}</option>;
            })}
          </select>
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-semibold text-on-surface-variant px-1">
            Tipo Combustible <span className="text-error">*</span>
          </label>
          <select
            name="fuelType"
            value={fuelType}
            onChange={onChange}
            required
            className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all cursor-pointer outline-none"
          >
            <option value="">Seleccionar tipo...</option>
            {mockFuelTypes.map((t) => (
              <option key={t.id} value={t.name}>{t.name}</option>
            ))}
          </select>
        </div>

        <div className="md:col-span-2 space-y-2">
          <label className="block text-sm font-semibold text-on-surface-variant px-1">
            Tipo de Vehículo <span className="text-error">*</span>
          </label>
          <select
            name="vehicleTypeId"
            value={vehicleTypeId}
            onChange={onChange}
            required
            className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all cursor-pointer outline-none"
          >
            <option value="">Seleccionar categoría...</option>
            {mockVehicleTypes.map((t) => (
              <option key={t.id} value={t.id}>{t.name}</option>
            ))}
          </select>
        </div>
      </div>
    </section>
  );
};