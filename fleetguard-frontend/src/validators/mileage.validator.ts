export const mileageValidator = {
  mileageValue: (value: number | ''): string | null => {
    if (value === '') return 'El kilometraje es obligatorio';
    if (value < 0) return 'El kilometraje no puede ser negativo';
    if (value === 0) return 'El kilometraje debe ser mayor a cero';
    return null;
  },

  recordedBy: (value: string): string | null => {
    if (!value.trim()) return 'El nombre de quien registra es obligatorio';
    return null;
  },

  isFormValid: (plate: string, mileageValue: number | '', recordedBy: string): boolean => {
    return (
      plate.trim().length > 0 &&
      mileageValue !== '' &&
      (mileageValue as number) > 0 &&
      recordedBy.trim().length > 0
    );
  },
};