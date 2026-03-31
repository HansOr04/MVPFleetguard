// ─── Modelos (respuestas del backend) ───────────────────────────────────────

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

// MileageResponse.java
export interface MileageLog {
  mileageLogId: string   
  vehicleId: string
  plate: string
  mileageValue: number
  currentMileage: number
  recordedBy: string
  recordedAt: string
  excessiveIncrement: boolean
}

// MaintenanceRuleResponse.java
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

// RuleVehicleTypeAssocResponse.java
export interface RuleVehicleTypeAssoc {
  id: string
  ruleId: string
  vehicleTypeId: string
  createdAt: string
}

// MaintenanceAlert (generado internamente por rules-alerts-service)
export interface MaintenanceAlert {
  id: string
  vehicleId: string
  vehicleTypeId: string
  ruleId: string
  status: 'PENDING' | 'WARNING' | 'OVERDUE' | 'RESOLVED'
  triggeredAt: string
  dueAtKm: number
}

// MaintenanceRecordResponse.java
export interface MaintenanceRecord {
  id: string
  vehicleId: string
  alertId: string | null
  ruleId: string | null
  serviceType: string
  description: string | null
  cost: number | null       
  provider: string | null
  performedAt: string
  mileageAtService: number
  createdAt: string
}

export interface ApiError {
  status: number
  message: string
  errors?: string[]
}

// ─── DTOs (lo que se envía al backend) ──────────────────────────────────────

// fleet-service — RegisterVehicleRequest.java
export interface CreateVehicleDto {
  plate: string
  brand: string
  model: string
  year: number
  fuelType: string
  vin: string
  vehicleTypeId: string  
}

// fleet-service — RegisterMileageRequest.java
export interface UpdateMileageDto {
  mileageValue: number   
  recordedBy: string
}

// rules-alerts-service — CreateMaintenanceRuleRequest.java
export interface CreateRuleDto {
  name: string
  maintenanceType: string
  intervalKm: number     
  warningThresholdKm: number
}

// rules-alerts-service — AssociateVehicleTypeRequest.java
export interface AssociateVehicleTypeDto {
  vehicleTypeId: string  
}

// rules-alerts-service — RegisterMaintenanceRequest.java
export interface CreateMaintenanceDto {
  vehicleId: string      
  alertId: string | null
  ruleId: string | null
  serviceType: string    
  description: string | null
  cost: number | null    
  provider: string | null
  performedAt: string | null
  mileageAtService: number   
}