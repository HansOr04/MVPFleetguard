import { alertsApi } from '@/lib/api';
import { MaintenanceAlert } from '@/types';

export const alertService = {
  getAll: async (status?: string): Promise<MaintenanceAlert[]> => {
    return alertsApi.getAll(status);
  },

  getByPlate: async (plate: string): Promise<MaintenanceAlert[]> => {
    return alertsApi.getByPlate(plate.toUpperCase());
  },

  getByVehicleId: async (vehicleId: string): Promise<MaintenanceAlert[]> => {
    return alertsApi.getByVehicleId(vehicleId);
  },
};