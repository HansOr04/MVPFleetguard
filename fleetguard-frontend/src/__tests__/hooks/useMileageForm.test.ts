import { renderHook, act } from '@testing-library/react'
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { useMileageForm } from '@/hooks/useMileageForm'

const mockUpdateMileage = vi.fn()
const mockGetByPlate = vi.fn()

vi.mock('@/services/vehicle.service', () => ({
  vehicleService: {
    updateMileage: (...args: unknown[]) => mockUpdateMileage(...args),
  },
}))

vi.mock('@/services/alert.service', () => ({
  alertService: {
    getByPlate: (...args: unknown[]) => mockGetByPlate(...args),
  },
}))

const mockMileageLog = {
  mileageLogId: 'log-001',
  vehicleId: 'v-001',
  plate: 'ABC-1234',
  previousMileage: 40000,
  mileageValue: 45000,
  kmTraveled: 5000,
  currentMileage: 45000,
  recordedBy: 'Juan',
  recordedAt: '2026-01-01T00:00:00Z',
  excessiveIncrement: false,
  alertId: null,
}

describe('useMileageForm', () => {
  const showToast = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('initializes with empty state', () => {
    const { result } = renderHook(() => useMileageForm(showToast))

    expect(result.current.formState.plate).toBe('')
    expect(result.current.formState.newMileage).toBe('')
    expect(result.current.formState.recordedBy).toBe('')
    expect(result.current.lastResult).toBeNull()
    expect(result.current.isFormValid).toBe(false)
  })

  it('validates form correctly when all fields are filled', () => {
    const { result } = renderHook(() => useMileageForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
      result.current.setNewMileage(45000)
      result.current.setRecordedBy('Juan')
    })

    expect(result.current.isFormValid).toBe(true)
  })

  it('detects negative mileage', () => {
    const { result } = renderHook(() => useMileageForm(showToast))

    act(() => {
      result.current.setNewMileage(-100)
    })

    expect(result.current.isNegative).toBe(true)
  })

  it('submits mileage update successfully', async () => {
    mockUpdateMileage.mockResolvedValue(mockMileageLog)
    mockGetByPlate.mockResolvedValue([])

    const { result } = renderHook(() => useMileageForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
      result.current.setNewMileage(45000)
      result.current.setRecordedBy('Juan')
    })

    // Run handleSubmit and advance fake timers concurrently so the
    // internal setTimeout(resolve, 1500) can settle within the same act.
    await act(async () => {
      const submitPromise = result.current.handleSubmit({
        preventDefault: vi.fn(),
      } as unknown as React.FormEvent)
      await vi.runAllTimersAsync()
      await submitPromise
    })

    expect(mockUpdateMileage).toHaveBeenCalledWith('ABC-1234', {
      mileageValue: 45000,
      recordedBy: 'Juan',
    })
    expect(showToast).toHaveBeenCalledWith('Odómetro actualizado correctamente', 'success')
    expect(result.current.lastResult).toEqual(mockMileageLog)
  })

  it('handles vehicle not found (404)', async () => {
    mockUpdateMileage.mockRejectedValue({ status: 404, message: 'Not found' })

    const { result } = renderHook(() => useMileageForm(showToast))

    act(() => {
      result.current.setPlate('XXX-0000')
      result.current.setNewMileage(1000)
      result.current.setRecordedBy('Juan')
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(showToast).toHaveBeenCalledWith('Vehículo no encontrado. Verifica la placa.', 'error')
  })

  it('handles connection error (status 0)', async () => {
    mockUpdateMileage.mockRejectedValue({ status: 0, message: 'Sin conexión' })

    const { result } = renderHook(() => useMileageForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
      result.current.setNewMileage(1000)
      result.current.setRecordedBy('Juan')
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(showToast).toHaveBeenCalledWith('Sin conexión con el servidor', 'error')
  })

  it('handles validation errors (400 with errors array)', async () => {
    mockUpdateMileage.mockRejectedValue({
      status: 400,
      message: 'Validation error',
      errors: ['mileage: invalid'],
    })

    const { result } = renderHook(() => useMileageForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
      result.current.setNewMileage(1000)
      result.current.setRecordedBy('Juan')
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(showToast).toHaveBeenCalledWith('Error de validación: mileage: invalid', 'error')
  })

  it('shows generated alerts after mileage update', async () => {
    const mockAlerts = [
      {
        id: 'alert-001',
        vehicleId: 'v-001',
        vehicleTypeId: 'vt-001',
        ruleId: 'rule-001',
        status: 'PENDING' as const,
        triggeredAt: '2026-01-01T00:00:00Z',
        dueAtKm: 50000,
      },
    ]
    mockUpdateMileage.mockResolvedValue(mockMileageLog)
    mockGetByPlate.mockResolvedValue(mockAlerts)

    const { result } = renderHook(() => useMileageForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
      result.current.setNewMileage(45000)
      result.current.setRecordedBy('Juan')
    })

    await act(async () => {
      const submitPromise = result.current.handleSubmit({
        preventDefault: vi.fn(),
      } as unknown as React.FormEvent)
      await vi.runAllTimersAsync()
      await submitPromise
    })

    expect(result.current.generatedAlerts).toEqual(mockAlerts)
  })

  it('does not submit when form is invalid', async () => {
    const { result } = renderHook(() => useMileageForm(showToast))

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(mockUpdateMileage).not.toHaveBeenCalled()
  })
})