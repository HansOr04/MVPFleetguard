import { describe, it, expect } from 'vitest';
import { vehicleValidator } from '@/validators/vehicle.validator';

describe('vehicleValidator', () => {
  describe('plate', () => {
    it('returns error when empty', () => {
      expect(vehicleValidator.plate('')).toBe('La placa es obligatoria');
    });
    it('returns error when only whitespace', () => {
      expect(vehicleValidator.plate('   ')).toBe('La placa es obligatoria');
    });
    it('returns null for valid plate', () => {
      expect(vehicleValidator.plate('ABC-1234')).toBeNull();
    });
  });

  describe('vin', () => {
    it('returns error when empty', () => {
      expect(vehicleValidator.vin('')).toBe('El VIN es obligatorio');
    });
    it('returns error when length != 17', () => {
      expect(vehicleValidator.vin('1234')).toBe('El VIN debe tener exactamente 17 caracteres');
    });
    it('returns null for valid 17-char VIN', () => {
      expect(vehicleValidator.vin('1HGBH41JXMN109186')).toBeNull();
    });
  });

  describe('brand', () => {
    it('returns error when empty', () => {
      expect(vehicleValidator.brand('')).toBe('La marca es obligatoria');
    });
    it('returns null for valid brand', () => {
      expect(vehicleValidator.brand('Toyota')).toBeNull();
    });
  });

  describe('model', () => {
    it('returns error when empty', () => {
      expect(vehicleValidator.model('')).toBe('El modelo es obligatorio');
    });
    it('returns null for valid model', () => {
      expect(vehicleValidator.model('Hilux')).toBeNull();
    });
  });

  describe('year', () => {
    it('returns error when zero', () => {
      expect(vehicleValidator.year(0)).toBe('El año es obligatorio');
    });
    it('returns error when negative', () => {
      expect(vehicleValidator.year(-1)).toBe('El año es obligatorio');
    });
    it('returns null for valid year', () => {
      expect(vehicleValidator.year(2023)).toBeNull();
    });
  });

  describe('fuelType', () => {
    it('returns error when empty', () => {
      expect(vehicleValidator.fuelType('')).toBe('El tipo de combustible es obligatorio');
    });
    it('returns null for valid fuelType', () => {
      expect(vehicleValidator.fuelType('Diésel')).toBeNull();
    });
  });

  describe('vehicleTypeId', () => {
    it('returns error when empty', () => {
      expect(vehicleValidator.vehicleTypeId('')).toBe('El tipo de vehículo es obligatorio');
    });
    it('returns null for valid vehicleTypeId', () => {
      expect(vehicleValidator.vehicleTypeId('c1a1d13e-b3df-4fab-9584-890b852d5311')).toBeNull();
    });
  });
});