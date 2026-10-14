const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

let currentAccessToken: string | null = localStorage.getItem('token');

export const setAccessToken = (token: string | null) => {
  currentAccessToken = token;
  if (token) {
    localStorage.setItem('token', token);
  } else {
    localStorage.removeItem('token');
  }
};

export const getAccessToken = () => currentAccessToken;

interface RequestOptions extends RequestInit {
  skipAuth?: boolean;
}

export async function apiClient<T>(endpoint: string, options: RequestOptions = {}): Promise<T> {
  const url = `${BASE_URL}${endpoint.startsWith('/') ? endpoint : `/${endpoint}`}`;
  
  const headers = new Headers(options.headers || {});
  if (!options.skipAuth && currentAccessToken) {
    headers.set('Authorization', `Bearer ${currentAccessToken}`);
  }
  if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  const response = await fetch(url, {
    ...options,
    headers,
    credentials: 'include', // sends refresh token cookie
  });

  if (response.status === 401 && !options.skipAuth && !endpoint.includes('/auth/login') && !endpoint.includes('/auth/refresh')) {
    // Attempt automatic refresh
    try {
      const refreshResponse = await fetch(`${BASE_URL}/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
      });
      if (refreshResponse.ok) {
        const refreshData = await refreshResponse.json();
        const newToken = refreshData.data?.accessToken;
        if (newToken) {
          setAccessToken(newToken);
          // Retry original request
          headers.set('Authorization', `Bearer ${newToken}`);
          const retryResponse = await fetch(url, { ...options, headers, credentials: 'include' });
          if (!retryResponse.ok) {
            const err = await retryResponse.json().catch(() => ({ message: 'Request failed' }));
            throw new Error(err.message || 'Request failed');
          }
          const retryData = await retryResponse.json();
          return retryData.data !== undefined ? retryData.data : retryData;
        }
      }
    } catch {
      setAccessToken(null);
      window.dispatchEvent(new Event('auth:unauthorized'));
    }
  }

  if (!response.ok) {
    const errorJson = await response.json().catch(() => ({
      message: `HTTP Error ${response.status}: ${response.statusText}`,
      errorCode: 'UNKNOWN_ERROR',
    }));
    throw new Error(errorJson.message || 'An unexpected error occurred');
  }

  // If status is 204 No Content
  if (response.status === 204) {
    return {} as T;
  }

  const json = await response.json();
  return json.data !== undefined ? json.data : json;
}
