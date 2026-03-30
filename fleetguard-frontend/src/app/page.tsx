'use client'

import React from 'react';
import Link from 'next/link';

export default function DashboardPage() {

    return (
        <div className="min-h-screen">
            <div className="flex items-center justify-center h-full">
                <div className="text-center">
                    <h1 className="text-4xl font-bold mb-4">Bienvenido a FleetGuard</h1>
                    <p className="text-lg text-gray-600 mb-6">Tu sistema de gestión de flotas confiable</p>
                </div>
            </div>
        </div>
    );
}
