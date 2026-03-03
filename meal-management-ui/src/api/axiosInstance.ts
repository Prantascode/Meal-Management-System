import axios from 'axios';

const api = axios.create({
  // source base URL
  baseURL: 'http://localhost:8080/api', 
  headers: {
    'Content-Type': 'application/json',
  },
});

/** REQUEST INTERCEPTOR*/
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

/** RESPONSE INTERCEPTOR*/
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      const refreshToken = localStorage.getItem('refreshToken');

      if (refreshToken) {
        try {
          const response = await axios.post(`${api.defaults.baseURL}/auth/refresh-token`, {
            refreshToken: refreshToken,
          });

          const { accessToken, refreshToken: newRefreshToken } = response.data;
          
          localStorage.setItem('accessToken', accessToken);
          if (newRefreshToken) localStorage.setItem('refreshToken', newRefreshToken);

          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return api(originalRequest); 
        } catch (refreshError) {
          handleLogout();
        }
      } else {
        handleLogout();
      }
    }

    // 2. Handle 403 Forbidden
    if (error.response?.status === 403) {
      console.error("Access Denied: Permissions missing.");
    }

    return Promise.reject(error);
  }
);

const handleLogout = () => {
  const role = localStorage.getItem('role');
  localStorage.clear();
  
  // Redirect logic
  if (role === 'ADMIN') {
    window.location.href = '/login';
  } else {
    window.location.href = '/member/login';
  }
};

export default api;