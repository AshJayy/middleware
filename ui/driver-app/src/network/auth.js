import apiClient from './network';

/**
 * Authenticates a driver using their email and password.
 *
 * @param {string} email - The driver's email address.
 * @param {string} password - The driver's password.
 * @returns {Promise<object>} A promise that resolves to the login response,
 * which includes the auth token and driver details.
 */
export const login = (email, password) => {
    if (!email || !password) {
        return Promise.reject(new Error('Email and password are required.'));
    }
    // UPDATED: This now calls the correct /api/auth/driver endpoint,
    // bypassing the default '/v1' baseURL of the apiClient.
    // We construct the full URL path from the root.
    return apiClient.post('/../auth/driver', { username: email, password });
};