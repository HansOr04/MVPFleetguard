import { useState, useEffect, useCallback } from 'react';
import { alertsApi } from '@/lib/api';
import { MaintenanceAlert } from '@/types';

export function useAlerts(initialFilter?: string) {
  const [alerts, setAlerts] = useState<MaintenanceAlert[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<string | undefined>(initialFilter);

  const fetchAlerts = useCallback(async () => {
    try {
      setLoading(true);
      const data = await alertsApi.getAll(filter === 'TODOS' ? undefined : filter);
      setAlerts(data);
      setError(null);
    } catch (err: unknown) {
      const e = err as { message?: string };
      setError(e.message || 'Error al cargar alertas');
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    fetchAlerts();
  }, [fetchAlerts]);

  return { alerts, loading, error, refetch: fetchAlerts, setFilter, filter };
}