import React from 'react';

interface InputFieldProps {
  label: string;
  name?: string;
  value: string | number | '';
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  type?: string;
  placeholder?: string;
  required?: boolean;
  disabled?: boolean;
  hint?: string;
  errorMessage?: string;
  uppercase?: boolean;
  min?: string | number;
  step?: string | number;
  onWheel?: (e: React.WheelEvent<HTMLInputElement>) => void;
  onKeyDown?: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  suffix?: string;
  prefixIcon?: string;
}

export const InputField: React.FC<InputFieldProps> = ({
  label,
  name,
  value,
  onChange,
  type = 'text',
  placeholder,
  required = false,
  disabled = false,
  hint,
  errorMessage,
  uppercase = false,
  min,
  step,
  onWheel,
  onKeyDown,
  suffix,
  prefixIcon,
}) => {
  const hasError = Boolean(errorMessage);

  return (
    <div className="space-y-2">
      <label className="block text-sm font-semibold text-on-surface-variant px-1">
        {label} {required && <span className="text-error">*</span>}
      </label>
      <div className="relative">
        {prefixIcon && (
          <span className="absolute left-3 top-1/2 -translate-y-1/2 material-symbols-outlined text-on-surface-variant/50 text-xl pointer-events-none">
            {prefixIcon}
          </span>
        )}
        <input
          name={name}
          value={value}
          onChange={onChange}
          type={type}
          placeholder={placeholder}
          required={required}
          disabled={disabled}
          min={min}
          step={step}
          onWheel={onWheel}
          onKeyDown={onKeyDown}
          className={`w-full border-none rounded-lg py-3 focus:ring-2 transition-all outline-none
            ${prefixIcon ? 'pl-11' : 'pl-4'}
            ${suffix ? 'pr-16' : 'pr-4'}
            ${hasError ? 'bg-error-container/30 focus:ring-error/20' : 'bg-surface-container-highest focus:ring-secondary/20'}
            ${uppercase ? 'uppercase font-mono tracking-widest text-lg' : ''}
          `}
        />
        {suffix && (
          <span className="absolute right-4 top-1/2 -translate-y-1/2 text-sm font-bold text-on-surface-variant pointer-events-none">
            {suffix}
          </span>
        )}
      </div>
      {hint && !hasError && (
        <p className="text-[11px] text-on-surface-variant/70 px-1">{hint}</p>
      )}
      {hasError && (
        <p className="text-[11px] text-error font-medium px-1">{errorMessage}</p>
      )}
    </div>
  );
};