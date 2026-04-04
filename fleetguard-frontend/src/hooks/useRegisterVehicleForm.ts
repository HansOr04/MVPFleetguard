import { useState } from 'react';
import { vehicleService } from '@/services/vehicle.service';
import { vehicleValidator } from '@/validators/vehicle.validator';
import { CreateVehicleDto } from '@/types';

const initialFormData: CreateVehicleDto = {
  plate: '',
  vin: '',
  brand: '',
  model: '',
  year: '' as unknown as number,
  fuelType: '',
  vehicleTypeId: '',
};

interface UseRegisterVehicleFormReturn {
  formData: CreateVehicleDto;
  loading: boolean;
  plateError: string;
  isVinValid: boolean;
  isFormValid: boolean;
  handleChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => void;
  handleSubmit: (e: React.FormEvent) => Promise<void>;
}

export function useRegisterVehicleForm(
  showToast: (message: string, type: 'success' | 'error') => void
): UseRegisterVehicleFormReturn {
  const [formData, setFormData] = useState<CreateVehicleDto>(initialFormData);
  const [loading, setLoading] = useState(false);
  const [plateError, setPlateError] = useState('');

  const isVinValid = formData.vin.length === 17;
  const isFormValid =
    isVinValid &&
    !vehicleValidator.plate(formData.plate) &&
    !vehicleValidator.brand(formData.brand) &&
    !vehicleValidator.model(formData.model) &&
    !vehicleValidator.year(formData.year as number) &&
    !vehicleValidator.fuelType(formData.fuelType) &&
    !vehicleValidator.vehicleTypeId(formData.vehicleTypeId);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    const parsedValue =
      name === 'plate'
        ? value.toUpperCase()
        : name === 'year'
        ? value === ''
          ? ''
          : Number(value)
        : value;

    setFormData((prev) => ({ ...prev, [name]: parsedValue }));
    if (name === 'plate') setPlateError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;
    setLoading(true);
    try {
      await vehicleService.register(formData);
      showToast('Vehículo registrado correctamente', 'success');
      setFormData(initialFormData);
      setPlateError('');
    } catch (error: unknown) {
      const e = error as { status?: number; message?: string; errors?: string[] };
      if (e.status === 409) {
        setPlateError('Esta placa ya está registrada');
        showToast('Error de registro: Placa duplicada', 'error');
      } else if (e.status === 0) {
        showToast('Sin conexión con el servidor', 'error');
      } else if (e.status === 400 && e.errors) {
        showToast(`Error de validación: ${e.errors.join(', ')}`, 'error');
      } else {
        showToast(e.message || 'Error al registrar vehículo', 'error');
      }
    } finally {
      setLoading(false);
    }
  };

  return {
    formData,
    loading,
    plateError,
    isVinValid,
    isFormValid,
    handleChange,
    handleSubmit,
  };
}