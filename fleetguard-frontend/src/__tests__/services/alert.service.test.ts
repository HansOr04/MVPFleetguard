import { describe, it, expect, vi } from 'vitest';
import { alertService } from '@/services/alert.service';
import * as apiModule from '@/lib/api';

describe('alertService', () => {
  it('calls alertsApi.getAll without filter', async () => {
    const spy = vi.spyOn(apiModule.alertsApi, 'getAll').mockResolvedValueOnce([]);
    await alertService.getAll();
    expect(spy).toHaveBeenCalledWith(undefined);
  });

  it('calls alertsApi.getAll with status filter', async () => {
    const spy = vi.spyOn(apiModule.alertsApi, 'getAll').mockResolvedValueOnce([]);
    await alertService.getAll('PENDING');
    expect(spy).toHaveBeenCalledWith('PENDING');
  });

  it('calls alertsApi.getByPlate with uppercase plate', async () => {
    const spy = vi.spyOn(apiModule.alertsApi, 'getByPlate').mockResolvedValueOnce([]);
    await alertService.getByPlate('abc-1234');
    expect(spy).toHaveBeenCalledWith('ABC-1234');
  });

  it('calls alertsApi.getByVehicleId', async () => {
    const spy = vi.spyOn(apiModule.alertsApi, 'getByVehicleId').mockResolvedValueOnce([]);
    await alertService.getByVehicleId('vehicle-001');
    expect(spy).toHaveBeenCalledWith('vehicle-001');
  });
});