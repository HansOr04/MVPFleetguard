'use client'

import React from 'react';
import { useToast } from '@/hooks/useToast';
import { useMileageForm } from '@/hooks/useMileageForm';
import { Toast } from '@/components/ui/Toast';
import { MileageForm } from '@/components/mileage/MileageForm';
import { MileageResultPanel } from '@/components/mileage/MileageResultPanel';
import { InfoCard } from '@/components/feedback/InfoCard';

export default function UpdateMileagePage() {
  const { toast, showToast } = useToast();
  const {
    formState,
    submitting,
    loadingAlerts,
    lastResult,
    lastPlate,
    generatedAlerts,
    isFormValid,
    isNegative,
    setPlate,
    setNewMileage,
    setRecordedBy,
    handleSubmit,
  } = useMileageForm(showToast);

  return (
    <div className="min-h-screen">
      <div className="p-12 pt-24 max-w-5xl mx-auto">
        <div className="mb-12">
          <h2 className="text-4xl font-extrabold tracking-tight text-primary mb-2">
            Actualizar Kilometraje
          </h2>
          <p className="text-on-surface-variant font-medium">
            Ingresa la placa del vehículo y el nuevo valor del odómetro para mantener el registro actualizado.
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
          <MileageForm
            plate={formState.plate}
            newMileage={formState.newMileage}
            recordedBy={formState.recordedBy}
            isNegative={isNegative}
            isFormValid={isFormValid}
            submitting={submitting}
            loadingAlerts={loadingAlerts}
            onPlateChange={setPlate}
            onMileageChange={setNewMileage}
            onRecordedByChange={setRecordedBy}
            onSubmit={handleSubmit}
          />

          <div className="space-y-4">
            {lastResult ? (
              <MileageResultPanel
                lastResult={lastResult}
                lastPlate={lastPlate}
                generatedAlerts={generatedAlerts}
                loadingAlerts={loadingAlerts}
              />
            ) : (
              <InfoCard icon="info" title="Guía de Registro">
                <ul className="space-y-2">
                  <li>• Ingresa la <strong>placa exacta</strong> tal como está registrada en el sistema.</li>
                  <li>• El kilometraje debe ser <strong>mayor a cero</strong>.</li>
                  <li>• El sistema avisará si el incremento es inusualmente alto una vez guardado.</li>
                </ul>
              </InfoCard>
            )}

            <InfoCard icon="speed" title="" variant="dark">
              Mantener el odómetro actualizado garantiza que las alertas de mantenimiento preventivo se generen en el momento correcto.
            </InfoCard>
          </div>
        </div>
      </div>
      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}