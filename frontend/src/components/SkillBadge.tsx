import React from 'react';
import { CheckCircle2, AlertCircle, XCircle } from 'lucide-react';

interface SkillBadgeProps {
  name: string;
  category?: string;
  status: 'MATCHED' | 'PARTIAL' | 'MISSING';
  importance?: 'REQUIRED' | 'PREFERRED';
}

export const SkillBadge: React.FC<SkillBadgeProps> = ({
  name,
  category,
  status,
  importance,
}) => {
  const getStatusStyles = () => {
    switch (status) {
      case 'MATCHED':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';
      case 'PARTIAL':
        return 'bg-amber-50 text-amber-700 border-amber-200';
      case 'MISSING':
        return 'bg-rose-50 text-rose-700 border-rose-200';
    }
  };

  const getIcon = () => {
    switch (status) {
      case 'MATCHED':
        return <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />;
      case 'PARTIAL':
        return <AlertCircle className="w-3.5 h-3.5 text-amber-600" />;
      case 'MISSING':
        return <XCircle className="w-3.5 h-3.5 text-rose-600" />;
    }
  };

  return (
    <div
      className={`inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg border text-xs font-medium shadow-xs transition-colors ${getStatusStyles()}`}
    >
      {getIcon()}
      <span className="font-semibold">{name}</span>
      {category && (
        <span className="text-[10px] uppercase opacity-75 font-normal ml-1">({category})</span>
      )}
      {importance === 'PREFERRED' && (
        <span className="text-[10px] bg-slate-200/60 px-1 rounded text-slate-600 ml-1">Nice-to-have</span>
      )}
    </div>
  );
};
