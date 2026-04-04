import React from 'react';

interface EmptyStateProps {
  icon: string;
  title: string;
  description?: string;
}

export const EmptyState: React.FC<EmptyStateProps> = ({ icon, title, description }) => {
  return (
    <div className="bg-surface-container-high rounded-xl p-6 text-center">
      <span className="material-symbols-outlined text-4xl text-on-surface-variant/40 mb-2 block">
        {icon}
      </span>
      <p className="text-on-surface-variant font-medium">{title}</p>
      {description && (
        <p className="text-on-surface-variant/60 text-sm mt-1">{description}</p>
      )}
    </div>
  );
};