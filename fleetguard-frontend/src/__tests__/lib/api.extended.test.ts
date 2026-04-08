import { describe, it, expect, vi, beforeEach } from 'vitest'
import { vehicleApi, rulesApi, maintenanceApi, alertsApi } from '@/lib/api'

const mockFetch = vi.fn()
global.fetch = mockFetch

describe('api - vehicleApi extended', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('updateMileage returns mileage log on success', async () => {
    const mockLog = {
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

    mockFetch.mockResolvedValue({
      ok: true,
      status: 200,
      json: () => Promise.resolve(mockLog),
    })

    const result = await vehicleApi.updateMileage('ABC-1234', {
      mileageValue: 45000,
      recordedBy: 'Juan',
    })

    expect(result).toEqual(mockLog)
    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/vehicles/ABC-1234/mileage'),
      expect.objectContaining({ method: 'POST' }),
    )
  })

  it('updateMileage falls back to mock on connection error', async () => {
    mockFetch.mockRejectedValue(new Error('Network error'))

    const result = await vehicleApi.updateMileage('ABC-1234', {
      mileageValue: 45000,
      recordedBy: 'Juan',
    })

    expect(result).toBeDefined()
    expect(result.plate).toBe('ABC-1234')
  })
})

describe('api - rulesApi extended', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('associateVehicleType succeeds', async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      status: 204,
      json: () => Promise.resolve(null),
    })

    // The function returns Promise<void>, so it resolves to undefined
    await expect(
      rulesApi.associateVehicleType('rule-001', { vehicleTypeId: 'vt-001' }),
    ).resolves.toBeUndefined()
  })

  it('associateVehicleType falls back silently on connection error', async () => {
    mockFetch.mockRejectedValue(new Error('Network error'))

    await expect(
      rulesApi.associateVehicleType('rule-001', { vehicleTypeId: 'vt-001' }),
    ).resolves.toBeUndefined()
  })

  it('create rule falls back to mock on connection error', async () => {
    mockFetch.mockRejectedValue(new Error('Network error'))

    const result = await rulesApi.create({
      name: 'Cambio de aceite',
      maintenanceType: 'PREVENTIVE',
      intervalKm: 5000,
      warningThresholdKm: 500,
    })

    expect(result).toBeDefined()
    expect(result.name).toBe('Cambio de aceite')
    expect(result.id).toBeDefined()
  })
})

describe('api - maintenanceApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('register maintenance record succeeds', async () => {
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

    mockFetch.mockResolvedValue({
      ok: true,
      status: 200,
      json: () => Promise.resolve(mockRecord),
    })

    const result = await maintenanceApi.register({
      plate: 'ABC-1234',
      alertId: 'alert-001',
      serviceType: 'Cambio de aceite',
      description: null,
      cost: null,
      provider: null,
      performedAt: '2026-01-01T00:00:00Z',
      mileageAtService: 50000,
      recordedBy: 'Juan',
    })

    expect(result).toEqual(mockRecord)
  })

  it('register falls back to mock on connection error', async () => {
    mockFetch.mockRejectedValue(new Error('Network error'))

    const result = await maintenanceApi.register({
      plate: 'ABC-1234',
      alertId: 'alert-001',
      serviceType: 'Cambio de aceite',
      description: null,
      cost: null,
      provider: null,
      performedAt: null,
      mileageAtService: 50000,
      recordedBy: 'Juan',
    })

    expect(result).toBeDefined()
    expect(result.plate).toBe('ABC-1234')
  })

  it('throws non-connection errors', async () => {
    mockFetch.mockResolvedValue({
      ok: false,
      status: 400,
      json: () => Promise.resolve({ message: 'Bad request' }),
    })

    await expect(
      maintenanceApi.register({
        plate: 'ABC-1234',
        alertId: 'alert-001',
        serviceType: 'Cambio de aceite',
        description: null,
        cost: null,
        provider: null,
        performedAt: null,
        mileageAtService: 50000,
        recordedBy: 'Juan',
      }),
    ).rejects.toMatchObject({ status: 400 })
  })
})

describe('api - alertsApi extended', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('getAll with status filter builds correct URL', async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      status: 200,
      json: () => Promise.resolve([]),
    })

    await alertsApi.getAll('PENDING')

    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining('status=PENDING'),
      expect.any(Object),
    )
  })

  it('getAll without status filter uses base URL', async () => {
    mockFetch.mockResolvedValue({
      ok: true,
      status: 200,
      json: () => Promise.resolve([]),
    })

    await alertsApi.getAll()

    expect(mockFetch).toHaveBeenCalledWith(
      expect.not.stringContaining('status='),
      expect.any(Object),
    )
  })

  it('getByVehicleId filters alerts by vehicleId', async () => {
    const allAlerts = [
      {
        id: 'a1',
        vehicleId: 'v-001',
        vehicleTypeId: 'vt-1',
        ruleId: 'r-1',
        status: 'PENDING',
        triggeredAt: '2026-01-01T00:00:00Z',
        dueAtKm: 50000,
      },
      {
        id: 'a2',
        vehicleId: 'v-002',
        vehicleTypeId: 'vt-1',
        ruleId: 'r-1',
        status: 'PENDING',
        triggeredAt: '2026-01-01T00:00:00Z',
        dueAtKm: 50000,
      },
    ]

    mockFetch.mockResolvedValue({
      ok: true,
      status: 200,
      json: () => Promise.resolve(allAlerts),
    })

    const result = await alertsApi.getByVehicleId('v-001')
    expect(result).toHaveLength(1)
    expect(result[0].vehicleId).toBe('v-001')
  })

  it('getAll falls back to mock on connection error', async () => {
    mockFetch.mockRejectedValue(new Error('Network error'))

    const result = await alertsApi.getAll()
    expect(Array.isArray(result)).toBe(true)
  })

  it('getByPlate falls back to mock on connection error', async () => {
    mockFetch.mockRejectedValue(new Error('Network error'))

    const result = await alertsApi.getByPlate('ABC-1234')
    expect(Array.isArray(result)).toBe(true)
  })
})