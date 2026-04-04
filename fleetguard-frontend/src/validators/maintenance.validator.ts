export const maintenanceValidator = {
  plate: (value: string): string | null => {
    if (!value.trim()) return 'La placa es obligatoria';
    return null;
  },

  recordedBy: (value: string): string | null => {
    if (!value.trim()) return 'El nombre de quien registra es obligatorio';
    return null;
  },

  performedAt: (value: string): string | null => {
    if (!value.trim()) return 'La fecha del servicio es obligatoria';
    return null;
  },

  mileageAtService: (value: string | number): string | null => {
    if (value === '' || value === null || value === undefined) return 'El kilometraje es obligatorio';
    if (Number(value) <= 0) return 'El kilometraje debe ser mayor a cero';
    return null;
  },

  isFormValid: (
    plate: string,
    selectedAlertId: string | null,
    recordedBy: string,
    performedAt: string,
    mileageAtService: string | number
  ): boolean => {
    return (
      plate.trim().length > 0 &&
      selectedAlertId !== null &&
      recordedBy.trim().length > 0 &&
      performedAt.trim().length > 0 &&
      mileageAtService !== '' &&
      Number(mileageAtService) > 0
    );
  },
};