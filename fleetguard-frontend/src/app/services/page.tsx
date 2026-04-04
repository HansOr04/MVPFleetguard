'use client'

import React from 'react';
import { useSearchParams } from 'next/navigation';
import { useToast } from '@/hooks/useToast';
import { useMaintenanceForm } from '@/hooks/useMaintenanceForm';
import { Toast } from '@/components/ui/Toast';
import { Button } from '@/components/ui/Button';
import { InfoCard } from '@/components/feedback/InfoCard';
import { VehicleSearchSection } from '@/components/services/VehicleSearchSection';
import { AlertSelectorSection } from '@/components/services/AlertSelectorSection';
import { ServiceDataSection } from '@/components/services/ServiceDataSection';
import { TraceabilitySection } from '@/components/services/TraceabilitySection';

export default function ServicesPage() {
  return (
    <React.Suspense fallback={<div className="p-12 pt-24 text-center text-on-surface-variant">Cargando...</div>}>
      <ServicesContent />
    </React.Suspense>
  );
}

function ServicesContent() {
  const { toast, showToast } = useToast();
  const searchParams = useSearchParams();

  const {
    plate,
    alerts,
    loadingAlerts,
    selectedAlert,
    alertSearchDone,
    formData,
    submitting,
    isFormValid,
    handlePlateChange,
    handleSearchAlerts,
    handleChange,
    handleSelectAlert,
    handleSubmit,
  } = useMaintenanceForm(showToast, searchParams.get('plate') ?? undefined);

  return (
    <div className="min-h-screen">
      <div className="p-12 pt-24 max-w-[1600px] mx-auto">
        <div className="mb-12">
          <h2 className="text-4xl font-extrabold text-primary tracking-tight mb-2">
            Registro de Servicios
          </h2>
          <p className="text-on-surface-variant text-lg max-w-2xl">
            Documenta las intervenciones de mantenimiento realizadas sobre los vehículos de la flota.
          </p>
        </div>

        <div className="grid grid-cols-12 gap-8">
          <div className="col-span-12 lg:col-span-8 bg-surface-container-lowest rounded-xl shadow-sm p-8">
            <form onSubmit={handleSubmit} className="space-y-8">
              <VehicleSearchSection
                plate={plate}
                loadingAlerts={loadingAlerts}
                onPlateChange={handlePlateChange}
                onSearch={handleSearchAlerts}
                onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); handleSearchAlerts(); } }}
              />

              {alertSearchDone && (
                <AlertSelectorSection
                  alerts={alerts}
                  selectedAlert={selectedAlert}
                  onSelectAlert={handleSelectAlert}
                />
              )}

              {selectedAlert && (
                <>
                  <ServiceDataSection
                    recordedBy={formData.recordedBy}
                    performedAt={formData.performedAt}
                    provider={formData.provider}
                    cost={formData.cost}
                    description={formData.description}
                    onChange={handleChange}
                  />
                  <TraceabilitySection
                    mileageAtService={formData.mileageAtService}
                    onChange={handleChange}
                  />
                  <div className="pt-6 flex items-center justify-end border-t border-slate-100">
                    <Button
                      type="submit"
                      disabled={!isFormValid}
                      loading={submitting}
                      icon="save"
                    >
                      Registrar Servicio
                    </Button>
                  </div>
                </>
              )}
            </form>
          </div>

          <div className="col-span-12 lg:col-span-4 space-y-6">
            <InfoCard icon="info" title="Guía de Registro">
              <ul className="space-y-3">
                <li>• Escribe la <strong>placa exacta</strong> del vehículo y presiona <strong>Consultar alertas</strong>.</li>
                <li>• Selecciona la <strong>alerta que este servicio resuelve</strong>.</li>
                <li>• Ingresa la <strong>fecha exacta</strong> en que se realizó el servicio.</li>
                <li>• Registra el <strong>kilometraje actual</strong> del vehículo al momento del servicio.</li>
                <li>• El <strong>proveedor, costo y descripción</strong> son opcionales pero recomendados.</li>
                <li>• Al guardar, la alerta quedará marcada como <strong>RESUELTA</strong>.</li>
              </ul>
            </InfoCard>

            <InfoCard icon="build_circle" title="" variant="dark">
              Cada registro de servicio cierra el ciclo de una alerta activa, manteniendo el historial de mantenimiento completo y preciso.
            </InfoCard>
          </div>
        </div>
      </div>
      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}