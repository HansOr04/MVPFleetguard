export const vehicleValidator = {
  plate: (value: string): string | null => {
    if (!value.trim()) return 'La placa es obligatoria';
    return null;
  },

  vin: (value: string): string | null => {
    if (!value.trim()) return 'El VIN es obligatorio';
    if (value.trim().length !== 17) return 'El VIN debe tener exactamente 17 caracteres';
    return null;
  },

  brand: (value: string): string | null => {
    if (!value.trim()) return 'La marca es obligatoria';
    return null;
  },

  model: (value: string): string | null => {
    if (!value.trim()) return 'El modelo es obligatorio';
    return null;
  },

  year: (value: number): string | null => {
    if (!value || value <= 0) return 'El año es obligatorio';
    return null;
  },

  fuelType: (value: string): string | null => {
    if (!value.trim()) return 'El tipo de combustible es obligatorio';
    return null;
  },

  vehicleTypeId: (value: string): string | null => {
    if (!value.trim()) return 'El tipo de vehículo es obligatorio';
    return null;
  },
};