import { useState, useEffect, useCallback } from 'react';
import { rulesApi } from '@/lib/api';
import { MaintenanceRule } from '@/types';

export function useRules() {
  const [rules, setRules] = useState<MaintenanceRule[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchRules = useCallback(async () => {
    try {
      setLoading(true);
      const data = await rulesApi.getAll();
      setRules(data);
      setError(null);
    } catch (err: unknown) {
      const e = err as { message?: string };
      setError(e.message || 'Error al cargar reglas');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchRules();
  }, [fetchRules]);

  return { rules, loading, error, refetch: fetchRules };
}