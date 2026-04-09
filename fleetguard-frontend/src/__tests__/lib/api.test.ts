import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { vehicleApi, alertsApi, isDemoMode } from '@/lib/api';

const mockFetch = vi.fn();

beforeEach(() => {
  vi.stubGlobal('fetch', mockFetch);
});

afterEach(() => {
  vi.unstubAllGlobals();
  mockFetch.mockReset();
});

describe('vehicleApi.register', () => {
  it('returns vehicle on 200', async () => {
    const vehicle = { id: '1', plate: 'ABC-1234' };
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => vehicle,
    });
    const result = await vehicleApi.register({
      plate: 'ABC-1234',
      vin: '1HGBH41JXMN109186',
      brand: 'Toyota',
      model: 'Hilux',
      year: 2023,
      fuelType: 'Diésel',
      vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5311',
    });
    expect(result).toEqual(vehicle);
  });

  it('throws ApiError with status and message on 409', async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 409,
      json: async () => ({ message: 'La placa ya está registrada', field: 'plate' }),
    });
    await expect(
      vehicleApi.register({
        plate: 'ABC-1234',
        vin: '1HGBH41JXMN109186',
        brand: 'Toyota',
        model: 'Hilux',
        year: 2023,
        fuelType: 'Diésel',
        vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5311',
      }),
    ).rejects.toMatchObject({ status: 409, message: 'La placa ya está registrada' });
  });

  it('uses demo mode when fetch throws network error and plate not in mocks', async () => {
    mockFetch.mockRejectedValueOnce(new Error('Network error'));
    const result = await vehicleApi.register({
      plate: 'NEW-9999',
      vin: '00000000000000000',
      brand: 'Honda',
      model: 'Civic',
      year: 2022,
      fuelType: 'Gasolina',
      vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5311',
    });
    expect(result.plate).toBe('NEW-9999');
    expect(isDemoMode()).toBe(true);
  });
});

describe('alertsApi.getAll', () => {
  it('returns alerts on 200', async () => {
    const alerts = [{ id: 'alert-1', status: 'PENDING' }];
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => alerts,
    });
    const result = await alertsApi.getAll();
    expect(result).toEqual(alerts);
  });

  it('appends status query param when provided', async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => [],
    });
    await alertsApi.getAll('PENDING');
    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining('status=PENDING'),
      expect.any(Object),
    );
  });
});