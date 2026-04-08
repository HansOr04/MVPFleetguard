import { describe, it, expect } from 'vitest';
import { maintenanceValidator } from '@/validators/maintenance.validator';

describe('maintenanceValidator', () => {
  describe('plate', () => {
    it('returns error when empty', () => {
      expect(maintenanceValidator.plate('')).toBe('La placa es obligatoria');
    });
    it('returns null for valid plate', () => {
      expect(maintenanceValidator.plate('ABC-1234')).toBeNull();
    });
  });

  describe('recordedBy', () => {
    it('returns error when empty', () => {
      expect(maintenanceValidator.recordedBy('')).toBe('El nombre de quien registra es obligatorio');
    });
    it('returns null for valid value', () => {
      expect(maintenanceValidator.recordedBy('Técnico')).toBeNull();
    });
  });

  describe('performedAt', () => {
    it('returns error when empty', () => {
      expect(maintenanceValidator.performedAt('')).toBe('La fecha del servicio es obligatoria');
    });
    it('returns null for valid date', () => {
      expect(maintenanceValidator.performedAt('2026-04-08')).toBeNull();
    });
  });

  describe('mileageAtService', () => {
    it('returns error when empty string', () => {
      expect(maintenanceValidator.mileageAtService('')).toBe('El kilometraje es obligatorio');
    });
    it('returns error when zero', () => {
      expect(maintenanceValidator.mileageAtService(0)).toBe('El kilometraje debe ser mayor a cero');
    });
    it('returns null for valid value', () => {
      expect(maintenanceValidator.mileageAtService(5000)).toBeNull();
    });
  });

  describe('isFormValid', () => {
    it('returns false when alertId is null', () => {
      expect(maintenanceValidator.isFormValid('ABC-1234', null, 'Juan', '2026-04-08', 5000)).toBe(false);
    });
    it('returns false when mileage is zero', () => {
      expect(maintenanceValidator.isFormValid('ABC-1234', 'alert-1', 'Juan', '2026-04-08', 0)).toBe(false);
    });
    it('returns true when all fields are valid', () => {
      expect(maintenanceValidator.isFormValid('ABC-1234', 'alert-1', 'Juan', '2026-04-08', 5000)).toBe(true);
    });
  });
});