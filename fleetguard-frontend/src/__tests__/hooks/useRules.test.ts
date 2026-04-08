import { renderHook } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { useRules } from '@/hooks/useRules'

describe('useRules', () => {
  it('returns empty rules array', () => {
    const { result } = renderHook(() => useRules())
    expect(result.current.rules).toEqual([])
  })

  it('returns loading as false', () => {
    const { result } = renderHook(() => useRules())
    expect(result.current.loading).toBe(false)
  })

  it('returns error as null', () => {
    const { result } = renderHook(() => useRules())
    expect(result.current.error).toBeNull()
  })

  it('refetch returns a resolved promise', async () => {
    const { result } = renderHook(() => useRules())
    await expect(result.current.refetch()).resolves.toBeUndefined()
  })
})