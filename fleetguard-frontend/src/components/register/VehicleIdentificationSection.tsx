import React from 'react';
import { SectionHeader } from '@/components/ui/SectionHeader';

interface VehicleIdentificationSectionProps {
  plate: string;
  vin: string;
  plateError: string;
  vinError: string;
  isPlateValid: boolean;
  isVinValid: boolean;
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void;
}

export const VehicleIdentificationSection: React.FC<VehicleIdentificationSectionProps> = ({
  plate,
  vin,
  plateError,
  vinError,
  isPlateValid,
  isVinValid,
  onChange,
}) => {
  return (
    <section>
      <SectionHeader icon="badge" title="Identificación del Vehículo" />
      <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-6">
        <div className="space-y-2">
          <label className="block text-sm font-semibold text-on-surface-variant px-1">
            Placa <span className="text-error">*</span>
          </label>
          <div className="relative">
            <input
              name="plate"
              value={plate}
              onChange={onChange}
              required
              placeholder="Ej: ABC-1234"
              type="text"
              className={`w-full border-none rounded-lg py-3 px-4 focus:ring-2 transition-all font-mono tracking-widest text-lg outline-none uppercase ${plateError
                ? 'bg-error-container/30 focus:ring-error/20'
                : 'bg-surface-container-highest focus:ring-secondary/20'
                }`}
            />
            {isPlateValid && !plateError && (
              <div className="absolute right-3 top-1/2 -translate-y-1/2">
                <span
                  className="material-symbols-outlined text-secondary"
                  style={{ fontVariationSettings: "'FILL' 1" }}
                >
                  check_circle
                </span>
              </div>
            )}
          </div>
          {plateError ? (
            <p className="text-[11px] text-error font-medium px-1">{plateError}</p>
          ) : (
            <p className="text-[11px] text-secondary font-medium px-1">Formato de placa válido</p>
          )}
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-semibold text-on-surface-variant px-1">
            Número de Serie (VIN) <span className="text-error">*</span>
          </label>
          <div className="relative">
            <input
              name="vin"
              value={vin}
              onChange={onChange}
              required
              maxLength={17}
              placeholder="17 caracteres requeridos"
              type="text"
              className={`w-full border-none rounded-lg py-3 px-4 pr-16 focus:ring-2 transition-all font-mono uppercase outline-none ${vinError || (!isVinValid && vin.length > 0)
                ? 'bg-error-container/30 focus:ring-error/20'
                : 'bg-surface-container-highest focus:ring-secondary/20'
                }`}
            />
            <div className="absolute right-3 top-1/2 -translate-y-1/2">
              <span className={`text-[11px] font-bold ${!isVinValid ? 'text-error' : 'text-secondary'}`}>
                {vin.length}/17
              </span>
            </div>
          </div>
          {vinError ? (
            <p className="text-[11px] text-error font-medium px-1">{vinError}</p>
          ) : (
            !isVinValid && vin.length > 0 && (
              <p className="text-[11px] text-error font-medium px-1">
                Debe contener exactamente 17 caracteres alfanuméricos.
              </p>
            )
          )}
        </div>
      </div>
    </section>
  );
};