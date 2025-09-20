import axios from 'axios';

// The base URL for all API requests, pointing to your orchestrator service.
const API_BASE_URL = 'http://localhost:8000/api/v1';

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor to add the authentication token to every request.
// The driver app will need a login system to get this token.
apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('driver_token'); // Or however you choose to store the token
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default apiClient;