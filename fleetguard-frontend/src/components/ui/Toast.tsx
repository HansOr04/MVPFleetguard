import React from 'react';

interface ToastProps {
  message: string;
  type: 'success' | 'error';
  visible: boolean;
  onClose?: () => void;
}

export const Toast: React.FC<ToastProps> = ({ message, type, visible, onClose }) => {
  if (!visible) return null;

  return (
    <div className={`fixed bottom-6 right-6 p-4 rounded-xl shadow-2xl flex items-center gap-3 animate-[slideIn_0.3s_ease-out_forwards] z-50 transition-all ${
      type === 'success' ? 'bg-[#183a2d] text-white border-b-4 border-secondary-container' : 'bg-[#400e12] text-white border-b-4 border-error'
    }`}>
      <span className="material-symbols-outlined" style={{fontVariationSettings: "'FILL' 1"}}>
        {type === 'success' ? 'check_circle' : 'error'}
      </span>
      <p className="font-medium text-sm pr-6">{message}</p>
      {onClose && (
        <button onClick={onClose} className="absolute right-2 top-2 text-white/50 hover:text-white">
          <span className="material-symbols-outlined text-[16px]">close</span>
        </button>
      )}
    </div>
  );
};
