export interface Vehicle {
  id: string
  plate: string
  brand: string
  model: string
  year: number
  fuelType: string
  vin: string
  status: string
  currentMileage: number
  vehicleTypeName: string
}

export interface VehicleType {
  id: string
  name: string
  description: string
}

export interface MileageLog {
  mileageLogId: string
  vehicleId: string
  plate: string
  previousMileage: number
  mileageValue: number
  kmTraveled: number
  currentMileage: number
  recordedBy: string
  recordedAt: string
  excessiveIncrement: boolean
  alertId: string | null
}

export interface MaintenanceRule {
  id: string
  name: string
  maintenanceType: string
  intervalKm: number
  warningThresholdKm: number
  status: string
  createdAt: string
  updatedAt: string
}

export interface RuleVehicleTypeAssoc {
  id: string
  ruleId: string
  vehicleTypeId: string
  createdAt: string
}

export interface MaintenanceAlert {
  id: string
  vehicleId: string
  vehicleTypeId: string
  ruleId: string
  ruleName?: string
  status: 'PENDING' | 'WARNING' | 'OVERDUE' | 'RESOLVED'
  triggeredAt: string
  dueAtKm: number
}

export interface MaintenanceRecord {
  id: string
  plate: string
  alertId: string | null
  ruleId: string | null
  serviceType: string
  description: string | null
  cost: number | null
  provider: string | null
  performedAt: string
  mileageAtService: number
  recordedBy: string
  createdAt: string
}

export interface ApiError {
  status: number
  message: string
  errors?: string[]
}

// ─── DTOs  ──────────────────────────────────────

export interface CreateVehicleDto {
  plate: string
  brand: string
  model: string
  year: number
  fuelType: string
  vin: string
  vehicleTypeId: string
}

export interface UpdateMileageDto {
  mileageValue: number
  recordedBy: string
}

export interface CreateRuleDto {
  name: string
  maintenanceType: string
  intervalKm: number
  warningThresholdKm: number
}

export interface AssociateVehicleTypeDto {
  vehicleTypeId: string
}

export interface CreateMaintenanceDto {
  plate: string
  alertId: string
  serviceType: string
  description: string | null
  cost: number | null
  provider: string | null
  performedAt: string | null
  mileageAtService: number
  recordedBy: string
}