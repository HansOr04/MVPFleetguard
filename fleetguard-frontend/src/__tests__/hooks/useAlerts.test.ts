import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useAlerts } from '@/hooks/useAlerts';
import * as alertServiceModule from '@/services/alert.service';
import { MaintenanceAlert } from '@/types';

const mockAlerts: MaintenanceAlert[] = [
  {
    id: 'alert-001',
    vehicleId: 'vehicle-001',
    vehicleTypeId: 'vtype-001',
    ruleId: 'rule-001',
    status: 'PENDING',
    triggeredAt: '2026-01-01T00:00:00Z',
    dueAtKm: 50000,
  },
];

describe('useAlerts', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('starts loading and fetches alerts successfully', async () => {
    vi.spyOn(alertServiceModule.alertService, 'getAll').mockResolvedValueOnce(mockAlerts);
    const { result } = renderHook(() => useAlerts());
    expect(result.current.loading).toBe(true);
    await waitFor(() => expect(result.current.loading).toBe(false));
    expect(result.current.alerts).toEqual(mockAlerts);
    expect(result.current.error).toBeNull();
  });

  it('sets error when fetch fails', async () => {
    vi.spyOn(alertServiceModule.alertService, 'getAll').mockRejectedValueOnce({
      message: 'Error de red',
    });
    const { result } = renderHook(() => useAlerts());
    await waitFor(() => expect(result.current.loading).toBe(false));
    expect(result.current.error).toBe('Error de red');
    expect(result.current.alerts).toEqual([]);
  });

  it('passes filter to alertService.getAll', async () => {
    const spy = vi.spyOn(alertServiceModule.alertService, 'getAll').mockResolvedValueOnce([]);
    renderHook(() => useAlerts('PENDING'));
    await waitFor(() => {
      expect(spy).toHaveBeenCalledWith('PENDING');
    });
  });

  it('passes undefined when filter is TODOS', async () => {
    const spy = vi.spyOn(alertServiceModule.alertService, 'getAll').mockResolvedValueOnce([]);
    renderHook(() => useAlerts('TODOS'));
    await waitFor(() => {
      expect(spy).toHaveBeenCalledWith(undefined);
    });
  });
});