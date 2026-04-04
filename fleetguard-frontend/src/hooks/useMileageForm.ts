import { useState } from 'react';
import { vehicleService } from '@/services/vehicle.service';
import { alertService } from '@/services/alert.service';
import { mileageValidator } from '@/validators/mileage.validator';
import { MileageLog, MaintenanceAlert } from '@/types';

interface MileageFormState {
  plate: string;
  newMileage: number | '';
  recordedBy: string;
}

interface UseMileageFormReturn {
  formState: MileageFormState;
  submitting: boolean;
  loadingAlerts: boolean;
  lastResult: MileageLog | null;
  lastPlate: string;
  generatedAlerts: MaintenanceAlert[];
  isFormValid: boolean;
  isNegative: boolean;
  isExcessive: boolean;
  mileageError: string | null;
  setPlate: (value: string) => void;
  setNewMileage: (value: number | '') => void;
  setRecordedBy: (value: string) => void;
  handleSubmit: (e: React.FormEvent) => Promise<void>;
  showToast: (message: string, type: 'success' | 'error') => void;
}

export function useMileageForm(
  showToast: (message: string, type: 'success' | 'error') => void
): UseMileageFormReturn {
  const [plate, setPlate] = useState('');
  const [newMileage, setNewMileage] = useState<number | ''>('');
  const [recordedBy, setRecordedBy] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [loadingAlerts, setLoadingAlerts] = useState(false);
  const [lastResult, setLastResult] = useState<MileageLog | null>(null);
  const [lastPlate, setLastPlate] = useState('');
  const [generatedAlerts, setGeneratedAlerts] = useState<MaintenanceAlert[]>([]);

  const isNegative = typeof newMileage === 'number' && newMileage < 0;
  const isExcessive = lastResult !== null && lastResult.excessiveIncrement;
  const mileageError = mileageValidator.mileageValue(newMileage);
  const isFormValid = mileageValidator.isFormValid(plate, newMileage, recordedBy);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;

    setSubmitting(true);
    setGeneratedAlerts([]);
    const submittedPlate = plate.toUpperCase();

    try {
      const result = await vehicleService.updateMileage(submittedPlate, {
        mileageValue: newMileage as number,
        recordedBy,
      });

      setLastResult(result);
      setLastPlate(submittedPlate);
      showToast('Odómetro actualizado correctamente', 'success');
      setNewMileage('');
      setRecordedBy('');

      setLoadingAlerts(true);
      await new Promise((resolve) => setTimeout(resolve, 1500));

      try {
        const alerts = await alertService.getByPlate(submittedPlate);
        setGeneratedAlerts(alerts);
      } catch {
      } finally {
        setLoadingAlerts(false);
      }
    } catch (error: unknown) {
      const e = error as { status?: number; message?: string; errors?: string[] };
      if (e.status === 0) {
        showToast('Sin conexión con el servidor', 'error');
      } else if (e.status === 404) {
        showToast('Vehículo no encontrado. Verifica la placa.', 'error');
      } else if (e.status === 400 && e.errors) {
        showToast(`Error de validación: ${e.errors.join(', ')}`, 'error');
      } else {
        showToast(e.message || 'Error al actualizar el kilometraje', 'error');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return {
    formState: { plate, newMileage, recordedBy },
    submitting,
    loadingAlerts,
    lastResult,
    lastPlate,
    generatedAlerts,
    isFormValid,
    isNegative,
    isExcessive,
    mileageError,
    setPlate,
    setNewMileage,
    setRecordedBy,
    handleSubmit,
    showToast,
  };
}