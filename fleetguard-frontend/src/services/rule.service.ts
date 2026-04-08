import { rulesApi } from '@/lib/api';
import { MaintenanceRule, CreateRuleDto, AssociateVehicleTypeDto } from '@/types';

export const ruleService = {
  create: async (data: CreateRuleDto): Promise<MaintenanceRule> => {
    return rulesApi.create(data);
  },

  associateVehicleType: async (ruleId: string, data: AssociateVehicleTypeDto): Promise<void> => {
    return rulesApi.associateVehicleType(ruleId, data);
  },
};