import React from 'react';

interface ButtonProps {
  type?: 'button' | 'submit' | 'reset';
  variant?: 'primary' | 'secondary' | 'ghost';
  loading?: boolean;
  disabled?: boolean;
  fullWidth?: boolean;
  icon?: string;
  children: React.ReactNode;
  onClick?: () => void;
}

export const Button: React.FC<ButtonProps> = ({
  type = 'button',
  variant = 'primary',
  loading = false,
  disabled = false,
  fullWidth = false,
  icon,
  children,
  onClick,
}) => {
  const base = 'px-10 py-3 rounded-lg font-bold transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed';

  const variants = {
    primary: 'bg-secondary text-white shadow-lg shadow-secondary/20 hover:bg-on-secondary-container',
    secondary: 'bg-primary-container text-secondary-container hover:bg-primary-container/80',
    ghost: 'bg-transparent text-secondary hover:bg-secondary/10',
  };

  return (
    <button
      type={type}
      disabled={disabled || loading}
      onClick={onClick}
      className={`${base} ${variants[variant]} ${fullWidth ? 'w-full' : ''}`}
    >
      {loading ? (
        <span className="material-symbols-outlined animate-spin text-sm">sync</span>
      ) : icon ? (
        <span className="material-symbols-outlined text-sm">{icon}</span>
      ) : null}
      {children}
    </button>
  );
};