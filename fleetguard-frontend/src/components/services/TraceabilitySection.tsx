import React from 'react';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { InputField } from '@/components/ui/InputField';

interface TraceabilitySectionProps {
  mileageAtService: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => void;
}

export const TraceabilitySection: React.FC<TraceabilitySectionProps> = ({
  mileageAtService,
  onChange,
}) => {
  return (
    <section>
      <SectionHeader icon="speed" title="Trazabilidad" />
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <InputField
          label="Kilometraje al Momento del Servicio"
          name="mileageAtService"
          value={mileageAtService}
          onChange={onChange}
          type="number"
          placeholder="0"
          required
          min={1}
          suffix="KM"
          onWheel={(e) => e.currentTarget.blur()}
        />
      </div>
    </section>
  );
};