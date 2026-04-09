'use client'

import React from 'react';
import { useToast } from '@/hooks/useToast';
import { useRegisterVehicleForm } from '@/hooks/useRegisterVehicleForm';
import { Toast } from '@/components/ui/Toast';
import { Button } from '@/components/ui/Button';
import { InfoCard } from '@/components/feedback/InfoCard';
import { VehicleIdentificationSection } from '@/components/register/VehicleIdentificationSection';
import { VehicleSpecsSection } from '@/components/register/VehicleSpecsSection';
import { VehiclePreviewCard } from '@/components/register/VehiclePreviewCard';

export default function RegisterVehiclePage() {
  const { toast, showToast } = useToast();
  const {
    formData,
    loading,
    plateError,
    vinError,
    isVinValid,
    isFormValid,
    handleChange,
    handleSubmit,
  } = useRegisterVehicleForm(showToast);

  return (
    <div className="min-h-screen">
      <div className="p-12 pt-24 max-w-[1600px] mx-auto">
        <div className="mb-12">
          <h2 className="text-4xl font-extrabold tracking-tight text-primary mb-2">
            Registrar Nuevo Vehículo
          </h2>
          <p className="text-on-surface-variant text-lg max-w-2xl">
            Completa la información técnica para integrar la nueva unidad al sistema central de monitoreo de flota.
          </p>
        </div>

        <div className="grid grid-cols-12 gap-8">
          <div className="col-span-12 lg:col-span-8 bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden p-8">
            <form onSubmit={handleSubmit} className="space-y-10">
              <VehicleIdentificationSection
                plate={formData.plate}
                vin={formData.vin}
                plateError={plateError}
                vinError={vinError}
                isPlateValid={formData.plate.length > 0}
                isVinValid={isVinValid}
                onChange={handleChange}
              />
              <VehicleSpecsSection
                brand={formData.brand}
                model={formData.model}
                year={formData.year as number | ''}
                fuelType={formData.fuelType}
                vehicleTypeId={formData.vehicleTypeId}
                onChange={handleChange}
              />
              <div className="pt-8 flex items-center justify-end gap-4 border-t border-slate-100">
                <Button
                  type="submit"
                  disabled={!isFormValid}
                  loading={loading}
                  icon="save"
                >
                  Guardar Vehículo
                </Button>
              </div>
            </form>
          </div>

          <div className="col-span-12 lg:col-span-4 space-y-8">
            <VehiclePreviewCard formData={formData} />
            <InfoCard icon="info" title="Guía de Registro">
              <ul className="space-y-3">
                <li>• Asegúrate de que el <strong>VIN</strong> coincida exactamente con la placa física del vehículo.</li>
                <li>• Selecciona el <strong>tipo de vehículo</strong> más adecuado para el registro.</li>
              </ul>
            </InfoCard>
          </div>
        </div>
      </div>
      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}