import React from 'react';
import { CreateVehicleDto } from '@/types';
import { mockVehicleTypes } from '@/lib/mocks/mockVehicleTypes';

interface VehiclePreviewCardProps {
  formData: CreateVehicleDto;
}

export const VehiclePreviewCard: React.FC<VehiclePreviewCardProps> = ({ formData }) => {
  return (
    <div className="bg-primary text-white rounded-2xl p-8 shadow-xl relative overflow-hidden group">
      <div className="absolute -right-20 -bottom-20 w-72 h-72 bg-secondary/10 rounded-full blur-3xl group-hover:bg-secondary/20 transition-colors" />
      <div className="relative z-10 flex flex-col gap-8">
        <div className="flex justify-between items-start">
          <div className="w-14 h-9 rounded-md bg-white/10 backdrop-blur flex items-center justify-center text-[10px] tracking-widest font-bold">
            CHIP
          </div>
          <span className="material-symbols-outlined text-4xl text-secondary-container">
            contactless
          </span>
        </div>
        <div>
          <p className="text-[10px] uppercase tracking-[0.25em] opacity-40 mb-2">
            Identificador de Flota
          </p>
          <p className="text-3xl font-mono tracking-widest font-extrabold">
            {formData.plate || '------'}
          </p>
        </div>
        <div className="flex flex-wrap gap-x-10 gap-y-4 text-sm">
          <div>
            <p className="text-[10px] uppercase tracking-wider opacity-40">Vehículo</p>
            <p className="font-semibold">
              {formData.brand || formData.model
                ? `${formData.brand || '---'} ${formData.model || '---'}`
                : '------'}
            </p>
          </div>
          <div>
            <p className="text-[10px] uppercase tracking-wider opacity-40">Año</p>
            <p className="font-semibold">{formData.year || '------'}</p>
          </div>
          <div>
            <p className="text-[10px] uppercase tracking-wider opacity-40">Combustible</p>
            <p className="font-semibold">{formData.fuelType || '---'}</p>
          </div>
          <div>
            <p className="text-[10px] uppercase tracking-wider opacity-40">Tipo</p>
            <p className="font-semibold">
              {mockVehicleTypes.find((t) => t.id === formData.vehicleTypeId)?.name || '------'}
            </p>
          </div>
        </div>
        <div className="flex justify-between items-end">
          <div>
            <p className="text-[10px] uppercase tracking-[0.2em] opacity-40 mb-1">VIN</p>
            <p className="text-xs font-mono tracking-wider opacity-70">
              {formData.vin || '-----------------'}
            </p>
          </div>
          <div className="flex items-center gap-2 bg-white/10 px-3 py-1.5 rounded-full backdrop-blur">
            <span className="w-2 h-2 rounded-full bg-secondary" />
            <span className="text-xs font-semibold">Pendiente</span>
          </div>
        </div>
      </div>
    </div>
  );
};