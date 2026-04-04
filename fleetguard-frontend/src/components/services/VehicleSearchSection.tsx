import React from 'react';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { InputField } from '@/components/ui/InputField';
import { Button } from '@/components/ui/Button';

interface VehicleSearchSectionProps {
  plate: string;
  loadingAlerts: boolean;
  onPlateChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSearch: () => void;
  onKeyDown: (e: React.KeyboardEvent<HTMLInputElement>) => void;
}

export const VehicleSearchSection: React.FC<VehicleSearchSectionProps> = ({
  plate,
  loadingAlerts,
  onPlateChange,
  onSearch,
  onKeyDown,
}) => {
  return (
    <section>
      <SectionHeader icon="badge" title="Identificación del Vehículo" />
      <div className="space-y-2">
        <InputField
          label="Placa"
          value={plate}
          onChange={onPlateChange}
          onKeyDown={onKeyDown}
          placeholder="Ej: ABC-1234"
          required
          uppercase
          prefixIcon="search"
        />
        <div className="flex justify-end pt-2">
          <Button
            type="button"
            onClick={onSearch}
            disabled={!plate.trim()}
            loading={loadingAlerts}
            icon="notifications_active"
          >
            Consultar alertas
          </Button>
        </div>
      </div>
    </section>
  );
};