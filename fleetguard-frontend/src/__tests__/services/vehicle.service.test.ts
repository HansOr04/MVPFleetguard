import { describe, it, expect, vi } from 'vitest';
import { vehicleService } from '@/services/vehicle.service';
import * as apiModule from '@/lib/api';

describe('vehicleService', () => {
  it('calls vehicleApi.register with correct data', async () => {
    const spy = vi.spyOn(apiModule.vehicleApi, 'register').mockResolvedValueOnce({
      id: '1',
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
    const dto = {
      plate: 'ABC-1234',
      vin: '1HGBH41JXMN109186',
      brand: 'Toyota',
      model: 'Hilux',
      year: 2023,
      fuelType: 'Diésel',
      vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5311',
    };
    await vehicleService.register(dto);
    expect(spy).toHaveBeenCalledWith(dto);
  });

  it('calls vehicleApi.updateMileage with plate in uppercase', async () => {
    const spy = vi.spyOn(apiModule.vehicleApi, 'updateMileage').mockResolvedValueOnce({
      mileageLogId: '1',
      vehicleId: 'v1',
      plate: 'ABC-1234',
      previousMileage: 1000,
      mileageValue: 2000,
      kmTraveled: 1000,
      currentMileage: 2000,
      recordedBy: 'Juan',
      recordedAt: '2026-01-01T00:00:00Z',
      excessiveIncrement: false,
      alertId: null,
    });
    await vehicleService.updateMileage('abc-1234', { mileageValue: 2000, recordedBy: 'Juan' });
    expect(spy).toHaveBeenCalledWith('ABC-1234', { mileageValue: 2000, recordedBy: 'Juan' });
  });
});