import { renderHook, act } from '@testing-library/react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useMaintenanceForm } from '@/hooks/useMaintenanceForm'

const mockRegister = vi.fn()
const mockGetByPlate = vi.fn()

vi.mock('@/services/maintenance.service', () => ({
  maintenanceService: {
    register: (...args: unknown[]) => mockRegister(...args),
  },
}))

vi.mock('@/services/alert.service', () => ({
  alertService: {
    getByPlate: (...args: unknown[]) => mockGetByPlate(...args),
  },
}))

const mockAlert = {
  id: 'alert-001',
  vehicleId: 'v-001',
  vehicleTypeId: 'vt-001',
  ruleId: 'rule-001',
  ruleName: 'Cambio de aceite',
  status: 'PENDING' as const,
  triggeredAt: '2026-01-01T00:00:00Z',
  dueAtKm: 50000,
}

const mockRecord = {
  id: 'rec-001',
  plate: 'ABC-1234',
  alertId: 'alert-001',
  ruleId: 'rule-001',
  serviceType: 'Cambio de aceite',
  description: null,
  cost: null,
  provider: null,
  performedAt: '2026-01-01T00:00:00Z',
  mileageAtService: 50000,
  recordedBy: 'Juan',
  createdAt: '2026-01-01T00:00:00Z',
}

describe('useMaintenanceForm', () => {
  const showToast = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('initializes with empty state', () => {
    const { result } = renderHook(() => useMaintenanceForm(showToast))

    expect(result.current.plate).toBe('')
    expect(result.current.alerts).toEqual([])
    expect(result.current.selectedAlert).toBeNull()
    expect(result.current.alertSearchDone).toBe(false)
    expect(result.current.isFormValid).toBe(false)
  })

  it('initializes with provided plate', () => {
    const { result } = renderHook(() => useMaintenanceForm(showToast, 'ABC-1234'))
    expect(result.current.plate).toBe('ABC-1234')
  })

  it('handles plate change and resets state', () => {
    const { result } = renderHook(() => useMaintenanceForm(showToast))

    act(() => {
      result.current.handlePlateChange({
        target: { value: 'abc-1234' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    expect(result.current.plate).toBe('ABC-1234')
    expect(result.current.alerts).toEqual([])
    expect(result.current.selectedAlert).toBeNull()
    expect(result.current.alertSearchDone).toBe(false)
  })

  it('searches alerts by plate successfully', async () => {
    mockGetByPlate.mockResolvedValue([mockAlert])

    const { result } = renderHook(() => useMaintenanceForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
    })

    await act(async () => {
      await result.current.handleSearchAlerts()
    })

    expect(mockGetByPlate).toHaveBeenCalledWith('ABC-1234')
    expect(result.current.alerts).toEqual([mockAlert])
    expect(result.current.alertSearchDone).toBe(true)
  })

  it('handles error when searching alerts', async () => {
    mockGetByPlate.mockRejectedValue(new Error('Network error'))

    const { result } = renderHook(() => useMaintenanceForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
    })

    await act(async () => {
      await result.current.handleSearchAlerts()
    })

    expect(showToast).toHaveBeenCalledWith('Error al buscar alertas del vehículo', 'error')
    expect(result.current.alertSearchDone).toBe(true)
  })

  it('does not search when plate is empty', async () => {
    const { result } = renderHook(() => useMaintenanceForm(showToast))

    await act(async () => {
      await result.current.handleSearchAlerts()
    })

    expect(mockGetByPlate).not.toHaveBeenCalled()
  })

  it('selects and deselects an alert', () => {
    const { result } = renderHook(() => useMaintenanceForm(showToast))

    act(() => {
      result.current.handleSelectAlert(mockAlert)
    })
    expect(result.current.selectedAlert).toEqual(mockAlert)

    act(() => {
      result.current.handleSelectAlert(mockAlert)
    })
    expect(result.current.selectedAlert).toBeNull()
  })

  it('handles form field changes', () => {
    const { result } = renderHook(() => useMaintenanceForm(showToast))

    act(() => {
      result.current.handleChange({
        target: { name: 'recordedBy', value: 'Juan Pérez' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    expect(result.current.formData.recordedBy).toBe('Juan Pérez')
  })

  it('validates form correctly', () => {
    const { result } = renderHook(() => useMaintenanceForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
      result.current.handleSelectAlert(mockAlert)
      result.current.handleChange({
        target: { name: 'recordedBy', value: 'Juan' },
      } as React.ChangeEvent<HTMLInputElement>)
      result.current.handleChange({
        target: { name: 'mileageAtService', value: '50000' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    expect(result.current.isFormValid).toBe(true)
  })

  it('submits maintenance record successfully', async () => {
    mockRegister.mockResolvedValue(mockRecord)

    const { result } = renderHook(() => useMaintenanceForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
      result.current.handleSelectAlert(mockAlert)
      result.current.handleChange({
        target: { name: 'recordedBy', value: 'Juan' },
      } as React.ChangeEvent<HTMLInputElement>)
      result.current.handleChange({
        target: { name: 'mileageAtService', value: '50000' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(mockRegister).toHaveBeenCalled()
    expect(showToast).toHaveBeenCalledWith('Servicio registrado correctamente', 'success')
  })

  it('handles 404 error on submit', async () => {
    mockRegister.mockRejectedValue({ status: 404, message: 'Not found' })

    const { result } = renderHook(() => useMaintenanceForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
      result.current.handleSelectAlert(mockAlert)
      result.current.handleChange({
        target: { name: 'recordedBy', value: 'Juan' },
      } as React.ChangeEvent<HTMLInputElement>)
      result.current.handleChange({
        target: { name: 'mileageAtService', value: '50000' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(showToast).toHaveBeenCalledWith('Alerta o vehículo no encontrado.', 'error')
  })

  it('handles connection error on submit', async () => {
    mockRegister.mockRejectedValue({ status: 0 })

    const { result } = renderHook(() => useMaintenanceForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
      result.current.handleSelectAlert(mockAlert)
      result.current.handleChange({
        target: { name: 'recordedBy', value: 'Juan' },
      } as React.ChangeEvent<HTMLInputElement>)
      result.current.handleChange({
        target: { name: 'mileageAtService', value: '50000' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(showToast).toHaveBeenCalledWith('Sin conexión con el servidor', 'error')
  })

  it('does not submit when form is invalid', async () => {
    const { result } = renderHook(() => useMaintenanceForm(showToast))

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(mockRegister).not.toHaveBeenCalled()
  })

  it('resets form after successful submission', async () => {
    mockRegister.mockResolvedValue(mockRecord)

    const { result } = renderHook(() => useMaintenanceForm(showToast))

    act(() => {
      result.current.setPlate('ABC-1234')
      result.current.handleSelectAlert(mockAlert)
      result.current.handleChange({
        target: { name: 'recordedBy', value: 'Juan' },
      } as React.ChangeEvent<HTMLInputElement>)
      result.current.handleChange({
        target: { name: 'mileageAtService', value: '50000' },
      } as React.ChangeEvent<HTMLInputElement>)
    })

    await act(async () => {
      await result.current.handleSubmit({ preventDefault: vi.fn() } as unknown as React.FormEvent)
    })

    expect(result.current.plate).toBe('')
    expect(result.current.selectedAlert).toBeNull()
    expect(result.current.alertSearchDone).toBe(false)
  })
})