import React from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { analysisApi } from '../api/analysisApi';
import { ScoreGauge } from '../components/ScoreGauge';
import { SkillBadge } from '../components/SkillBadge';
import {
  ArrowLeft,
  PlusCircle,
  CheckCircle,
  XCircle,
  AlertTriangle,
  Lightbulb,
  Key,
  ShieldAlert,
  Info,
} from 'lucide-react';

export const AnalysisResultPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const { data: analysis, isLoading, isError, error } = useQuery({
    queryKey: ['analysis-report', id],
    queryFn: () => analysisApi.getAnalysisById(Number(id)),
    enabled: !!id,
  });

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-16 text-center">
        <div className="w-10 h-10 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
        <p className="text-slate-600 font-medium">Retrieving analysis report...</p>
      </div>
    );
  }

  if (isError || !analysis) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-16 text-center">
        <div className="p-4 bg-rose-50 text-rose-700 rounded-xl border border-rose-200 mb-6">
          <p className="font-semibold">Unable to load report</p>
          <p className="text-sm mt-1">{error instanceof Error ? error.message : 'Report not found'}</p>
        </div>
        <button
          onClick={() => navigate('/history')}
          className="inline-flex items-center text-sm font-semibold text-indigo-600 hover:text-indigo-700"
        >
          <ArrowLeft className="w-4 h-4 mr-1" />
          Back to History
        </button>
      </div>
    );
  }

  const breakdown = analysis.scoreBreakdown;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Navigation & Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
        <div>
          <Link
            to="/history"
            className="inline-flex items-center text-xs font-semibold text-slate-500 hover:text-slate-800 mb-2 transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5 mr-1" />
            Back to Analysis History
          </Link>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
            Analysis Report: {analysis.jobTitle || 'Target Role'}
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 mt-1">
            {analysis.company ? `${analysis.company} • ` : ''}Document: {analysis.resumeFilename} •{' '}
            {new Date(analysis.createdAt).toLocaleDateString(undefined, {
              year: 'numeric',
              month: 'long',
              day: 'numeric',
            })}
          </p>
        </div>

        <Link
          to="/analyze"
          className="inline-flex items-center px-4 py-2 rounded-lg text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-sm transition-colors self-start sm:self-auto"
        >
          <PlusCircle className="w-4 h-4 mr-1.5" />
          <span>New Analysis</span>
        </Link>
      </div>

      {/* Main Grid: Score Gauge & Executive Summary */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-8">
        {/* Score Card */}
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col items-center justify-center">
          <ScoreGauge score={analysis.matchScore} size={190} />

          <div className="w-full mt-6 pt-5 border-t border-slate-100 text-xs text-slate-500 space-y-2">
            <div className="flex justify-between">
              <span>Required Skills Weight:</span>
              <span className="font-semibold text-slate-700">50%</span>
            </div>
            <div className="flex justify-between">
              <span>Experience Evidence Weight:</span>
              <span className="font-semibold text-slate-700">25%</span>
            </div>
            <div className="flex justify-between">
              <span>Qualifications Weight:</span>
              <span className="font-semibold text-slate-700">15%</span>
            </div>
            <div className="flex justify-between">
              <span>Preferred Skills Weight:</span>
              <span className="font-semibold text-slate-700">10%</span>
            </div>
          </div>
        </div>

        {/* Executive Summary & Detailed Score Breakdown */}
        <div className="lg:col-span-2 bg-white p-6 sm:p-8 rounded-2xl border border-slate-200 shadow-xs flex flex-col justify-between">
          <div>
            <div className="flex items-center space-x-2 text-indigo-600 font-semibold text-sm mb-3">
              <Info className="w-4 h-4" />
              <span>Executive Alignment Summary</span>
            </div>
            <p className="text-slate-700 text-sm leading-relaxed mb-6">
              {analysis.summary}
            </p>
          </div>

          {breakdown && (
            <div className="bg-slate-50 p-5 rounded-xl border border-slate-200">
              <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider mb-3">
                Deterministic Score Calculation
              </h4>
              <p className="text-xs font-mono text-slate-600 mb-4 bg-white p-3 rounded-lg border border-slate-200 overflow-x-auto">
                {breakdown.formulaExplanation}
              </p>

              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-center">
                <div className="bg-white p-2.5 rounded-lg border border-slate-200">
                  <span className="block text-[11px] text-slate-500">Required</span>
                  <span className="text-sm font-bold text-slate-900">{breakdown.requiredSkillsScore} pts</span>
                </div>
                <div className="bg-white p-2.5 rounded-lg border border-slate-200">
                  <span className="block text-[11px] text-slate-500">Experience</span>
                  <span className="text-sm font-bold text-slate-900">{breakdown.experienceEvidenceScore} pts</span>
                </div>
                <div className="bg-white p-2.5 rounded-lg border border-slate-200">
                  <span className="block text-[11px] text-slate-500">Qualifications</span>
                  <span className="text-sm font-bold text-slate-900">{breakdown.qualificationsScore} pts</span>
                </div>
                <div className="bg-white p-2.5 rounded-lg border border-slate-200">
                  <span className="block text-[11px] text-slate-500">Preferred</span>
                  <span className="text-sm font-bold text-slate-900">{breakdown.preferredSkillsScore} pts</span>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Skills Gap Analysis */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        {/* Matched Skills */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center space-x-2 text-emerald-600 font-semibold text-sm mb-4">
            <CheckCircle className="w-4 h-4" />
            <span>Matched Skills ({analysis.matchingSkills.length})</span>
          </div>
          {analysis.matchingSkills.length === 0 ? (
            <p className="text-xs text-slate-400 italic">No matching skills identified</p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {analysis.matchingSkills.map((s, idx) => (
                <SkillBadge key={idx} name={s} status="MATCHED" />
              ))}
            </div>
          )}
        </div>

        {/* Missing Required Skills */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center space-x-2 text-rose-600 font-semibold text-sm mb-4">
            <XCircle className="w-4 h-4" />
            <span>Missing Required Skills ({analysis.missingRequiredSkills.length})</span>
          </div>
          {analysis.missingRequiredSkills.length === 0 ? (
            <p className="text-xs text-emerald-600 font-medium">All required skills present in resume!</p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {analysis.missingRequiredSkills.map((s, idx) => (
                <SkillBadge key={idx} name={s} status="MISSING" importance="REQUIRED" />
              ))}
            </div>
          )}
        </div>

        {/* Missing Preferred Skills */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center space-x-2 text-amber-600 font-semibold text-sm mb-4">
            <AlertTriangle className="w-4 h-4" />
            <span>Nice-to-Have Skills ({analysis.missingPreferredSkills.length})</span>
          </div>
          {analysis.missingPreferredSkills.length === 0 ? (
            <p className="text-xs text-slate-400 italic">All preferred competencies covered</p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {analysis.missingPreferredSkills.map((s, idx) => (
                <SkillBadge key={idx} name={s} status="MISSING" importance="PREFERRED" />
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Strengths and Recommendations */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center space-x-2 text-indigo-600 font-semibold text-sm mb-4">
            <CheckCircle className="w-4 h-4" />
            <span>Resume Strengths</span>
          </div>
          <ul className="space-y-3">
            {analysis.strengths.map((str, idx) => (
              <li key={idx} className="flex items-start text-xs sm:text-sm text-slate-700">
                <span className="w-1.5 h-1.5 rounded-full bg-indigo-500 mt-2 mr-2.5 flex-shrink-0"></span>
                <span>{str}</span>
              </li>
            ))}
          </ul>
        </div>

        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center space-x-2 text-amber-600 font-semibold text-sm mb-4">
            <Lightbulb className="w-4 h-4" />
            <span>Actionable Suggestions for Improvement</span>
          </div>
          <ul className="space-y-3">
            {analysis.recommendations.map((rec, idx) => (
              <li key={idx} className="flex items-start text-xs sm:text-sm text-slate-700">
                <span className="w-1.5 h-1.5 rounded-full bg-amber-500 mt-2 mr-2.5 flex-shrink-0"></span>
                <span>{rec}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* Keywords & Evidence Table */}
      {analysis.keywordAnalysis.length > 0 && (
        <div className="bg-white rounded-xl border border-slate-200 shadow-xs overflow-hidden mb-8">
          <div className="px-6 py-4 border-b border-slate-100 flex items-center space-x-2">
            <Key className="w-4 h-4 text-indigo-600" />
            <h3 className="font-semibold text-sm text-slate-900">Keyword Presence & Evidence Trace</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-100 text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 uppercase font-semibold">
                <tr>
                  <th className="px-6 py-3">Keyword / Competency</th>
                  <th className="px-6 py-3">Status</th>
                  <th className="px-6 py-3">Resume Evidence / Context</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {analysis.keywordAnalysis.map((k, idx) => (
                  <tr key={idx} className="hover:bg-slate-50/60">
                    <td className="px-6 py-3 font-medium text-slate-900">{k.keyword}</td>
                    <td className="px-6 py-3">
                      {k.foundInResume ? (
                        <span className="inline-flex items-center text-emerald-700 font-semibold">
                          <CheckCircle className="w-3.5 h-3.5 mr-1 text-emerald-500" /> Present
                        </span>
                      ) : (
                        <span className="inline-flex items-center text-rose-700 font-semibold">
                          <XCircle className="w-3.5 h-3.5 mr-1 text-rose-500" /> Missing
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-3 text-slate-600">{k.context}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Disclaimer Card */}
      <div className="bg-slate-50 border border-slate-200 rounded-xl p-5 flex items-start space-x-3 text-xs text-slate-500 leading-relaxed">
        <ShieldAlert className="w-5 h-5 text-slate-400 flex-shrink-0 mt-0.5" />
        <div>
          <span className="font-semibold text-slate-700 block mb-0.5">Automated Match Estimate Disclaimer</span>
          <span>{analysis.disclaimer}</span>
        </div>
      </div>
    </div>
  );
};
