import React from 'react';

interface InfoCardProps {
  icon: string;
  title: string;
  children: React.ReactNode;
  variant?: 'default' | 'dark';
}

export const InfoCard: React.FC<InfoCardProps> = ({
  icon,
  title,
  children,
  variant = 'default',
}) => {
  if (variant === 'dark') {
    return (
      <div className="bg-primary text-white rounded-xl p-8 shadow-xl relative overflow-hidden">
        <div className="absolute -right-12 -bottom-12 w-48 h-48 bg-secondary/10 rounded-full blur-3xl" />
        <div className="relative z-10">
          <span
            className="material-symbols-outlined text-4xl text-secondary-container mb-4 block"
            style={{ fontVariationSettings: "'FILL' 1" }}
          >
            {icon}
          </span>
          <p className="text-sm font-medium opacity-80 leading-relaxed">{children}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-surface-container-low rounded-xl p-6 border-l-4 border-secondary">
      <h4 className="font-bold text-primary mb-4 flex items-center gap-2">
        <span
          className="material-symbols-outlined text-secondary"
          style={{ fontVariationSettings: "'FILL' 1" }}
        >
          {icon}
        </span>
        {title}
      </h4>
      <div className="text-sm text-on-surface-variant leading-relaxed">
        {children}
      </div>
    </div>
  );
};