'use client'

import React, { useState, useRef, useEffect } from 'react';
import { rulesApi } from '@/lib/api';
import { useRules } from '@/hooks/useRules';
import { useAlerts } from '@/hooks/useAlerts';
import { useToast } from '@/hooks/useToast';
import { Toast } from '@/components/ui/Toast';
import { mockVehicleTypes } from '@/lib/mocks/mockVehicleTypes';
import { mockMaintenanceTypes } from '@/lib/mocks/mockMaintenanceTypes';
import { mockMaintenanceRules } from '@/lib/mocks/mockMaintenanceRules';
import { CreateRuleDto } from '@/types';

interface RuleFormData extends CreateRuleDto {
  intervalKm: number;
  warningThresholdKm: number;
}

export default function RulesPage() {
  const { toast, showToast } = useToast();
  const { rules, loading: loadingRules, refetch } = useRules();
  const { alerts } = useAlerts('PENDING');

  const [formData, setFormData] = useState<RuleFormData>({
    name: '',
    maintenanceType: '',
    intervalKm: 0,
    warningThresholdKm: 0,
  });

  const [selectedVehicleTypes, setSelectedVehicleTypes] = useState<string[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [nameInput, setNameInput] = useState('');
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(-1);
  const [nameIsValid, setNameIsValid] = useState(false);
  const suggestionRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const filteredSuggestions = nameInput.trim().length > 0
    ? mockMaintenanceRules.filter((r) =>
        r.ruleType.toLowerCase().includes(nameInput.toLowerCase())
      )
    : [];

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        suggestionRef.current && !suggestionRef.current.contains(e.target as Node) &&
        inputRef.current && !inputRef.current.contains(e.target as Node)
      ) {
        setShowSuggestions(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelectSuggestion = (rule: typeof mockMaintenanceRules[0]) => {
    setNameInput(rule.ruleType);
    setFormData((prev) => ({
      ...prev,
      name: rule.ruleType,
      intervalKm: rule.intervalKm,
      warningThresholdKm: rule.thresholdKm,
    }));
    setNameIsValid(true);
    setShowSuggestions(false);
    setHighlightedIndex(-1);
  };

  const handleNameInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setNameInput(value);
    const exactMatch = mockMaintenanceRules.find(
      (r) => r.ruleType.toLowerCase() === value.toLowerCase()
    );
    if (exactMatch) {
      setFormData((prev) => ({
        ...prev,
        name: exactMatch.ruleType,
        intervalKm: exactMatch.intervalKm,
        warningThresholdKm: exactMatch.thresholdKm,
      }));
      setNameIsValid(true);
    } else {
      setFormData((prev) => ({ ...prev, name: '' }));
      setNameIsValid(false);
    }
    setShowSuggestions(true);
    setHighlightedIndex(-1);
  };

  const handleNameKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (!showSuggestions || filteredSuggestions.length === 0) return;
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setHighlightedIndex((prev) => Math.min(prev + 1, filteredSuggestions.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setHighlightedIndex((prev) => Math.max(prev - 1, 0));
    } else if (e.key === 'Enter' && highlightedIndex >= 0) {
      e.preventDefault();
      handleSelectSuggestion(filteredSuggestions[highlightedIndex]);
    } else if (e.key === 'Escape') {
      setShowSuggestions(false);
    }
  };

  const isFormValid =
    nameIsValid &&
    !!formData.name &&
    formData.intervalKm > 0 &&
    !!formData.maintenanceType &&
    selectedVehicleTypes.length > 0;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]:
        name === 'intervalKm' || name === 'warningThresholdKm'
          ? Number(value)
          : value,
    }));
  };

  const handleCheckboxChange = (vehicleTypeId: string) => {
    setSelectedVehicleTypes((prev) =>
      prev.includes(vehicleTypeId)
        ? prev.filter((id) => id !== vehicleTypeId)
        : [...prev, vehicleTypeId]
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;

    setSubmitting(true);
    try {
      const createdRule = await rulesApi.create(formData);

      let associationErrors = 0;
      for (const vehicleTypeId of selectedVehicleTypes) {
        try {
          await rulesApi.associateVehicleType(createdRule.id, { vehicleTypeId });
        } catch {
          associationErrors++;
        }
      }

      if (associationErrors > 0) {
        showToast(`Regla creada, pero ${associationErrors} asociación(es) fallaron`, 'error');
      } else {
        showToast('Regla de mantenimiento creada exitosamente', 'success');
      }

      setFormData({
        name: '',
        maintenanceType: '',
        intervalKm: 0,
        warningThresholdKm: 0,
      });
      setNameInput('');
      setNameIsValid(false);
      setSelectedVehicleTypes([]);
      refetch();
    } catch (error: unknown) {
      const e = error as { message?: string };
      showToast(e.message || 'Error al crear la regla', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen">
      <div className="p-12 pt-24 max-w-[1600px] mx-auto">

        <div className="mb-12">
          <h2 className="text-4xl font-extrabold tracking-tight text-primary mb-2">
            Reglas de Mantenimiento
          </h2>
          <p className="text-on-surface-variant text-lg max-w-2xl">
            Define los intervalos de mantenimiento preventivo y asócialos a los tipos de vehículo de tu flota.
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

                <div className="space-y-2">
                  <label className="block text-sm font-semibold text-on-surface-variant px-1">
                    Nombre de la Regla <span className="text-error">*</span>
                  </label>
                  <div className="relative">
                    <input
                      ref={inputRef}
                      required
                      name="name"
                      value={nameInput}
                      onChange={handleNameInputChange}
                      onKeyDown={handleNameKeyDown}
                      onFocus={() => { if (nameInput.trim().length > 0) setShowSuggestions(true); }}
                      className={`w-full border-none rounded-lg py-3 px-4 focus:ring-2 transition-all text-on-surface outline-none ${
                        nameInput.length > 0 && !nameIsValid
                          ? 'bg-error-container/30 focus:ring-error/20'
                          : 'bg-surface-container-highest focus:ring-secondary/20'
                      }`}
                      placeholder="Escribe para buscar una regla..."
                      type="text"
                      autoComplete="off"
                    />
                    {nameInput.length > 0 && (
                      <div className="absolute right-3 top-1/2 -translate-y-1/2">
                        {nameIsValid
                          ? <span className="material-symbols-outlined text-secondary" style={{ fontVariationSettings: "'FILL' 1" }}>check_circle</span>
                          : <span className="material-symbols-outlined text-error" style={{ fontVariationSettings: "'FILL' 1" }}>error</span>
                        }
                      </div>
                    )}
                    {showSuggestions && filteredSuggestions.length > 0 && (
                      <div
                        ref={suggestionRef}
                        className="absolute z-50 left-0 right-0 top-full mt-1 bg-surface-container-lowest rounded-xl shadow-xl border border-outline-variant/20 max-h-64 overflow-y-auto"
                      >
                        {filteredSuggestions.map((rule, index) => (
                          <button
                            key={rule.ruleType}
                            type="button"
                            onMouseDown={() => handleSelectSuggestion(rule)}
                            className={`w-full text-left px-4 py-3 transition-colors ${
                              index === highlightedIndex
                                ? 'bg-secondary/10 text-secondary'
                                : 'hover:bg-surface-container-high text-on-surface'
                            } ${index !== filteredSuggestions.length - 1 ? 'border-b border-outline-variant/10' : ''}`}
                          >
                            <span className="text-sm font-medium">{rule.ruleType}</span>
                          </button>
                        ))}
                      </div>
                    )}
                    {showSuggestions && nameInput.trim().length > 0 && filteredSuggestions.length === 0 && (
                      <div className="absolute z-50 left-0 right-0 top-full mt-1 bg-surface-container-lowest rounded-xl shadow-xl border border-outline-variant/20 px-4 py-3">
                        <p className="text-sm text-on-surface-variant">No se encontraron reglas para esta búsqueda.</p>
                      </div>
                    )}
                  </div>
                  {nameInput.length > 0 && !nameIsValid && (
                    <p className="text-[11px] text-error font-medium px-1">
                      Selecciona una opción válida de la lista.
                    </p>
                  )}
                </div>

                <div className="space-y-2">
                  <label className="block text-sm font-semibold text-on-surface-variant px-1">
                    Tipo de Mantenimiento <span className="text-error">*</span>
                  </label>
                  <select
                    required
                    name="maintenanceType"
                    value={formData.maintenanceType}
                    onChange={handleChange}
                    className="w-full bg-surface-container-highest border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all text-on-surface outline-none cursor-pointer"
                  >
                    <option value="" disabled>
                      Seleccionar tipo...
                    </option>
                    {mockMaintenanceTypes.map((type) => (
                      <option key={type.id} value={type.id}>
                        {type.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="space-y-2">
                  <label className="block text-sm font-semibold text-on-surface-variant px-1">
                    Intervalo (km) <span className="text-error">*</span>
                  </label>
                  <input
                    required
                    name="intervalKm"
                    value={formData.intervalKm || ''}
                    onChange={handleChange}
                    min={1}
                    readOnly={nameIsValid}
                    className={`w-full border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all text-on-surface outline-none ${
                      nameIsValid ? 'bg-surface-container-high opacity-70 cursor-not-allowed' : 'bg-surface-container-highest'
                    }`}
                    placeholder="Ej: 10000"
                    type="number"
                  />
                </div>

                <div className="space-y-2">
                  <label className="block text-sm font-semibold text-on-surface-variant px-1">
                    Umbral de Aviso (km) <span className="text-error">*</span>
                  </label>
                  <input
                    required
                    name="warningThresholdKm"
                    value={formData.warningThresholdKm || ''}
                    onChange={handleChange}
                    min={0}
                    readOnly={nameIsValid}
                    className={`w-full border-none rounded-lg py-3 px-4 focus:ring-2 focus:ring-secondary/20 transition-all text-on-surface outline-none ${
                      nameIsValid ? 'bg-surface-container-high opacity-70 cursor-not-allowed' : 'bg-surface-container-highest'
                    }`}
                    placeholder="Ej: 500"
                    type="number"
                  />
                </div>
              </div>

              <div className="space-y-3">
                <div className="flex items-center gap-2 mb-8">
                  <span className="material-symbols-outlined text-secondary">
                    directions_car
                  </span>
                  <label className="text-xl font-bold text-primary flex items-center gap-2">
                    Tipos de Vehículo a Asociar
                  </label>
                </div>

                <div className="flex flex-wrap gap-3">
                  {mockVehicleTypes.map((type) => {
                    const isSelected = selectedVehicleTypes.includes(type.id);
                    return (
                      <button
                        key={type.id}
                        type="button"
                        onClick={() => handleCheckboxChange(type.id)}
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

              <div className="pt-6 flex items-center justify-end border-t border-slate-100">
                <button
                  type="submit"
                  disabled={!isFormValid || submitting}
                  className="px-10 py-3 rounded-lg bg-secondary text-white font-bold shadow-lg shadow-secondary/20 hover:bg-on-secondary-container transition-all flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {submitting ? (
                    <span className="material-symbols-outlined animate-spin text-sm">
                      sync
                    </span>
                  ) : (
                    <span className="material-symbols-outlined text-sm">
                      save
                    </span>
                  )}
                  Crear Regla
                </button>
              </div>
            </form>
          </div>

          <div className="col-span-12 lg:col-span-4 space-y-6">
            <div className="bg-surface-container-low rounded-xl p-6 border-l-4 border-secondary">
              <h4 className="font-bold text-primary mb-4 flex items-center gap-2">
                <span className="material-symbols-outlined text-secondary" style={{ fontVariationSettings: "'FILL' 1" }}>info</span>
                Guía de Registro
              </h4>
              <ul className="text-sm text-on-surface-variant leading-relaxed space-y-3">
                <li>• Selecciona una <strong>regla de mantenimiento</strong> que desees agregar.</li>
                <li>• El <strong>intervalo</strong> define cada cuántos kilómetros se debe ejecutar el mantenimiento.</li>
                <li>• El <strong>umbral de aviso</strong> indica con cuántos km de anticipación se generará la alerta.</li>
                <li>• Selecciona <strong>al menos un tipo de vehículo</strong> para activar la regla en la flota correspondiente.</li>
              </ul>
            </div>

            <div className="bg-primary text-white rounded-xl p-8 shadow-xl relative overflow-hidden">
              <div className="absolute -right-12 -bottom-12 w-48 h-48 bg-secondary/10 rounded-full blur-3xl" />
              <div className="relative z-10">
                <span
                  className="material-symbols-outlined text-4xl text-secondary-container mb-4 block"
                  style={{ fontVariationSettings: "'FILL' 1" }}
                >
                  settings_applications
                </span>
                <p className="text-sm font-medium opacity-80 leading-relaxed">
                  Las reglas bien configuradas permiten que el sistema genere alertas precisas antes de que se venza el mantenimiento, evitando fallas imprevistas en tu flota.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <Toast message={toast.message} type={toast.type} visible={toast.visible} />
    </div>
  );
}