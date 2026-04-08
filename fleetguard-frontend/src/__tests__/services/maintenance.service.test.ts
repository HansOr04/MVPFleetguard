import { describe, it, expect, vi } from 'vitest';
import { maintenanceService } from '@/services/maintenance.service';
import * as apiModule from '@/lib/api';

describe('maintenanceService', () => {
  it('calls maintenanceApi.register with correct data', async () => {
    const spy = vi.spyOn(apiModule.maintenanceApi, 'register').mockResolvedValueOnce({
      id: 'record-1',
      plate: 'ABC-1234',
      alertId: 'alert-1',
      ruleId: 'rule-1',
      serviceType: 'Cambio de aceite',
      description: null,
      cost: null,
      provider: null,
      performedAt: '2026-04-08T00:00:00',
      mileageAtService: 5000,
      recordedBy: 'Juan',
      createdAt: '2026-04-08T00:00:00Z',
    });
    const dto = {
      plate: 'ABC-1234',
      alertId: 'alert-1',
      serviceType: 'Cambio de aceite',
      description: null,
      cost: null,
      provider: null,
      performedAt: '2026-04-08T00:00:00',
      mileageAtService: 5000,
      recordedBy: 'Juan',
    };
    await maintenanceService.register(dto);
    expect(spy).toHaveBeenCalledWith(dto);
  });
});