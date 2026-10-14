import React from 'react';

interface ScoreGaugeProps {
  score: number;
  size?: number;
  strokeWidth?: number;
}

export const ScoreGauge: React.FC<ScoreGaugeProps> = ({
  score,
  size = 180,
  strokeWidth = 14,
}) => {
  const normalizedScore = Math.max(0, Math.min(100, Math.round(score)));
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (normalizedScore / 100) * circumference;

  let colorClass = 'text-rose-500';
  let bgFill = 'bg-rose-50';
  let label = 'Low Match';

  if (normalizedScore >= 75) {
    colorClass = 'text-emerald-500';
    bgFill = 'bg-emerald-50';
    label = 'Strong Match';
  } else if (normalizedScore >= 50) {
    colorClass = 'text-amber-500';
    bgFill = 'bg-amber-50';
    label = 'Moderate Match';
  }

  return (
    <div className="flex flex-col items-center">
      <div className="relative" style={{ width: size, height: size }}>
        <svg className="w-full h-full transform -rotate-90" viewBox={`0 0 ${size} ${size}`}>
          {/* Background Track */}
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="currentColor"
            strokeWidth={strokeWidth}
            className="text-slate-100"
            fill="transparent"
          />
          {/* Progress Circle */}
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="currentColor"
            strokeWidth={strokeWidth}
            className={`${colorClass} transition-all duration-1000 ease-out`}
            fill="transparent"
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
          />
        </svg>

        {/* Center Text */}
        <div className="absolute inset-0 flex flex-col items-center justify-center text-center">
          <span className="text-4xl font-extrabold text-slate-900 tracking-tight">
            {normalizedScore}%
          </span>
          <span className="text-xs uppercase tracking-wider font-semibold text-slate-400 mt-0.5">
            Match Score
          </span>
        </div>
      </div>

      <div className={`mt-3 px-3 py-1 rounded-full text-xs font-semibold ${bgFill} ${colorClass} border border-current/20`}>
        {label}
      </div>
    </div>
  );
};
