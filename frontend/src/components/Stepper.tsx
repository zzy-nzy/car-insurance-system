import React from 'react';

const STEPS = ['使用者情報', '契約中保険', '車両情報', '補償条件', '確認'];

interface Props { currentStep: number; }

export default function Stepper({ currentStep }: Props) {
  if (currentStep < 1 || currentStep > 5) return null;
  return (
    <div className="stepper">
      {STEPS.map((label, i) => {
        const idx = i + 1;
        const cls = idx === currentStep ? 'active' : idx < currentStep ? 'completed' : '';
        return (
          <div key={i} className={`stepper-step ${cls}`}>
            <span className="step-num">{idx < currentStep ? '✓' : idx}</span>
            <span className="step-label">{label}</span>
          </div>
        );
      })}
    </div>
  );
}
