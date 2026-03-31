import React from 'react';

export const LoadingSpinner: React.FC = () => {
  return (
    <div className="flex flex-col items-center justify-center p-12">
      <div className="w-10 h-10 border-4 border-surface-container-high border-t-secondary rounded-full animate-spin"></div>
      <p className="mt-4 text-on-surface-variant text-sm font-medium">Cargando...</p>
    </div>
  );
};
