import { describe, it, expect } from 'vitest';
import { mileageValidator } from '@/validators/mileage.validator';

describe('mileageValidator', () => {
  describe('mileageValue', () => {
    it('returns error when empty string', () => {
      expect(mileageValidator.mileageValue('')).toBe('El kilometraje es obligatorio');
    });
    it('returns error when negative', () => {
      expect(mileageValidator.mileageValue(-5)).toBe('El kilometraje no puede ser negativo');
    });
    it('returns error when zero', () => {
      expect(mileageValidator.mileageValue(0)).toBe('El kilometraje debe ser mayor a cero');
    });
    it('returns null for valid positive value', () => {
      expect(mileageValidator.mileageValue(1000)).toBeNull();
    });
  });

  describe('recordedBy', () => {
    it('returns error when empty', () => {
      expect(mileageValidator.recordedBy('')).toBe('El nombre de quien registra es obligatorio');
    });
    it('returns null for valid value', () => {
      expect(mileageValidator.recordedBy('Juan')).toBeNull();
    });
  });

  describe('isFormValid', () => {
    it('returns false when plate is empty', () => {
      expect(mileageValidator.isFormValid('', 1000, 'Juan')).toBe(false);
    });
    it('returns false when mileage is empty string', () => {
      expect(mileageValidator.isFormValid('ABC-1234', '', 'Juan')).toBe(false);
    });
    it('returns false when mileage is zero', () => {
      expect(mileageValidator.isFormValid('ABC-1234', 0, 'Juan')).toBe(false);
    });
    it('returns false when recordedBy is empty', () => {
      expect(mileageValidator.isFormValid('ABC-1234', 1000, '')).toBe(false);
    });
    it('returns true when all fields are valid', () => {
      expect(mileageValidator.isFormValid('ABC-1234', 1000, 'Juan')).toBe(true);
    });
  });
});