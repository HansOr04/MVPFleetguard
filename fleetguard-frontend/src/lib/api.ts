import {
  Vehicle,
  MileageLog,
  MaintenanceRule,
  MaintenanceRecord,
  MaintenanceAlert,
  ApiError,
  CreateVehicleDto,
  UpdateMileageDto,
  CreateRuleDto,
  AssociateVehicleTypeDto,
  CreateMaintenanceDto,
} from "@/types";
import {
  mockVehicles,
  mockRules,
  mockAlerts,
  mockRecords,
} from "./mocks/mockData";

const FLEET_URL = process.env.NEXT_PUBLIC_FLEET_SERVICE_URL;
const RULES_URL = process.env.NEXT_PUBLIC_RULES_SERVICE_URL;

let demoModeActive = false;

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  try {
    const response = await fetch(url, {
      ...options,
      headers: { "Content-Type": "application/json", ...options?.headers },
    });

    if (response.ok) {
      demoModeActive = false;
      if (response.status === 204) return null as T;
      return response.json();
    }

    const errorBody = await response.json().catch(() => ({}));
    throw {
      status: response.status,
      message: errorBody.message || "Error en la solicitud",
      errors: errorBody.errors,
      // Si el backend envía un campo 'field', lo propagamos
      field: errorBody.field,
    } as ApiError;
  } catch (error: unknown) {
    const e = error as ApiError;
    if (e.status !== undefined) throw e;
    demoModeActive = true;
    throw { status: 0, message: "Sin conexión con el servidor" } as ApiError;
  }
}

// ─── Vehículos ────────────────────────────────────────────────────────────────

export const vehicleApi = {
  register: async (data: CreateVehicleDto): Promise<Vehicle> => {
    try {
      return await request<Vehicle>(`${FLEET_URL}/api/vehicles`, {
        method: "POST",
        body: JSON.stringify(data),
      });
    } catch (e: unknown) {
      const err = e as ApiError;

      if (err.status === 0) {
        // Modo demo: simulamos la lógica de duplicados con campo discriminador
        if (mockVehicles.find((v) => v.plate === data.plate))
          throw {
            status: 409,
            message: "La placa ya está registrada",
            field: "plate",
          } as ApiError;

        if (mockVehicles.find((v) => v.vin === data.vin))
          throw {
            status: 409,
            message: "El VIN ya está registrado",
            field: "vin",
          } as ApiError;

        const newV: Vehicle = {
          ...data,
          id: crypto.randomUUID(),
          status: "ACTIVE",
          currentMileage: 0,
          vehicleTypeName: data.vehicleTypeId,
        };
        mockVehicles.push(newV);
        return newV;
      }

      throw err;
    }
  },

  updateMileage: async (
    plate: string,
    data: UpdateMileageDto,
  ): Promise<MileageLog> => {
    try {
      return await request<MileageLog>(
        `${FLEET_URL}/api/vehicles/${plate}/mileage`,
        { method: "POST", body: JSON.stringify(data) },
      );
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        const v = mockVehicles.find((v) => v.plate === plate);
        const previous = v?.currentMileage ?? 0;
        if (v) v.currentMileage = data.mileageValue;
        return {
          mileageLogId: crypto.randomUUID(),
          vehicleId: v?.id ?? "",
          plate,
          previousMileage: previous,
          mileageValue: data.mileageValue,
          kmTraveled: data.mileageValue - previous,
          currentMileage: data.mileageValue,
          recordedBy: data.recordedBy,
          recordedAt: new Date().toISOString(),
          excessiveIncrement: false,
          alertId: null,
        };
      }
      throw e;
    }
  },
};

// ─── Reglas ───────────────────────────────────────���───────────────────────────

