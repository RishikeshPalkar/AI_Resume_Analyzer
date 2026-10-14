import { apiClient } from './client';
import { PagedResponse, Resume, ResumeDetail } from '../types';

export const resumeApi = {
  uploadResume: async (file: File): Promise<ResumeDetail> => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient<ResumeDetail>('/resumes', {
      method: 'POST',
      body: formData,
    });
  },

  getResumes: async (page = 0, size = 10): Promise<PagedResponse<Resume>> => {
    return apiClient<PagedResponse<Resume>>(`/resumes?page=${page}&size=${size}`);
  },

  getResumeById: async (id: number): Promise<ResumeDetail> => {
    return apiClient<ResumeDetail>(`/resumes/${id}`);
  },

  deleteResume: async (id: number): Promise<void> => {
    return apiClient<void>(`/resumes/${id}`, {
      method: 'DELETE',
    });
  },
};
