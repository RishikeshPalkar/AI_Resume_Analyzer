import { apiClient } from './client';
import { JobDescription, PagedResponse } from '../types';

export const jobApi = {
  createJobDescription: async (data: { title?: string; company?: string; descriptionText: string }): Promise<JobDescription> => {
    return apiClient<JobDescription>('/job-descriptions', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },

  getJobDescriptions: async (page = 0, size = 10): Promise<PagedResponse<JobDescription>> => {
    return apiClient<PagedResponse<JobDescription>>(`/job-descriptions?page=${page}&size=${size}`);
  },

  getJobDescriptionById: async (id: number): Promise<JobDescription> => {
    return apiClient<JobDescription>(`/job-descriptions/${id}`);
  },

  deleteJobDescription: async (id: number): Promise<void> => {
    return apiClient<void>(`/job-descriptions/${id}`, {
      method: 'DELETE',
    });
  },
};
