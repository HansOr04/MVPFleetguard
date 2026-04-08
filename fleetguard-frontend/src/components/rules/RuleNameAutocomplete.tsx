import React from 'react';
import { mockMaintenanceRules } from '@/lib/mocks/mockMaintenanceRules';

interface RuleNameAutocompleteProps {
  nameInput: string;
  nameIsValid: boolean;
  showSuggestions: boolean;
  highlightedIndex: number;
  filteredSuggestions: typeof mockMaintenanceRules;
  inputRef: React.RefObject<HTMLInputElement>;
  suggestionRef: React.RefObject<HTMLDivElement>;
  onInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onKeyDown: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  onFocus: () => void;
  onSelectSuggestion: (rule: typeof mockMaintenanceRules[0]) => void;
}

export const RuleNameAutocomplete: React.FC<RuleNameAutocompleteProps> = ({
  nameInput,
  nameIsValid,
  showSuggestions,
  highlightedIndex,
  filteredSuggestions,
  inputRef,
  suggestionRef,
  onInputChange,
  onKeyDown,
  onFocus,
  onSelectSuggestion,
}) => {
  return (
    <div className="space-y-2">
      <label className="block text-sm font-semibold text-on-surface-variant px-1">
        Nombre de la Regla <span className="text-error">*</span>
      </label>
      <div className="relative">
        <input
          ref={inputRef}
          required
          name="name"
          value={nameInput}
          onChange={onInputChange}
          onKeyDown={onKeyDown}
          onFocus={onFocus}
          placeholder="Escribe para buscar una regla..."
          type="text"
          autoComplete="off"
          className={`w-full border-none rounded-lg py-3 px-4 focus:ring-2 transition-all outline-none ${
            nameInput.length > 0 && !nameIsValid
              ? 'bg-error-container/30 focus:ring-error/20'
              : 'bg-surface-container-highest focus:ring-secondary/20'
          }`}
        />
        {nameInput.length > 0 && (
          <div className="absolute right-3 top-1/2 -translate-y-1/2">
            {nameIsValid ? (
              <span
                className="material-symbols-outlined text-secondary"
                style={{ fontVariationSettings: "'FILL' 1" }}
              >
                check_circle
              </span>
            ) : (
              <span
                className="material-symbols-outlined text-error"
                style={{ fontVariationSettings: "'FILL' 1" }}
              >
                error
              </span>
            )}
          </div>
        )}
        {showSuggestions && filteredSuggestions.length > 0 && (
          <div
            ref={suggestionRef}
            className="absolute z-50 left-0 right-0 top-full mt-1 bg-surface-container-lowest rounded-xl shadow-xl border border-outline-variant/20 max-h-64 overflow-y-auto"
          >
            {filteredSuggestions.map((rule, index) => (
              <button
                key={rule.ruleType}
                type="button"
                onMouseDown={() => onSelectSuggestion(rule)}
                className={`w-full text-left px-4 py-3 transition-colors ${
                  index === highlightedIndex
                    ? 'bg-secondary/10 text-secondary'
                    : 'hover:bg-surface-container-high text-on-surface'
                } ${index !== filteredSuggestions.length - 1 ? 'border-b border-outline-variant/10' : ''}`}
              >
                <span className="text-sm font-medium">{rule.ruleType}</span>
              </button>
            ))}
          </div>
        )}
        {showSuggestions && nameInput.trim().length > 0 && filteredSuggestions.length === 0 && (
          <div className="absolute z-50 left-0 right-0 top-full mt-1 bg-surface-container-lowest rounded-xl shadow-xl border border-outline-variant/20 px-4 py-3">
            <p className="text-sm text-on-surface-variant">
              No se encontraron reglas para esta búsqueda.
            </p>
          </div>
        )}
      </div>
      {nameInput.length > 0 && !nameIsValid && (
        <p className="text-[11px] text-error font-medium px-1">
          Selecciona una opción válida de la lista.
        </p>
      )}
    </div>
  );
};