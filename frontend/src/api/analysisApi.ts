import { apiClient } from './client';
import { AnalysisDetail, AnalysisSummary, DashboardStats, PagedResponse } from '../types';

export const analysisApi = {
  createAnalysis: async (data: {
    resumeId: number;
    jobDescriptionId?: number;
    jobTitle?: string;
    company?: string;
    jobDescriptionText?: string;
  }): Promise<AnalysisDetail> => {
    return apiClient<AnalysisDetail>('/analyses', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },

  getAnalyses: async (page = 0, size = 10): Promise<PagedResponse<AnalysisSummary>> => {
    return apiClient<PagedResponse<AnalysisSummary>>(`/analyses?page=${page}&size=${size}`);
  },

  getAnalysisById: async (id: number): Promise<AnalysisDetail> => {
    return apiClient<AnalysisDetail>(`/analyses/${id}`);
  },

  deleteAnalysis: async (id: number): Promise<void> => {
    return apiClient<void>(`/analyses/${id}`, {
      method: 'DELETE',
    });
  },

  getStats: async (): Promise<DashboardStats> => {
    return apiClient<DashboardStats>('/analyses/stats');
  },
};
