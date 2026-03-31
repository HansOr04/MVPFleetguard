import React from 'react';

export const SkeletonCard: React.FC = () => {
  return (
    <div className="bg-surface-container p-8 rounded-xl flex flex-col justify-between items-start animate-pulse h-[160px]">
      <div className="w-12 h-12 rounded-lg bg-surface-variant flex items-center justify-center mb-6"></div>
      <div>
        <div className="h-4 bg-surface-variant rounded w-24 mb-2"></div>
        <div className="h-8 bg-surface-variant rounded w-16"></div>
      </div>
    </div>
  );
};
