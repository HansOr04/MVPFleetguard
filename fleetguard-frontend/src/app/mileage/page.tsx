'use client'

import React from 'react';

export default function UpdateMileagePage() {
  return (
    <div className="min-h-screen">
      <div className="p-12 pt-24">
        <h1 className="text-4xl font-bold mb-4">Actualizar Kilometraje</h1>
        <p className="text-gray-600 mb-8">Busca un vehículo por placa y actualiza su odómetro.</p>

        <section>
          <input placeholder="Buscar placa..." type="text" />
          <button type="button">Buscar</button>
        </section>

        <section>
          <p>Aquí aparecerá la información del vehículo encontrado.</p>
          <input placeholder="Nuevo kilometraje" type="number" />
          <input placeholder="Registrado por (opcional)" type="text" />
          <button type="button">Actualizar Odómetro</button>
        </section>
      </div>
    </div>
  );
}
