import { apiClient, setAccessToken } from './client';
import { AuthResponse, UserProfile } from '../types';

export const authApi = {
  register: async (data: { name: string; email: string; password: string }): Promise<AuthResponse> => {
    const res = await apiClient<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
      skipAuth: true,
    });
    setAccessToken(res.accessToken);
    return res;
  },

  login: async (data: { email: string; password: string }): Promise<AuthResponse> => {
    const res = await apiClient<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
      skipAuth: true,
    });
    setAccessToken(res.accessToken);
    return res;
  },

  logout: async (): Promise<void> => {
    try {
      await apiClient<void>('/auth/logout', { method: 'POST' });
    } finally {
      setAccessToken(null);
    }
  },

  getCurrentUser: async (): Promise<UserProfile> => {
    return apiClient<UserProfile>('/auth/me');
  },

  changePassword: async (data: { currentPassword: string; newPassword: string }): Promise<void> => {
    return apiClient<void>('/auth/change-password', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },

  deleteAccount: async (): Promise<void> => {
    try {
      await apiClient<void>('/account/me', { method: 'DELETE' });
    } finally {
      setAccessToken(null);
    }
  },
};
