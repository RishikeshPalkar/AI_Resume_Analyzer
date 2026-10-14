import React from 'react';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../context/AuthContext';
import { analysisApi } from '../api/analysisApi';
import { PlusCircle, FileText, BarChart2, Calendar, ArrowRight, Sparkles } from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  const { data: stats, isLoading: statsLoading } = useQuery({
    queryKey: ['dashboard-stats'],
    queryFn: () => analysisApi.getStats(),
  });

  const { data: recentAnalyses, isLoading: analysesLoading } = useQuery({
    queryKey: ['recent-analyses'],
    queryFn: () => analysisApi.getAnalyses(0, 5),
  });

  const analysesList = recentAnalyses?.content || [];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Welcome Hero Banner */}
      <div className="bg-gradient-to-r from-indigo-700 to-indigo-900 rounded-2xl p-6 sm:p-8 text-white shadow-sm mb-8 flex flex-col md:flex-row md:items-center md:justify-between gap-6">
        <div>
          <div className="inline-flex items-center space-x-1.5 px-3 py-1 rounded-full bg-indigo-500/30 text-indigo-100 text-xs font-semibold mb-3">
            <Sparkles className="w-3.5 h-3.5" />
            <span>AI Resume Optimization Platform</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight">
            Welcome back, {user?.name || 'Engineer'}!
          </h1>
          <p className="mt-2 text-indigo-100 text-sm sm:text-base max-w-xl">
            Compare your resume against any tech job posting. Identify missing keywords, calculate alignment, and prepare for interviews.
          </p>
        </div>

        <Link
          to="/analyze"
          className="inline-flex items-center justify-center px-5 py-3 rounded-xl font-semibold text-indigo-900 bg-white hover:bg-indigo-50 shadow-sm transition-colors text-sm flex-shrink-0"
        >
          <PlusCircle className="w-4 h-4 mr-2 text-indigo-600" />
          <span>New Resume Match</span>
        </Link>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 mb-8">
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-slate-500">Total Analyses</span>
            <div className="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
              <FileText className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-3xl font-extrabold text-slate-900">
              {statsLoading ? '...' : stats?.totalAnalyses ?? 0}
            </span>
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-slate-500">Latest Match Score</span>
            <div className="p-2 bg-emerald-50 text-emerald-600 rounded-lg">
              <BarChart2 className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4 flex items-baseline">
            <span className="text-3xl font-extrabold text-slate-900">
              {analysesList.length > 0 && analysesList[0].matchScore !== null
                ? `${analysesList[0].matchScore}%`
                : 'N/A'}
            </span>
            {analysesList.length > 0 && analysesList[0].matchScore !== null && (
              <span className="ml-2 text-xs text-slate-500 font-medium">most recent</span>
            )}
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-slate-500">Account Status</span>
            <div className="p-2 bg-blue-50 text-blue-600 rounded-lg">
              <Calendar className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-lg font-semibold text-slate-800">Standard Tier</span>
            <p className="text-xs text-slate-500 mt-1">Unlimited analyses enabled</p>
          </div>
        </div>
      </div>

      {/* Recent Activity Table */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-xs overflow-hidden">
        <div className="px-6 py-5 border-b border-slate-100 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-slate-900">Recent Analyses</h2>
          {analysesList.length > 0 && (
            <Link to="/history" className="text-sm font-semibold text-indigo-600 hover:text-indigo-700 flex items-center">
              <span>View all</span>
              <ArrowRight className="w-4 h-4 ml-1" />
            </Link>
          )}
        </div>

        {analysesLoading ? (
          <div className="p-8 text-center text-slate-500 text-sm">Loading activity...</div>
        ) : analysesList.length === 0 ? (
          <div className="text-center py-16 px-4">
            <div className="w-12 h-12 bg-indigo-50 text-indigo-600 rounded-full flex items-center justify-center mx-auto mb-3">
              <PlusCircle className="w-6 h-6" />
            </div>
            <h3 className="text-base font-semibold text-slate-900">No analyses yet</h3>
            <p className="mt-1 text-sm text-slate-500 max-w-sm mx-auto">
              Upload your resume and test it against a job description to see your first deterministic score.
            </p>
            <Link
              to="/analyze"
              className="mt-5 inline-flex items-center px-4 py-2 text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg shadow-sm transition-colors"
            >
              Start Analysis Now
            </Link>
          </div>
        ) : (
          <div className="divide-y divide-slate-100">
            {analysesList.map((item) => (
              <div key={item.id} className="p-6 hover:bg-slate-50 transition-colors flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                <div className="flex items-start space-x-3.5">
                  <div className="p-2.5 bg-slate-100 text-slate-600 rounded-lg mt-0.5">
                    <FileText className="w-5 h-5" />
                  </div>
                  <div>
                    <h4 className="text-sm font-semibold text-slate-900">
                      {item.jobTitle || 'General Engineering Role'}
                    </h4>
                    <p className="text-xs text-slate-500 mt-0.5">
                      {item.company ? `${item.company} • ` : ''}File: {item.resumeFilename}
                    </p>
                    <span className="text-[11px] text-slate-400 mt-1 block">
                      {new Date(item.createdAt).toLocaleDateString(undefined, {
                        year: 'numeric',
                        month: 'short',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit',
                      })}
                    </span>
                  </div>
                </div>

                <div className="flex items-center space-x-4 self-end sm:self-auto">
                  <div className="text-right">
                    <span className="text-lg font-bold text-slate-900">
                      {item.matchScore !== null ? `${item.matchScore}%` : 'Pending'}
                    </span>
                    <span className="block text-[10px] text-slate-400 uppercase font-semibold">
                      Match Score
                    </span>
                  </div>

                  <Link
                    to={`/analyses/${item.id}`}
                    className="px-3.5 py-1.5 rounded-lg text-xs font-semibold text-indigo-600 hover:text-indigo-700 bg-indigo-50 hover:bg-indigo-100 transition-colors flex items-center"
                  >
                    <span>View Report</span>
                    <ArrowRight className="w-3.5 h-3.5 ml-1" />
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
