import { setupServer } from 'msw/node';
import { vehicleHandlers } from './handlers/vehicle.handlers';
import { alertHandlers } from './handlers/alert.handlers';
import { maintenanceHandlers } from './handlers/maintenance.handlers';
import { rulesHandlers } from './handlers/rules.handlers';

export const server = setupServer(
  ...vehicleHandlers,
  ...alertHandlers,
  ...maintenanceHandlers,
  ...rulesHandlers,
);