export const rulesApi = {
  create: async (data: CreateRuleDto): Promise<MaintenanceRule> => {
    try {
      return await request<MaintenanceRule>(
        `${RULES_URL}/api/maintenance-rules`,
        {
          method: "POST",
          body: JSON.stringify(data),
        },
      );
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        const now = new Date().toISOString();
        const newR: MaintenanceRule = {
          ...data,
          id: crypto.randomUUID(),
          status: "ACTIVE",
          createdAt: now,
          updatedAt: now,
        };
        mockRules.push(newR);
        return newR;
      }
      throw e;
    }
  },

  associateVehicleType: async (
    ruleId: string,
    data: AssociateVehicleTypeDto,
  ): Promise<void> => {
    try {
      await request<void>(
        `${RULES_URL}/api/maintenance-rules/${ruleId}/vehicle-types`,
        { method: "POST", body: JSON.stringify(data) },
      );
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) return;
      throw e;
    }
  },
};

// ─── Mantenimientos ───────────────────────────────────────────────────────────

export const maintenanceApi = {
  register: async (data: CreateMaintenanceDto): Promise<MaintenanceRecord> => {
    const plate = data.plate.trim().toUpperCase();
    const { plate: _, ...body } = data;
    try {
      return await request<MaintenanceRecord>(
        `${RULES_URL}/api/maintenance/${plate}`,
        { method: "POST", body: JSON.stringify(body) },
      );
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        const now = new Date().toISOString();
        const v = mockVehicles.find((v) => v.plate === plate);
        const alert = mockAlerts.find((a) => a.id === data.alertId);
        const newRecord: MaintenanceRecord = {
          id: crypto.randomUUID(),
          plate,
          alertId: data.alertId,
          ruleId: alert?.ruleId ?? null,
          serviceType: data.serviceType,
          description: data.description ?? null,
          cost: data.cost ?? null,
          provider: data.provider ?? null,
          performedAt: data.performedAt ?? now,
          mileageAtService: data.mileageAtService,
          recordedBy: data.recordedBy,
          createdAt: now,
        };
        mockRecords.push(newRecord);
        if (alert) alert.status = "RESOLVED";
        if (v && data.mileageAtService > v.currentMileage) {
          v.currentMileage = data.mileageAtService;
        }
        return newRecord;
      }
      throw e;
    }
  },
};

export const isDemoMode = () => demoModeActive;

// ─── Alertas ──────────────────────────────────────────────────────────────────

export const alertsApi = {
  getAll: async (status?: string): Promise<MaintenanceAlert[]> => {
    const url = status
      ? `${RULES_URL}/api/alerts?status=${status}`
      : `${RULES_URL}/api/alerts`;
    try {
      return await request<MaintenanceAlert[]>(url);
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        return status
          ? mockAlerts.filter((a) => a.status === status)
          : mockAlerts.filter((a) =>
              ["PENDING", "WARNING", "OVERDUE"].includes(a.status),
            );
      }
      throw e;
    }
  },

  getByPlate: async (plate: string): Promise<MaintenanceAlert[]> => {
    try {
      return await request<MaintenanceAlert[]>(
        `${RULES_URL}/api/alerts/vehicle/${plate.trim().toUpperCase()}`,
      );
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        const v = mockVehicles.find(
          (v) => v.plate === plate.trim().toUpperCase(),
        );
        return v
          ? mockAlerts.filter(
              (a) =>
                a.vehicleId === v.id &&
                ["PENDING", "WARNING", "OVERDUE"].includes(a.status),
            )
          : [];
      }
      throw e;
    }
  },

  getByVehicleId: async (vehicleId: string): Promise<MaintenanceAlert[]> => {
    try {
      const all = await request<MaintenanceAlert[]>(`${RULES_URL}/api/alerts`);
      return all.filter((a) => a.vehicleId === vehicleId);
    } catch (e: unknown) {
      if ((e as ApiError).status === 0) {
        return mockAlerts.filter(
          (a) =>
            a.vehicleId === vehicleId &&
            ["PENDING", "WARNING", "OVERDUE"].includes(a.status),
        );
      }
      throw e;
    }
  },
};
