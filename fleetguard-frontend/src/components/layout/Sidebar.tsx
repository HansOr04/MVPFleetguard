'use client'

import Link from 'next/link';
import { usePathname } from 'next/navigation';

export const Sidebar = () => {
  const pathname = usePathname();
  const isActive = (path: string) => pathname === path;

  const navItems = [
    { path: '/',          label: 'Home',               icon: 'home' },
    { path: '/register',  label: 'Registrar Vehículo',      icon: 'directions_car' },
    { path: '/rules',     label: 'Reglas de Mantenimiento',  icon: 'settings_applications' },
    { path: '/mileage',   label: 'Actualizar Kilometraje',   icon: 'speed' },
    { path: '/services',  label: 'Registro de Servicios',    icon: 'history_edu' },
  ];

  return (
    <aside className="h-screen w-64 fixed left-0 top-0 overflow-y-auto bg-[#002045] shadow-2xl flex flex-col py-6 z-40">
      <div className="px-6 mb-10">
        <span className="text-2xl font-black tracking-tighter text-white">FleetGuard</span>
        <p className="text-slate-400 text-xs mt-1">Fleet Management</p>
      </div>

      <nav className="flex-1 space-y-1 px-4">
        {navItems.map((item) => (
          <Link
            key={item.path}
            href={item.path}
            className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 ease-in-out hover:translate-x-1 ${
              isActive(item.path)
                ? 'bg-[#8ef5b5] text-[#002045] font-bold'
                : 'text-slate-300 hover:bg-[#1a365d] hover:text-white'
            }`}
          >
            <span className="material-symbols-outlined">{item.icon}</span>
            {item.label}
          </Link>
        ))}
      </nav>
    </aside>
  );
};