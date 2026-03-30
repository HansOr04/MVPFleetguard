'use client'

import React, { useState } from 'react';
import { CreateVehicleDto } from '@/types';

export default function RegisterVehiclePage() {
    const [formData, setFormData] = useState<CreateVehicleDto>({
        plate: '',
        vin: '',
        brand: '',
        model: '',
        year: new Date().getFullYear(),
        fuelType: 'Diésel',
        vehicleTypeId: '',
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
    };

    return (
        <div>
            <h1>Registrar Nuevo Vehículo</h1>
            <form onSubmit={handleSubmit}>
                <input name="plate" value={formData.plate} onChange={handleChange} placeholder="Placa" />
                <input name="vin" value={formData.vin} onChange={handleChange} placeholder="VIN (17 caracteres)" maxLength={17} />
                <input name="brand" value={formData.brand} onChange={handleChange} placeholder="Marca" />
                <input name="model" value={formData.model} onChange={handleChange} placeholder="Modelo" />
                <input name="year" value={formData.year} onChange={handleChange} type="number" />
                <select name="fuelType" value={formData.fuelType} onChange={handleChange}>
                    <option value="Gasolina">Gasolina</option>
                    <option value="Diésel">Diésel</option>
                    <option value="Híbrido">Híbrido</option>
                    <option value="Eléctrico">Eléctrico</option>
                </select>
                <select name="vehicleTypeId" value={formData.vehicleTypeId} onChange={handleChange}>
                    <option value="">Seleccionar tipo...</option>
                    <option value="pickup">Pick-up / Utilitario</option>
                    <option value="suv">SUV / Camioneta</option>
                    <option value="truck">Camión de Carga</option>
                    <option value="sedan">Sedán / Ejecutivo</option>
                </select>
                <button type="submit">Guardar Vehículo</button>
            </form>
        </div>
    );
}