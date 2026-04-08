import React from 'react';
import { InputField } from '@/components/ui/InputField';
import { Button } from '@/components/ui/Button';

interface MileageFormProps {
  plate: string;
  newMileage: number | '';
  recordedBy: string;
  isNegative: boolean;
  isFormValid: boolean;
  submitting: boolean;
  loadingAlerts: boolean;
  onPlateChange: (value: string) => void;
  onMileageChange: (value: number | '') => void;
  onRecordedByChange: (value: string) => void;
  onSubmit: (e: React.FormEvent) => void;
}

export const MileageForm: React.FC<MileageFormProps> = ({
  plate,
  newMileage,
  recordedBy,
  isNegative,
  isFormValid,
  submitting,
  loadingAlerts,
  onPlateChange,
  onMileageChange,
  onRecordedByChange,
  onSubmit,
}) => {
  return (
    <div className="bg-surface-container-lowest rounded-xl shadow-sm p-8">
      <form onSubmit={onSubmit} className="space-y-6">
        <InputField
          label="Placa del Vehículo"
          value={plate}
          onChange={(e) => onPlateChange(e.target.value)}
          placeholder="Ej: ABC-1234"
          required
          uppercase
        />
        <InputField
          label="Nuevo Kilometraje"
          value={newMileage}
          onChange={(e) => onMileageChange(e.target.value === '' ? '' : Number(e.target.value))}
          type="number"
          placeholder="0"
          required
          min={1}
          onWheel={(e) => e.currentTarget.blur()}
          errorMessage={isNegative ? 'El kilometraje no puede ser negativo.' : undefined}
        />
        <InputField
          label="Registrado por"
          value={recordedBy}
          onChange={(e) => onRecordedByChange(e.target.value)}
          placeholder="Nombre del conductor o técnico"
          required
        />
        <div className="pt-4 flex justify-end border-t border-slate-100">
          <Button
            type="submit"
            disabled={!isFormValid}
            loading={submitting || loadingAlerts}
            icon="speed"
          >
            {loadingAlerts ? 'Verificando alertas...' : 'Actualizar Odómetro'}
          </Button>
        </div>
      </form>
    </div>
  );
};