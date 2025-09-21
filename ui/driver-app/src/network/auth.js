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
    return apiClient.post('/auth/driver', { username: email, password });
};

/**
 * Registers a new driver.
 *
 * @param {object} driver - The driver details (name, email, password).
 * @returns {Promise<object>} A promise that resolves to the signup response.
 */
export const signup = (driver) => {
    // if (!name || !email || !password) {
    //     return Promise.reject(new Error('Name, email, and password are required.'));
    // }
    return apiClient.post('/auth/driver-sign-up', driver);
};

/**
 * Logs out the currently authenticated driver.
 *
 * @returns {Promise<object>} A promise that resolves to the logout response.
 */
export const logout = () => {
    return apiClient.post('/driver-logout');
};
