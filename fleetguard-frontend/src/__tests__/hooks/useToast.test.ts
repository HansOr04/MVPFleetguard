import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useToast } from '@/hooks/useToast';

describe('useToast', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('starts with toast not visible', () => {
    const { result } = renderHook(() => useToast());
    expect(result.current.toast.visible).toBe(false);
  });

  it('shows toast with correct message and type', () => {
    const { result } = renderHook(() => useToast());
    act(() => {
      result.current.showToast('Operación exitosa', 'success');
    });
    expect(result.current.toast.visible).toBe(true);
    expect(result.current.toast.message).toBe('Operación exitosa');
    expect(result.current.toast.type).toBe('success');
  });

  it('hides toast after 4 seconds', () => {
    const { result } = renderHook(() => useToast());
    act(() => {
      result.current.showToast('Error', 'error');
    });
    expect(result.current.toast.visible).toBe(true);
    act(() => {
      vi.advanceTimersByTime(4000);
    });
    expect(result.current.toast.visible).toBe(false);
  });

  it('shows error toast correctly', () => {
    const { result } = renderHook(() => useToast());
    act(() => {
      result.current.showToast('Algo salió mal', 'error');
    });
    expect(result.current.toast.type).toBe('error');
    expect(result.current.toast.message).toBe('Algo salió mal');
  });
});