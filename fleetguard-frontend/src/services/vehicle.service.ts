import { vehicleApi } from '@/lib/api';
import { Vehicle, CreateVehicleDto, UpdateMileageDto, MileageLog } from '@/types';

export const vehicleService = {
  register: async (data: CreateVehicleDto): Promise<Vehicle> => {
    return vehicleApi.register(data);
  },

  updateMileage: async (plate: string, data: UpdateMileageDto): Promise<MileageLog> => {
    return vehicleApi.updateMileage(plate.toUpperCase(), data);
  },
};