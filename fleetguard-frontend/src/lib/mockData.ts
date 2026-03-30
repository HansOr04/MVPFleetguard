import {
  Vehicle,
  MaintenanceRule,
  MaintenanceAlert,
  MaintenanceRecord,
} from '@/types';

export const mockVehicleTypes = [
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5311',
    name: 'Sedán',
    description: 'Automóvil de cuatro puertas con maletero separado.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5312',
    name: 'Hatchback',
    description: 'Auto compacto con puerta trasera integrada.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5313',
    name: 'SUV',
    description: 'Vehículo alto con capacidad para distintos terrenos.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5314',
    name: 'Crossover',
    description: 'SUV basado en plataforma de automóvil.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5315',
    name: 'Pickup',
    description: 'Vehículo con cabina y caja de carga abierta.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5316',
    name: 'Coupé',
    description: 'Automóvil de dos puertas con diseño deportivo.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5317',
    name: 'Convertible',
    description: 'Auto con techo retráctil o desmontable.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5318',
    name: 'Minivan',
    description: 'Vehículo familiar con gran espacio interior.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5319',
    name: 'Van',
    description: 'Vehículo amplio para transporte de carga o pasajeros.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5320',
    name: 'Motocicleta',
    description: 'Vehículo motorizado de dos ruedas.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5321',
    name: 'Camión',
    description: 'Vehículo pesado para transporte de mercancías.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5322',
    name: 'Autobús',
    description: 'Vehículo grande para transporte de pasajeros.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5323',
    name: 'Microbús',
    description: 'Vehículo mediano para transporte de pasajeros.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5324',
    name: 'Furgón',
    description: 'Vehículo cerrado para transporte de carga.',
  },
  {
    id: 'c1a1d13e-b3df-4fab-9584-890b852d5325',
    name: 'Tractor',
    description: 'Vehículo de trabajo usado en actividades agrícolas.',
  },
];

export const mockVehicles: Vehicle[] = [
  {
    id: '550e8400-e29b-41d4-a716-446655440001',
    plate: 'ABC-1234',
    brand: 'Toyota',
    model: 'Hilux',
    year: 2023,
    fuelType: 'Diésel',
    vin: '1HGBH41JXMN109186',
    status: 'ACTIVE',
    currentMileage: 45000,
    vehicleTypeName: 'Pickup',
  },
  {
    id: '550e8400-e29b-41d4-a716-446655440002',
    plate: 'GBA-5678',
    brand: 'Toyota',
    model: 'Hilux',
    year: 2022,
    fuelType: 'Diésel',
    vin: '1HGBH41JXMN109187',
    status: 'ACTIVE',
    currentMileage: 50500,
    vehicleTypeName: 'Pickup',
  },
  {
    id: '550e8400-e29b-41d4-a716-446655440003',
    plate: 'PQR-9012',
    brand: 'Chevrolet',
    model: 'Sail',
    year: 2021,
    fuelType: 'Gasolina',
    vin: '1HGBH41JXMN109188',
    status: 'ACTIVE',
    currentMileage: 19800,
    vehicleTypeName: 'Sedán',
  },
  {
    id: '550e8400-e29b-41d4-a716-446655440004',
    plate: 'MNK-3456',
    brand: 'Hino',
    model: '300 Series',
    year: 2020,
    fuelType: 'Diésel',
    vin: '1HGBH41JXMN109189',
    status: 'ACTIVE',
    currentMileage: 142500,
    vehicleTypeName: 'Camión',
  },
];

export const mockRules: MaintenanceRule[] = [
  {
    id: 'a1b2c3d4-e5f6-7890-abcd-ef1234567801',
    name: 'Cambio de Aceite',
    maintenanceType: 'PREVENTIVE',
    intervalKm: 10000,
    warningThresholdKm: 500,
    status: 'ACTIVE',
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'a1b2c3d4-e5f6-7890-abcd-ef1234567802',
    name: 'Rotación de Llantas',
    maintenanceType: 'PREVENTIVE',
    intervalKm: 15000,
    warningThresholdKm: 1000,
    status: 'ACTIVE',
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: 'a1b2c3d4-e5f6-7890-abcd-ef1234567803',
    name: 'Revisión de Frenos',
    maintenanceType: 'PREVENTIVE',
    intervalKm: 20000,
    warningThresholdKm: 1500,
    status: 'ACTIVE',
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
];

export const mockAlerts: MaintenanceAlert[] = [
  {
    id: 'b1c2d3e4-f5a6-7890-bcde-fa1234567801',
    vehicleId: '550e8400-e29b-41d4-a716-446655440002',
    vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5315',
    ruleId: 'a1b2c3d4-e5f6-7890-abcd-ef1234567801',
    status: 'PENDING',
    triggeredAt: '2024-11-01T00:00:00Z',
    dueAtKm: 50000,
  },
  {
    id: 'b1c2d3e4-f5a6-7890-bcde-fa1234567802',
    vehicleId: '550e8400-e29b-41d4-a716-446655440003',
    vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5311',
    ruleId: 'a1b2c3d4-e5f6-7890-abcd-ef1234567803',
    status: 'PENDING',
    triggeredAt: '2024-11-10T00:00:00Z',
    dueAtKm: 20000,
  },
  {
    id: 'b1c2d3e4-f5a6-7890-bcde-fa1234567803',
    vehicleId: '550e8400-e29b-41d4-a716-446655440001',
    vehicleTypeId: 'c1a1d13e-b3df-4fab-9584-890b852d5315',
    ruleId: 'a1b2c3d4-e5f6-7890-abcd-ef1234567801',
    status: 'RESOLVED',
    triggeredAt: '2024-09-01T00:00:00Z',
    dueAtKm: 40000,
  },
];

export const mockRecords: MaintenanceRecord[] = [
  {
    id: 'd1e2f3a4-b5c6-7890-defa-bc1234567801',
    vehicleId: '550e8400-e29b-41d4-a716-446655440001',
    alertId: 'b1c2d3e4-f5a6-7890-bcde-fa1234567803',
    ruleId: 'a1b2c3d4-e5f6-7890-abcd-ef1234567801',
    serviceType: 'Cambio de Aceite',
    description: 'Aceite sintético 5W-30',
    cost: 150,
    provider: 'Taller Autorizado Toyota',
    performedAt: '2024-09-05T00:00:00Z',
    mileageAtService: 40100,
    createdAt: '2024-09-05T00:00:00Z',
  },
];