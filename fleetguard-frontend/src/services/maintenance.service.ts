import { maintenanceApi } from '@/lib/api';
import { CreateMaintenanceDto, MaintenanceRecord } from '@/types';

export const maintenanceService = {
  register: async (data: CreateMaintenanceDto): Promise<MaintenanceRecord> => {
    return maintenanceApi.register(data);
  },
};