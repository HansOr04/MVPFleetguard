import { useState, useRef, useEffect, useCallback } from 'react';
import { ruleService } from '@/services/rule.service';
import { mockMaintenanceRules } from '@/lib/mocks/mockMaintenanceRules';
import { CreateRuleDto } from '@/types';

interface RuleFormData extends CreateRuleDto {
  intervalKm: number;
  warningThresholdKm: number;
}

const initialFormData: RuleFormData = {
  name: '',
  maintenanceType: '',
  intervalKm: 0,
  warningThresholdKm: 0,
};

interface UseRuleFormReturn {
  formData: RuleFormData;
  nameInput: string;
  nameIsValid: boolean;
  showSuggestions: boolean;
  highlightedIndex: number;
  filteredSuggestions: typeof mockMaintenanceRules;
  selectedVehicleTypes: string[];
  submitting: boolean;
  isFormValid: boolean;
  inputRef: React.RefObject<HTMLInputElement>;
  suggestionRef: React.RefObject<HTMLDivElement>;
  handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void;
  handleNameInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleNameKeyDown: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  handleSelectSuggestion: (rule: typeof mockMaintenanceRules[0]) => void;
  handleCheckboxChange: (vehicleTypeId: string) => void;
  handleNameFocus: () => void;
  handleSubmit: (e: React.FormEvent) => Promise<void>;
}

export function useRuleForm(
  showToast: (message: string, type: 'success' | 'error') => void,
  onSuccess: () => void
): UseRuleFormReturn {
  const [formData, setFormData] = useState<RuleFormData>(initialFormData);
  const [nameInput, setNameInput] = useState('');
  const [nameIsValid, setNameIsValid] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(-1);
  const [selectedVehicleTypes, setSelectedVehicleTypes] = useState<string[]>([]);
  const [submitting, setSubmitting] = useState(false);

  const inputRef = useRef<HTMLInputElement>(null);
  const suggestionRef = useRef<HTMLDivElement>(null);

  const filteredSuggestions =
    nameInput.trim().length > 0
      ? mockMaintenanceRules.filter((r) =>
          r.ruleType.toLowerCase().includes(nameInput.toLowerCase())
        )
      : [];

  const isFormValid =
    nameIsValid &&
    !!formData.name &&
    formData.intervalKm > 0 &&
    !!formData.maintenanceType &&
    selectedVehicleTypes.length > 0;

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        suggestionRef.current &&
        !suggestionRef.current.contains(e.target as Node) &&
        inputRef.current &&
        !inputRef.current.contains(e.target as Node)
      ) {
        setShowSuggestions(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelectSuggestion = useCallback((rule: typeof mockMaintenanceRules[0]) => {
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
  }, []);

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

  const handleNameFocus = () => {
    if (nameInput.trim().length > 0) setShowSuggestions(true);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]:
        name === 'intervalKm' || name === 'warningThresholdKm' ? Number(value) : value,
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
      const createdRule = await ruleService.create(formData);
      let associationErrors = 0;
      for (const vehicleTypeId of selectedVehicleTypes) {
        try {
          await ruleService.associateVehicleType(createdRule.id, { vehicleTypeId });
        } catch {
          associationErrors++;
        }
      }
      if (associationErrors > 0) {
        showToast(`Regla creada, pero ${associationErrors} asociación(es) fallaron`, 'error');
      } else {
        showToast('Regla de mantenimiento creada exitosamente', 'success');
      }
      setFormData(initialFormData);
      setNameInput('');
      setNameIsValid(false);
      setSelectedVehicleTypes([]);
      onSuccess();
    } catch (error: unknown) {
      const e = error as { message?: string };
      showToast(e.message || 'Error al crear la regla', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return {
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
  };
}