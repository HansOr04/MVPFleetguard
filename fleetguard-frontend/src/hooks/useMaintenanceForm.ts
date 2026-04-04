import { useState, useCallback } from 'react';
import { maintenanceService } from '@/services/maintenance.service';
import { alertService } from '@/services/alert.service';
import { maintenanceValidator } from '@/validators/maintenance.validator';
import { MaintenanceAlert, CreateMaintenanceDto } from '@/types';

interface MaintenanceFormData {
  recordedBy: string;
  description: string;
  cost: string;
  provider: string;
  performedAt: string;
  mileageAtService: string;
}

interface UseMaintenanceFormReturn {
  plate: string;
  alerts: MaintenanceAlert[];
  loadingAlerts: boolean;
  selectedAlert: MaintenanceAlert | null;
  alertSearchDone: boolean;
  formData: MaintenanceFormData;
  submitting: boolean;
  isFormValid: boolean;
  setPlate: (value: string) => void;
  handlePlateChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleSearchAlerts: () => Promise<void>;
  handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => void;
  handleSelectAlert: (alert: MaintenanceAlert) => void;
  handleSubmit: (e: React.FormEvent) => Promise<void>;
}

const initialFormData: MaintenanceFormData = {
  recordedBy: '',
  description: '',
  cost: '',
  provider: '',
  performedAt: new Date().toISOString().split('T')[0],
  mileageAtService: '',
};

export function useMaintenanceForm(
  showToast: (message: string, type: 'success' | 'error') => void,
  initialPlate?: string
): UseMaintenanceFormReturn {
  const [plate, setPlate] = useState(initialPlate ?? '');
  const [alerts, setAlerts] = useState<MaintenanceAlert[]>([]);
  const [loadingAlerts, setLoadingAlerts] = useState(false);
  const [selectedAlert, setSelectedAlert] = useState<MaintenanceAlert | null>(null);
  const [alertSearchDone, setAlertSearchDone] = useState(false);
  const [formData, setFormData] = useState<MaintenanceFormData>(initialFormData);
  const [submitting, setSubmitting] = useState(false);

  const isFormValid = maintenanceValidator.isFormValid(
    plate,
    selectedAlert?.id ?? null,
    formData.recordedBy,
    formData.performedAt,
    formData.mileageAtService
  );

  const handlePlateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPlate(e.target.value.toUpperCase());
    setAlerts([]);
    setSelectedAlert(null);
    setAlertSearchDone(false);
  };

  const handleSearchAlerts = useCallback(async () => {
    if (!plate.trim()) return;
    setLoadingAlerts(true);
    setSelectedAlert(null);
    setAlerts([]);
    try {
      const result = await alertService.getByPlate(plate.trim());
      setAlerts(result);
      setAlertSearchDone(true);
    } catch {
      showToast('Error al buscar alertas del vehículo', 'error');
      setAlertSearchDone(true);
    } finally {
      setLoadingAlerts(false);
    }
  }, [plate, showToast]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSelectAlert = (alert: MaintenanceAlert) => {
    setSelectedAlert((prev) => (prev?.id === alert.id ? null : alert));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid || !selectedAlert) return;
    setSubmitting(true);

    try {
      const dto: CreateMaintenanceDto = {
        plate: plate.trim().toUpperCase(),
        alertId: selectedAlert.id,
        serviceType: selectedAlert.ruleName ?? selectedAlert.ruleId,
        description: formData.description.trim() || null,
        cost: formData.cost !== '' ? parseFloat(formData.cost) : null,
        provider: formData.provider.trim() || null,
        performedAt: `${formData.performedAt}T00:00:00`,
        mileageAtService: parseInt(formData.mileageAtService, 10),
        recordedBy: formData.recordedBy.trim(),
      };

      await maintenanceService.register(dto);
      showToast('Servicio registrado correctamente', 'success');

      setPlate('');
      setAlerts([]);
      setSelectedAlert(null);
      setAlertSearchDone(false);
      setFormData(initialFormData);
    } catch (error: unknown) {
      const e = error as { status?: number; message?: string; errors?: string[] };
      if (e.status === 0) {
        showToast('Sin conexión con el servidor', 'error');
      } else if (e.status === 400 && e.errors) {
        showToast(`Error de validación: ${e.errors.join(', ')}`, 'error');
      } else if (e.status === 404) {
        showToast('Alerta o vehículo no encontrado.', 'error');
      } else {
        showToast(e.message || 'Error al registrar el servicio', 'error');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return {
    plate,
    alerts,
    loadingAlerts,
    selectedAlert,
    alertSearchDone,
    formData,
    submitting,
    isFormValid,
    setPlate,
    handlePlateChange,
    handleSearchAlerts,
    handleChange,
    handleSelectAlert,
    handleSubmit,
  };
}