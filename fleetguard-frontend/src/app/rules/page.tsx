'use client'

import React from 'react';
import { useToast } from '@/hooks/useToast';
import { useRules } from '@/hooks/useRules';
import { useRuleForm } from '@/hooks/useRuleForm';
import { Toast } from '@/components/ui/Toast';
import { Button } from '@/components/ui/Button';
import { InputField } from '@/components/ui/InputField';
import { InfoCard } from '@/components/feedback/InfoCard';
import { RuleNameAutocomplete } from '@/components/rules/RuleNameAutocomplete';
import { VehicleTypeSelectorSection } from '@/components/rules/VehicleTypeSelectorSection';
import { mockMaintenanceTypes } from '@/lib/mocks/mockMaintenanceTypes';

export default function RulesPage() {
  const { toast, showToast } = useToast();
  const { refetch } = useRules();
  const {
    formData,
    nameInput,
    nameIsValid,
    showSuggestions,
    highlightedIndex,
    filteredSuggestions,
    selectedVehicleTypes,
    submitting,
    isFormValid,
    inputRef,
    suggestionRef,
    handleChange,
    handleNameInputChange,
    handleNameKeyDown,
    handleSelectSuggestion,
    handleCheckboxChange,
    handleNameFocus,
    handleSubmit,
  } = useRuleForm(showToast, refetch);

  return (
    <div className="min-h-screen">
      <div className="p-12 pt-24 max-w-[1600px] mx-auto">
        <div className="mb-12">
          <h2 className="text-4xl font-extrabold tracking-tight text-primary mb-2">
            Reglas de Mantenimiento
          </h2>
          <p className="text-on-surface-variant text-lg max-w-2xl">
            Selecciona el intervalo de mantenimiento preventivo y asócialo a los tipos de vehículo de tu flota.
          </p>
        </div>

        <div className="grid grid-cols-12 gap-8">
          <div className="col-span-12 lg:col-span-8 bg-surface-container-lowest rounded-xl shadow-sm p-8">
            <div className="mb-8">
              <h3 className="text-xl font-bold text-primary flex items-center gap-2">
                <span className="material-symbols-outlined text-secondary">add_circle</span>
                Crear Nueva Regla
              </h3>
            </div>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <RuleNameAutocomplete
                  nameInput={nameInput}
                  nameIsValid={nameIsValid}
                  showSuggestions={showSuggestions}
                  highlightedIndex={highlightedIndex}
                  filteredSuggestions={filteredSuggestions}
                  inputRef={inputRef}
                  suggestionRef={suggestionRef}
                  onInputChange={handleNameInputChange}
                  onKeyDown={handleNameKeyDown}
                  onFocus={handleNameFocus}
                  onSelectSuggestion={handleSelectSuggestion}
                />
                <div className="space-y-2">
                  <label className="block text-sm font-semibold text-on-surface-variant px-1">
                    Tipo de Mantenimiento <span className="text-error">*</span>
                  </label>
                  <select
                    required
                    name="maintenanceType"
                    value={formData.maintenanceType}
                    onChange={handleChange}
                    className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all outline-none cursor-pointer"
                  >
                    <option value="" disabled>Seleccionar tipo...</option>
                    {mockMaintenanceTypes.map((type) => (
                      <option key={type.id} value={type.id}>{type.name}</option>
                    ))}
                  </select>
                </div>
                <InputField
                  label="Intervalo (km)"
                  name="intervalKm"
                  value={formData.intervalKm || ''}
                  onChange={handleChange}
                  type="number"
                  placeholder="Ej: 10000"
                  required
                  min={1}
                />
                <InputField
                  label="Umbral de Aviso (km)"
                  name="warningThresholdKm"
                  value={formData.warningThresholdKm || ''}
                  onChange={handleChange}
                  type="number"
                  placeholder="Ej: 500"
                  required
                  min={0}
                />
              </div>

              <VehicleTypeSelectorSection
                selectedVehicleTypes={selectedVehicleTypes}
                onToggle={handleCheckboxChange}
              />

              <div className="pt-6 flex items-center justify-end border-t border-slate-100">
                <Button
                  type="submit"
                  disabled={!isFormValid}
                  loading={submitting}
                  icon="save"
                >
                  Crear Regla
                </Button>
              </div>
            </form>
          </div>

          <div className="col-span-12 lg:col-span-4 space-y-6">
            <InfoCard icon="info" title="Guía de Registro">
              <ul className="space-y-3">
                <li>• Selecciona una <strong>regla de mantenimiento</strong> que desees agregar.</li>
                <li>• El <strong>intervalo</strong> define cada cuántos kilómetros se debe ejecutar el mantenimiento.</li>
                <li>• El <strong>umbral de aviso</strong> indica con cuántos km de anticipación se generará la alerta.</li>
                <li>• Selecciona <strong>al menos un tipo de vehículo</strong> para activar la regla en la flota correspondiente.</li>
              </ul>
            </InfoCard>
            <InfoCard icon="settings_applications" title="" variant="dark">
              Las reglas bien configuradas permiten que el sistema genere alertas precisas antes de que se venza el mantenimiento, evitando fallas imprevistas en tu flota.
            </InfoCard>
          </div>
        </div>
      </div>
      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}