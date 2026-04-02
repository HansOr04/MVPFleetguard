'use client'

import React from 'react';
import Link from 'next/link';

export default function HomePage() {
  return (
    <div className="min-h-screen">
      <div className="p-12 pt-24 max-w-[1600px] mx-auto">

        <div className="mb-16">
          <h1 className="text-5xl font-extrabold tracking-tight text-primary mb-4">
            Bienvenido a FleetGuard
          </h1>
          <p className="text-on-surface-variant text-xl max-w-2xl leading-relaxed">
            Gestiona el mantenimiento preventivo de tu flota de forma inteligente. Registra vehículos, define reglas y resuelve alertas antes de que se conviertan en fallas.
          </p>
        </div>

        <div className="grid grid-cols-12 gap-8 mb-12">

          <Link
            href="/register"
            className="col-span-12 md:col-span-6 lg:col-span-4 bg-surface-container-lowest rounded-xl shadow-sm p-8 hover:shadow-md transition-all group border-2 border-transparent hover:border-secondary/20"
          >
            <div className="flex items-center gap-4 mb-6">
              <span className="w-14 h-14 rounded-xl bg-primary-container flex items-center justify-center">
                <span
                  className="material-symbols-outlined text-sm text-secondary-container"
                  style={{ fontVariationSettings: "'FILL' 1" }}
                >
                  directions_car
                </span>
              </span>
              <div>
                <h2 className="text-lg font-bold text-primary">Registrar Vehículo</h2>
                <p className="text-xs text-on-surface-variant font-medium">Flota</p>
              </div>
            </div>
            <p className="text-sm text-on-surface-variant leading-relaxed">
              Integra nuevas unidades al sistema con su información técnica completa: placa, VIN, marca, modelo, año y tipo de combustible.
            </p>
            <div className="mt-6 flex items-center gap-1 text-secondary font-bold text-sm group-hover:gap-2 transition-all">
              <span>Ir al registro</span>
              <span className="material-symbols-outlined text-base">arrow_forward</span>
            </div>
          </Link>

          <Link
            href="/rules"
            className="col-span-12 md:col-span-6 lg:col-span-4 bg-surface-container-lowest rounded-xl shadow-sm p-8 hover:shadow-md transition-all group border-2 border-transparent hover:border-secondary/20"
          >
            <div className="flex items-center gap-4 mb-6">
              <span className="w-14 h-14 rounded-xl bg-primary-container flex items-center justify-center">
                <span
                  className="material-symbols-outlined text-sm text-secondary-container"
                  style={{ fontVariationSettings: "'FILL' 1" }}
                >
                  settings_applications
                </span>
              </span>
              <div>
                <h2 className="text-lg font-bold text-primary">Reglas de Mantenimiento</h2>
                <p className="text-xs text-on-surface-variant font-medium">Configuración</p>
              </div>
            </div>
            <p className="text-sm text-on-surface-variant leading-relaxed">
              Define los intervalos de mantenimiento preventivo y asócialos a los tipos de vehículo para que el sistema genere alertas automáticas.
            </p>
            <div className="mt-6 flex items-center gap-1 text-secondary font-bold text-sm group-hover:gap-2 transition-all">
              <span>Ver reglas</span>
              <span className="material-symbols-outlined text-base">arrow_forward</span>
            </div>
          </Link>

          <Link
            href="/services"
            className="col-span-12 md:col-span-6 lg:col-span-4 bg-surface-container-lowest rounded-xl shadow-sm p-8 hover:shadow-md transition-all group border-2 border-transparent hover:border-secondary/20"
          >
            <div className="flex items-center gap-4 mb-6">
              <span className="w-14 h-14 rounded-xl bg-primary-container flex items-center justify-center">
                <span
                  className="material-symbols-outlined text-sm text-secondary-container"
                  style={{ fontVariationSettings: "'FILL' 1" }}
                >
                  build_circle
                </span>
              </span>
              <div>
                <h2 className="text-lg font-bold text-primary">Registrar Servicio</h2>
                <p className="text-xs text-on-surface-variant font-medium">Mantenimiento</p>
              </div>
            </div>
            <p className="text-sm text-on-surface-variant leading-relaxed">
              Documenta las intervenciones realizadas sobre los vehículos, resuelve alertas activas y mantén el historial de mantenimiento al día.
            </p>
            <div className="mt-6 flex items-center gap-1 text-secondary font-bold text-sm group-hover:gap-2 transition-all">
              <span>Registrar</span>
              <span className="material-symbols-outlined text-base">arrow_forward</span>
            </div>
          </Link>

          <Link
            href="/mileage"
            className="col-span-12 md:col-span-6 lg:col-span-4 bg-surface-container-lowest rounded-xl shadow-sm p-8 hover:shadow-md transition-all group border-2 border-transparent hover:border-secondary/20"
          >
            <div className="flex items-center gap-4 mb-6">
              <span className="w-14 h-14 rounded-xl bg-primary-container flex items-center justify-center">
                <span
                  className="material-symbols-outlined text-sm text-secondary-container"
                  style={{ fontVariationSettings: "'FILL' 1" }}
                >
                  speed
                </span>
              </span>
              <div>
                <h2 className="text-lg font-bold text-primary">Actualizar Kilometraje</h2>
                <p className="text-xs text-on-surface-variant font-medium">Trazabilidad</p>
              </div>
            </div>
            <p className="text-sm text-on-surface-variant leading-relaxed">
              Registra el kilometraje actual de cada vehículo para que el sistema evalúe las reglas y genere alertas de mantenimiento cuando corresponda.
            </p>
            <div className="mt-6 flex items-center gap-1 text-secondary font-bold text-sm group-hover:gap-2 transition-all">
              <span>Actualizar</span>
              <span className="material-symbols-outlined text-base">arrow_forward</span>
            </div>
          </Link>

          <div className="col-span-12 lg:col-span-8 bg-primary text-white rounded-xl p-10 shadow-xl relative overflow-hidden">
            <div className="absolute -right-16 -bottom-16 w-72 h-72 bg-secondary/10 rounded-full blur-3xl" />
            <div className="absolute -left-8 -top-8 w-48 h-48 bg-white/5 rounded-full blur-2xl" />
            <div className="relative z-10">
              <span
                className="material-symbols-outlined text-5xl text-secondary-container mb-6 block"
                style={{ fontVariationSettings: "'FILL' 1" }}
              >
                shield_check
              </span>
              <h3 className="text-2xl font-extrabold mb-3">
                Mantenimiento preventivo que protege tu inversión
              </h3>
              <p className="text-sm opacity-80 leading-relaxed max-w-xl">
                FleetGuard monitorea continuamente el kilometraje de tu flota, evalúa las reglas de mantenimiento configuradas y genera alertas automáticas antes de que se venza cada intervención. El resultado: menos fallas imprevistas, más vida útil para tus vehículos.
              </p>
            </div>
          </div>

        </div>

        <div className="bg-surface-container-low rounded-xl p-6 border-l-4 border-secondary">
          <h3 className="font-bold text-primary mb-4 flex items-center gap-2">
            ¿Cómo funciona el ciclo de mantenimiento?
          </h3>
          <ol className="text-sm text-on-surface-variant leading-relaxed space-y-2 list-none">
            <li className="flex items-start gap-3">
              <span className="w-5 h-5 rounded-full bg-secondary text-white text-xs font-bold flex items-center justify-center shrink-0 mt-0.5">1</span>
              <span>Registra los vehículos de tu flota con su información técnica completa.</span>
            </li>
            <li className="flex items-start gap-3">
              <span className="w-5 h-5 rounded-full bg-secondary text-white text-xs font-bold flex items-center justify-center shrink-0 mt-0.5">2</span>
              <span>Define las reglas de mantenimiento con su intervalo en km y umbral de aviso, asociadas al tipo de vehículo correspondiente.</span>
            </li>
            <li className="flex items-start gap-3">
              <span className="w-5 h-5 rounded-full bg-secondary text-white text-xs font-bold flex items-center justify-center shrink-0 mt-0.5">3</span>
              <span>Actualiza el kilometraje de cada vehículo periódicamente. El sistema evalúa automáticamente las reglas y genera alertas cuando el vehículo se acerca al umbral.</span>
            </li>
            <li className="flex items-start gap-3">
              <span className="w-5 h-5 rounded-full bg-secondary text-white text-xs font-bold flex items-center justify-center shrink-0 mt-0.5">4</span>
              <span>Consulta las alertas activas por placa, selecciona la alerta a resolver y registra el servicio realizado. La alerta queda resuelta automáticamente.</span>
            </li>
          </ol>
        </div>

      </div>
    </div>
  );
}
