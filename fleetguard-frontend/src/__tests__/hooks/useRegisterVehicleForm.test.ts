import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useRegisterVehicleForm } from '@/hooks/useRegisterVehicleForm';
import * as vehicleServiceModule from '@/services/vehicle.service';

const showToast = vi.fn();

const validFormData = {
  plate: 'ABC-1234',
  vin: '1HGBH41JXMN109186',
  brand: 'Toyota',
  model: 'Hilux',
  year: 2023,
  fuelType: 'Diésel',
  vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5311',
};

function fillForm(result: ReturnType<typeof renderHook<ReturnType<typeof useRegisterVehicleForm>, unknown>>['result']) {
  Object.entries(validFormData).forEach(([name, value]) => {
    act(() => {
      result.current.handleChange({
        target: { name, value: String(value) },
      } as React.ChangeEvent<HTMLInputElement>);
    });
  });
}

describe('useRegisterVehicleForm', () => {
  beforeEach(() => {
    showToast.mockClear();
  });

  it('starts with empty form and invalid state', () => {
    const { result } = renderHook(() => useRegisterVehicleForm(showToast));
    expect(result.current.formData.plate).toBe('');
    expect(result.current.isFormValid).toBe(false);
  });

  it('isVinValid is true when vin has 17 chars', () => {
    const { result } = renderHook(() => useRegisterVehicleForm(showToast));
    act(() => {
      result.current.handleChange({
        target: { name: 'vin', value: '1HGBH41JXMN109186' },
      } as React.ChangeEvent<HTMLInputElement>);
    });
    expect(result.current.isVinValid).toBe(true);
  });

  it('isVinValid is false when vin has less than 17 chars', () => {
    const { result } = renderHook(() => useRegisterVehicleForm(showToast));
    act(() => {
      result.current.handleChange({
        target: { name: 'vin', value: 'SHORT' },
      } as React.ChangeEvent<HTMLInputElement>);
    });
    expect(result.current.isVinValid).toBe(false);
  });

  it('converts plate to uppercase', () => {
    const { result } = renderHook(() => useRegisterVehicleForm(showToast));
    act(() => {
      result.current.handleChange({
        target: { name: 'plate', value: 'abc-1234' },
      } as React.ChangeEvent<HTMLInputElement>);
    });
    expect(result.current.formData.plate).toBe('ABC-1234');
  });

  it('clears plateError on plate change', () => {
    const { result } = renderHook(() => useRegisterVehicleForm(showToast));
    fillForm(result);
    vi.spyOn(vehicleServiceModule.vehicleService, 'register').mockRejectedValueOnce({
      status: 409,
      message: 'La placa ya está registrada',
      field: 'plate',
    });
    act(() => {
      result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent);
    });
    act(() => {
      result.current.handleChange({
        target: { name: 'plate', value: 'XYZ-9999' },
      } as React.ChangeEvent<HTMLInputElement>);
    });
    expect(result.current.plateError).toBe('');
  });

  it('sets vinError on 409 with field=vin', async () => {
    const { result } = renderHook(() => useRegisterVehicleForm(showToast));
    fillForm(result);
    vi.spyOn(vehicleServiceModule.vehicleService, 'register').mockRejectedValueOnce({
      status: 409,
      message: 'El VIN ya está registrado',
      field: 'vin',
    });
    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent);
    });
    expect(result.current.vinError).toBe('Este VIN ya está registrado en el sistema');
    expect(result.current.plateError).toBe('');
  });

  it('sets plateError on 409 with field=plate', async () => {
    const { result } = renderHook(() => useRegisterVehicleForm(showToast));
    fillForm(result);
    vi.spyOn(vehicleServiceModule.vehicleService, 'register').mockRejectedValueOnce({
      status: 409,
      message: 'La placa ya está registrada',
      field: 'plate',
    });
    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent);
    });
    expect(result.current.plateError).toBe('Esta placa ya está registrada en el sistema');
    expect(result.current.vinError).toBe('');
  });

  it('sets vinError when 409 message contains "vin" and no field', async () => {
    const { result } = renderHook(() => useRegisterVehicleForm(showToast));
    fillForm(result);
    vi.spyOn(vehicleServiceModule.vehicleService, 'register').mockRejectedValueOnce({
      status: 409,
      message: 'El vin ya existe en el sistema',
    });
    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent);
    });
    expect(result.current.vinError).toBe('Este VIN ya está registrado en el sistema');
  });

  it('calls showToast on status 0', async () => {
    const { result } = renderHook(() => useRegisterVehicleForm(showToast));
    fillForm(result);
    vi.spyOn(vehicleServiceModule.vehicleService, 'register').mockRejectedValueOnce({
      status: 0,
      message: 'Sin conexión',
    });
    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent);
    });
    expect(showToast).toHaveBeenCalledWith('Sin conexión con el servidor', 'error');
  });

  it('resets form and shows success toast on successful submit', async () => {
    const { result } = renderHook(() => useRegisterVehicleForm(showToast));
    fillForm(result);
    vi.spyOn(vehicleServiceModule.vehicleService, 'register').mockResolvedValueOnce({
      id: 'new-id',
      plate: 'ABC-1234',
      brand: 'Toyota',
      model: 'Hilux',
      year: 2023,
      fuelType: 'Diésel',
      vin: '1HGBH41JXMN109186',
      status: 'ACTIVE',
      currentMileage: 0,
      vehicleTypeName: 'Pickup',
    });
    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent);
    });
    expect(showToast).toHaveBeenCalledWith('Vehículo registrado correctamente', 'success');
    expect(result.current.formData.plate).toBe('');
  });
});