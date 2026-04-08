import { renderHook, act } from '@testing-library/react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useRuleForm } from '@/hooks/useRuleForm'

const mockCreate = vi.fn()
const mockAssociateVehicleType = vi.fn()

vi.mock('@/services/rule.service', () => ({
  ruleService: {
    create: (...args: unknown[]) => mockCreate(...args),
    associateVehicleType: (...args: unknown[]) => mockAssociateVehicleType(...args),
  },
}))

const mockRule = {
  id: 'rule-001',
  name: 'Cambio de aceite motor liviano',
  maintenanceType: 'PREVENTIVE',
  intervalKm: 5000,
  warningThresholdKm: 500,
  status: 'ACTIVE',
  createdAt: '2026-01-01T00:00:00Z',
  updatedAt: '2026-01-01T00:00:00Z',
}

describe('useRuleForm', () => {
  const showToast = vi.fn()
  const onSuccess = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('initializes with empty state', () => {
    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    expect(result.current.nameInput).toBe('')
    expect(result.current.nameIsValid).toBe(false)
    expect(result.current.selectedVehicleTypes).toEqual([])
    expect(result.current.isFormValid).toBe(false)
    expect(result.current.submitting).toBe(false)
  })

  it('updates name input and validates against known rules', () => {
    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    act(() => {
      result.current.handleNameInputChange({
        target: { value: 'Cambio de aceite motor liviano' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    expect(result.current.nameInput).toBe('Cambio de aceite motor liviano')
    expect(result.current.nameIsValid).toBe(true)
    expect(result.current.formData.intervalKm).toBe(5000)
    expect(result.current.formData.warningThresholdKm).toBe(500)
  })

  it('marks name as invalid for unknown rule name', () => {
    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    act(() => {
      result.current.handleNameInputChange({
        target: { value: 'Regla inventada' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    expect(result.current.nameIsValid).toBe(false)
  })

  it('shows suggestions when typing', () => {
    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    act(() => {
      result.current.handleNameInputChange({
        target: { value: 'aceite' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    expect(result.current.showSuggestions).toBe(true)
    expect(result.current.filteredSuggestions.length).toBeGreaterThan(0)
  })

  it('selects a suggestion and fills form data', () => {
    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    const suggestion = {
      ruleType: 'Cambio de aceite motor liviano',
      intervalKm: 5000,
      thresholdKm: 500,
    }

    act(() => {
      result.current.handleSelectSuggestion(suggestion)
    })

    expect(result.current.nameInput).toBe('Cambio de aceite motor liviano')
    expect(result.current.nameIsValid).toBe(true)
    expect(result.current.formData.intervalKm).toBe(5000)
    expect(result.current.showSuggestions).toBe(false)
  })

  it('toggles vehicle type selection', () => {
    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    act(() => {
      result.current.handleCheckboxChange('vtype-001')
    })
    expect(result.current.selectedVehicleTypes).toContain('vtype-001')

    act(() => {
      result.current.handleCheckboxChange('vtype-001')
    })
    expect(result.current.selectedVehicleTypes).not.toContain('vtype-001')
  })

  it('validates form only when all required fields are filled', () => {
    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    act(() => {
      result.current.handleSelectSuggestion({
        ruleType: 'Cambio de aceite motor liviano',
        intervalKm: 5000,
        thresholdKm: 500,
      })
      result.current.handleChange({
        target: { name: 'maintenanceType', value: 'PREVENTIVE' },
      } as React.ChangeEvent<HTMLInputElement>)
      result.current.handleCheckboxChange('c1a1d13e-b3df-4fab-9584-890b852d5311')
    })

    expect(result.current.isFormValid).toBe(true)
  })

  it('submits rule and associates vehicle types successfully', async () => {
    mockCreate.mockResolvedValue(mockRule)
    mockAssociateVehicleType.mockResolvedValue(undefined)

    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    act(() => {
      result.current.handleSelectSuggestion({
        ruleType: 'Cambio de aceite motor liviano',
        intervalKm: 5000,
        thresholdKm: 500,
      })
      result.current.handleChange({
        target: { name: 'maintenanceType', value: 'PREVENTIVE' },
      } as React.ChangeEvent<HTMLInputElement>)
      result.current.handleCheckboxChange('c1a1d13e-b3df-4fab-9584-890b852d5311')
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(mockCreate).toHaveBeenCalled()
    expect(mockAssociateVehicleType).toHaveBeenCalledWith('rule-001', {
      vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5311',
    })
    expect(showToast).toHaveBeenCalledWith('Regla de mantenimiento creada exitosamente', 'success')
    expect(onSuccess).toHaveBeenCalled()
  })

  it('shows partial error when some associations fail', async () => {
    mockCreate.mockResolvedValue(mockRule)
    mockAssociateVehicleType.mockRejectedValue(new Error('Association failed'))

    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    act(() => {
      result.current.handleSelectSuggestion({
        ruleType: 'Cambio de aceite motor liviano',
        intervalKm: 5000,
        thresholdKm: 500,
      })
      result.current.handleChange({
        target: { name: 'maintenanceType', value: 'PREVENTIVE' },
      } as React.ChangeEvent<HTMLInputElement>)
      result.current.handleCheckboxChange('c1a1d13e-b3df-4fab-9584-890b852d5311')
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(showToast).toHaveBeenCalledWith(
      expect.stringContaining('asociación(es) fallaron'),
      'error',
    )
  })

  it('handles create rule error', async () => {
    mockCreate.mockRejectedValue({ message: 'Server error' })

    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    act(() => {
      result.current.handleSelectSuggestion({
        ruleType: 'Cambio de aceite motor liviano',
        intervalKm: 5000,
        thresholdKm: 500,
      })
      result.current.handleChange({
        target: { name: 'maintenanceType', value: 'PREVENTIVE' },
      } as React.ChangeEvent<HTMLInputElement>)
      result.current.handleCheckboxChange('c1a1d13e-b3df-4fab-9584-890b852d5311')
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(showToast).toHaveBeenCalledWith('Server error', 'error')
  })

  it('does not submit when form is invalid', async () => {
    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(mockCreate).not.toHaveBeenCalled()
  })

  it('resets form after successful submission', async () => {
    mockCreate.mockResolvedValue(mockRule)
    mockAssociateVehicleType.mockResolvedValue(undefined)

    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    act(() => {
      result.current.handleSelectSuggestion({
        ruleType: 'Cambio de aceite motor liviano',
        intervalKm: 5000,
        thresholdKm: 500,
      })
      result.current.handleChange({
        target: { name: 'maintenanceType', value: 'PREVENTIVE' },
      } as React.ChangeEvent<HTMLInputElement>)
      result.current.handleCheckboxChange('c1a1d13e-b3df-4fab-9584-890b852d5311')
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(result.current.nameInput).toBe('')
    expect(result.current.nameIsValid).toBe(false)
    expect(result.current.selectedVehicleTypes).toEqual([])
  })

  it('handles keyboard navigation in suggestions', () => {
    const { result } = renderHook(() => useRuleForm(showToast, onSuccess))

    act(() => {
      result.current.handleNameInputChange({
        target: { value: 'aceite' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    act(() => {
      result.current.handleNameKeyDown({
        key: 'ArrowDown',
        preventDefault: vi.fn(),
      } as unknown as React.KeyboardEvent<HTMLInputElement>)
    })
    expect(result.current.highlightedIndex).toBe(0)

    act(() => {
      result.current.handleNameKeyDown({
        key: 'Escape',
        preventDefault: vi.fn(),
      } as unknown as React.KeyboardEvent<HTMLInputElement>)
    })
    expect(result.current.showSuggestions).toBe(false)
  })
})