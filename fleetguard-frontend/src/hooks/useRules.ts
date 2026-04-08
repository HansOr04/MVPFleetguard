export function useRules() {
  const refetch = () => Promise.resolve();
  return { rules: [], loading: false, error: null, refetch };
}