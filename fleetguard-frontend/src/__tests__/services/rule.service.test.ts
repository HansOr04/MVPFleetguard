import { describe, it, expect, vi } from 'vitest';
import { ruleService } from '@/services/rule.service';
import * as apiModule from '@/lib/api';

describe('ruleService', () => {
  it('calls rulesApi.create with correct data', async () => {
    const spy = vi.spyOn(apiModule.rulesApi, 'create').mockResolvedValueOnce({
      id: 'rule-1',
      name: 'Cambio de aceite',
      maintenanceType: 'PREVENTIVE',
      intervalKm: 5000,
      warningThresholdKm: 500,
      status: 'ACTIVE',
      createdAt: '2026-01-01T00:00:00Z',
      updatedAt: '2026-01-01T00:00:00Z',
    });
    const dto = { name: 'Cambio de aceite', maintenanceType: 'PREVENTIVE', intervalKm: 5000, warningThresholdKm: 500 };
    await ruleService.create(dto);
    expect(spy).toHaveBeenCalledWith(dto);
  });

  it('calls rulesApi.associateVehicleType with correct params', async () => {
    const spy = vi.spyOn(apiModule.rulesApi, 'associateVehicleType').mockResolvedValueOnce(undefined);
    await ruleService.associateVehicleType('rule-1', { vehicleTypeId: 'vtype-1' });
    expect(spy).toHaveBeenCalledWith('rule-1', { vehicleTypeId: 'vtype-1' });
  });
});