import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

/** REQUEST INTERCEPTOR */
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('accessToken');

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

/** RESPONSE INTERCEPTOR */
api.interceptors.response.use(
  (response) => response,

  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
    };

    // If request config is missing, reject safely
    if (!originalRequest) {
      return Promise.reject(error);
    }

    // 1. Handle 401 Unauthorized
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem('refreshToken');

      if (refreshToken) {
        try {
          const response = await axios.post(
            `${api.defaults.baseURL}/auth/refresh-token`,
            {
              refreshToken,
            }
          );

          const {
            accessToken,
            refreshToken: newRefreshToken,
            role,
            email,
          } = response.data;

          if (!accessToken) {
            handleLogout();
            return Promise.reject(error);
          }

          localStorage.setItem('accessToken', accessToken);

          if (newRefreshToken) {
            localStorage.setItem('refreshToken', newRefreshToken);
          }

          if (role) {
            localStorage.setItem('role', role);
          }

          if (email) {
            localStorage.setItem('email', email);
          }

          originalRequest.headers.Authorization = `Bearer ${accessToken}`;

          return api(originalRequest);
        } catch (refreshError) {
          console.error('Refresh token failed, logging out...');
          handleLogout();
          return Promise.reject(refreshError);
        }
      }

      handleLogout();
    }

    // 2. Handle 403 Forbidden
    if (error.response?.status === 403) {
      console.error(
        "403 Forbidden: Your token is valid, but you don't have permission for this resource."
      );
    }

    return Promise.reject(error);
  }
);

const handleLogout = () => {
  localStorage.clear();
  window.location.href = '/login';
};

export default api;