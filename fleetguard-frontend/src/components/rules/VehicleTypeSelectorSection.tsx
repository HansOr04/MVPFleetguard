import React from 'react';
import { mockVehicleTypes } from '@/lib/mocks/mockVehicleTypes';

interface VehicleTypeSelectorSectionProps {
  selectedVehicleTypes: string[];
  onToggle: (vehicleTypeId: string) => void;
}

export const VehicleTypeSelectorSection: React.FC<VehicleTypeSelectorSectionProps> = ({
  selectedVehicleTypes,
  onToggle,
}) => {
  return (
    <div className="space-y-3">
      <div className="flex items-center gap-2 mb-4">
        <span className="material-symbols-outlined text-secondary">directions_car</span>
        <h3 className="text-xl font-bold text-primary">Tipos de Vehículo a Asociar</h3>
      </div>
      <div className="flex flex-wrap gap-3">
        {mockVehicleTypes.map((type) => {
          const isSelected = selectedVehicleTypes.includes(type.id);
          return (
            <button
              key={type.id}
              type="button"
              onClick={() => onToggle(type.id)}
              className={`px-4 py-2 rounded-full text-sm font-semibold border-2 transition-all ${
                isSelected
                  ? 'bg-secondary text-white border-secondary shadow-sm'
                  : 'bg-surface-container-highest text-on-surface-variant border-transparent hover:border-secondary/30'
              }`}
            >
              {type.name}
            </button>
          );
        })}
      </div>
      {selectedVehicleTypes.length === 0 && (
        <p className="text-[11px] text-on-surface-variant/60 px-1">
          Debe seleccionar al menos un tipo de vehículo.
        </p>
      )}
    </div>
  );
};