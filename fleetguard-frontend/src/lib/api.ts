import {
  Vehicle,
  MileageLog,
  MaintenanceRule,
  MaintenanceAlert,
  MaintenanceRecord,
  ApiError,
  CreateVehicleDto,
  UpdateMileageDto,
  CreateRuleDto,
  AssociateVehicleTypeDto,
  CreateMaintenanceDto,
} from '@/types';
import { mockVehicles, mockRules, mockAlerts, mockRecords } from './mocks/mockData';

const FLEET_URL = process.env.NEXT_PUBLIC_FLEET_SERVICE_URL;
const RULES_URL = process.env.NEXT_PUBLIC_RULES_SERVICE_URL;

let demoModeActive = false;

// ─── Helper genérico ─────────────────────────────────────────────────────────

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  try {
    const response = await fetch(url, {
      ...options,
      headers: { 'Content-Type': 'application/json', ...options?.headers },
    });

    if (response.ok) {
      demoModeActive = false;
      if (response.status === 204) return null as T;
      return response.json();
    }

    const errorBody = await response.json().catch(() => ({}));
    throw {
      status: response.status,
      message: errorBody.message || 'Error en la solicitud',
      errors: errorBody.errors,
    } as ApiError;
  } catch (error: unknown) {
    const e = error as ApiError;
    if (e.status !== undefined) throw e;
    demoModeActive = true;
    throw { status: 0, message: 'Sin conexión con el servidor' } as ApiError;
  }
}

// ─── Vehículos — POST /api/vehicles  |  POST /api/vehicles/{plate}/mileage ───

export const vehicleApi = {
  getAll: async (status?: string): Promise<Vehicle[]> => {
    try {
      const query = status ? `?status=${status}` : '';
      return await request<Vehicle[]>(`${FLEET_URL}/api/vehicles${query}`);
    } catch (e: unknown) {
      if ((e as ApiError).status === 0)
        return status ? mockVehicles.filter((v) => v.status === status) : mockVehicles;
      throw e;
    }
  },

  getByPlate: async (plate: string): Promise<Vehicle> => {
    try {
      return await request<Vehicle>(`${FLEET_URL}/api/vehicles/${plate}`);
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        const v = mockVehicles.find((v) => v.plate === plate);
        if (v) return v;
        throw { status: 404, message: 'Vehículo no encontrado' } as ApiError;
      }
      throw e;
    }
  },

  // POST /api/vehicles  ← RegisterVehicleRequest.java
  register: async (data: CreateVehicleDto): Promise<Vehicle> => {
    try {
      return await request<Vehicle>(`${FLEET_URL}/api/vehicles`, {
        method: 'POST',
        body: JSON.stringify(data),
      });
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        if (mockVehicles.find((v) => v.plate === data.plate))
          throw { status: 409, message: 'La placa ya está registrada' } as ApiError;
        const newV: Vehicle = {
          ...data,
          id: crypto.randomUUID(),
          status: 'ACTIVE',
          currentMileage: 0,
          vehicleTypeName: data.vehicleTypeId,
        };
        mockVehicles.push(newV);
        return newV;
      }
      throw e;
    }
  },

  // POST /api/vehicles/{plate}/mileage  ← RegisterMileageRequest.java
  updateMileage: async (plate: string, data: UpdateMileageDto): Promise<MileageLog> => {
    try {
      return await request<MileageLog>(
        `${FLEET_URL}/api/vehicles/${plate}/mileage`,
        { method: 'POST', body: JSON.stringify(data) },
      );
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        const v = mockVehicles.find((v) => v.plate === plate);
        if (v) v.currentMileage = data.mileageValue;
        return {
          mileageLogId: crypto.randomUUID(), 
          vehicleId: v?.id ?? '',
          plate,
          mileageValue: data.mileageValue,
          currentMileage: data.mileageValue,
          recordedBy: data.recordedBy,
          recordedAt: new Date().toISOString(),
          excessiveIncrement: false,
        };
      }
      throw e;
    }
  },
};

// ─── Reglas — POST /api/maintenance-rules  |  POST /{id}/vehicle-types ───────

export const rulesApi = {
  getAll: async (): Promise<MaintenanceRule[]> => {
    try {
      return await request<MaintenanceRule[]>(`${RULES_URL}/api/maintenance-rules`);
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) return mockRules;
      throw e;
    }
  },

  // POST /api/maintenance-rules  ← CreateMaintenanceRuleRequest.java
  create: async (data: CreateRuleDto): Promise<MaintenanceRule> => {
    try {
      return await request<MaintenanceRule>(`${RULES_URL}/api/maintenance-rules`, {
        method: 'POST',
        body: JSON.stringify(data),
      });
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        const now = new Date().toISOString();
        const newR: MaintenanceRule = {
          ...data,
          id: crypto.randomUUID(),
          status: 'ACTIVE',
          createdAt: now,
          updatedAt: now,
        };
        mockRules.push(newR);
        return newR;
      }
      throw e;
    }
  },

  // POST /api/maintenance-rules/{id}/vehicle-types  ← AssociateVehicleTypeRequest.java
  associateVehicleType: async (
    ruleId: string,
    data: AssociateVehicleTypeDto,
  ): Promise<void> => {
    try {
      await request<void>(
        `${RULES_URL}/api/maintenance-rules/${ruleId}/vehicle-types`,
        { method: 'POST', body: JSON.stringify(data) },
      );
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) return;
      throw e;
    }
  },
};

// ─── Alertas — GET /api/alerts (endpoint pendiente de confirmar en backend) ───

export const alertsApi = {
  getAll: async (status?: string): Promise<MaintenanceAlert[]> => {
    try {
      const query = status ? `?status=${status}` : '';
      return await request<MaintenanceAlert[]>(`${RULES_URL}/api/alerts${query}`);
    } catch (e: unknown) {
      if ((e as ApiError).status === 0)
        return status ? mockAlerts.filter((a) => a.status === status) : mockAlerts;
      throw e;
    }
  },
};

// ─── Mantenimientos — POST /api/maintenance  ← RegisterMaintenanceRequest.java

export const maintenanceApi = {
  register: async (data: CreateMaintenanceDto): Promise<MaintenanceRecord> => {
    try {
      return await request<MaintenanceRecord>(`${RULES_URL}/api/maintenance`, {
        method: 'POST',
        body: JSON.stringify(data),
      });
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        const now = new Date().toISOString();
        const newRecord: MaintenanceRecord = {
          ...data,
          id: crypto.randomUUID(),
          description: data.description ?? null,
          cost: data.cost ?? null,
          provider: data.provider ?? null,
          performedAt: data.performedAt ?? now,
          createdAt: now,
        };
        mockRecords.push(newRecord);
        if (data.alertId) {
          const alert = mockAlerts.find((a) => a.id === data.alertId);
          if (alert) alert.status = 'RESOLVED';
        }
        return newRecord;
      }
      throw e;
    }
  },
};

export const isDemoMode = () => demoModeActive;