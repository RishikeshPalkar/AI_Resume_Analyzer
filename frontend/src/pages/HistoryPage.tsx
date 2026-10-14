import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { analysisApi } from '../api/analysisApi';
import { FileText, Trash2, ArrowRight, PlusCircle, AlertCircle, ChevronLeft, ChevronRight } from 'lucide-react';

export const HistoryPage: React.FC = () => {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [deleteId, setDeleteId] = useState<number | null>(null);

  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['analyses-history', page],
    queryFn: () => analysisApi.getAnalyses(page, 10),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => analysisApi.deleteAnalysis(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['analyses-history'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard-stats'] });
      setDeleteId(null);
    },
  });

  const analyses = data?.content || [];
  const totalPages = data?.totalPages || 0;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">Analysis History</h1>
          <p className="mt-1 text-sm text-slate-500">
            View, inspect, and manage your past resume and job description match reports.
          </p>
        </div>

        <Link
          to="/analyze"
          className="inline-flex items-center px-4 py-2.5 rounded-lg text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-sm transition-colors self-start sm:self-auto"
        >
          <PlusCircle className="w-4 h-4 mr-1.5" />
          <span>New Analysis</span>
        </Link>
      </div>

      {isError && (
        <div className="mb-6 p-4 rounded-xl bg-rose-50 border border-rose-200 flex items-center space-x-3 text-rose-700 text-sm">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <span>{error instanceof Error ? error.message : 'Failed to load history'}</span>
        </div>
      )}

      {/* Reports Table Card */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-xs overflow-hidden">
        {isLoading ? (
          <div className="p-12 text-center text-slate-500 text-sm">
            <div className="w-8 h-8 border-3 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-2"></div>
            Loading analysis history...
          </div>
        ) : analyses.length === 0 ? (
          <div className="text-center py-16 px-4">
            <div className="w-12 h-12 bg-slate-100 text-slate-400 rounded-full flex items-center justify-center mx-auto mb-3">
              <FileText className="w-6 h-6" />
            </div>
            <h3 className="text-base font-semibold text-slate-900">No reports found</h3>
            <p className="mt-1 text-sm text-slate-500 max-w-sm mx-auto">
              You haven't run any resume evaluations yet.
            </p>
            <Link
              to="/analyze"
              className="mt-5 inline-flex items-center px-4 py-2 text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg shadow-sm"
            >
              Start First Analysis
            </Link>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-100 text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 uppercase font-semibold">
                <tr>
                  <th className="px-6 py-3.5">Target Job</th>
                  <th className="px-6 py-3.5">Resume File</th>
                  <th className="px-6 py-3.5">Date</th>
                  <th className="px-6 py-3.5">Score</th>
                  <th className="px-6 py-3.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {analyses.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50/60 transition-colors">
                    <td className="px-6 py-4">
                      <span className="font-semibold text-slate-900 block text-sm">
                        {item.jobTitle || 'General Engineering Role'}
                      </span>
                      {item.company && (
                        <span className="text-slate-500 text-xs block">{item.company}</span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-slate-600">
                      <div className="flex items-center space-x-1.5">
                        <FileText className="w-3.5 h-3.5 text-slate-400" />
                        <span>{item.resumeFilename}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-slate-500">
                      {new Date(item.createdAt).toLocaleDateString(undefined, {
                        year: 'numeric',
                        month: 'short',
                        day: 'numeric',
                      })}
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm font-extrabold text-slate-900">
                        {item.matchScore !== null ? `${item.matchScore}%` : 'N/A'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right space-x-2">
                      <Link
                        to={`/analyses/${item.id}`}
                        className="inline-flex items-center px-3 py-1.5 rounded-lg text-xs font-semibold text-indigo-700 bg-indigo-50 hover:bg-indigo-100 transition-colors"
                      >
                        <span>View</span>
                        <ArrowRight className="w-3 h-3 ml-1" />
                      </Link>

                      <button
                        onClick={() => setDeleteId(item.id)}
                        className="inline-flex items-center px-2.5 py-1.5 rounded-lg text-xs font-medium text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-colors"
                        title="Delete report"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination Bar */}
        {totalPages > 1 && (
          <div className="px-6 py-4 border-t border-slate-100 flex items-center justify-between">
            <span className="text-xs text-slate-500">
              Page {page + 1} of {totalPages}
            </span>
            <div className="flex space-x-1">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="p-1.5 rounded-md border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-40"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <button
                onClick={() => setPage((p) => (data?.last ? p : p + 1))}
                disabled={data?.last}
                className="p-1.5 rounded-md border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-40"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Delete Confirmation Modal */}
      {deleteId !== null && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl border border-slate-200">
            <h3 className="text-lg font-bold text-slate-900 mb-2">Delete Analysis Report?</h3>
            <p className="text-sm text-slate-600 mb-6">
              Are you sure you want to delete this analysis report? This action cannot be undone.
            </p>
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setDeleteId(null)}
                className="px-4 py-2 rounded-lg text-sm font-semibold text-slate-700 hover:bg-slate-100 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={() => deleteMutation.mutate(deleteId)}
                disabled={deleteMutation.isPending}
                className="px-4 py-2 rounded-lg text-sm font-semibold text-white bg-rose-600 hover:bg-rose-700 shadow-sm transition-colors"
              >
                {deleteMutation.isPending ? 'Deleting...' : 'Delete Report'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
