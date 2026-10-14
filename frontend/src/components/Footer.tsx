import React from 'react';
import { ShieldCheck } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-white border-t border-slate-200 mt-auto py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row items-center justify-between text-sm text-slate-500 gap-4">
          <div className="flex items-center space-x-2">
            <ShieldCheck className="w-4 h-4 text-emerald-600" />
            <span>Secure document extraction with Apache PDFBox & POI. LLM analysis via Spring AI.</span>
          </div>
          <div className="text-center md:text-right">
            <p>© {new Date().getFullYear()} AI Resume Analyzer & Job Matcher. Production Portfolio Project.</p>
            <p className="text-xs text-slate-400 mt-0.5">Automated match estimates are advisory and not official employer hiring decisions.</p>
          </div>
        </div>
      </div>
    </footer>
  );
};
