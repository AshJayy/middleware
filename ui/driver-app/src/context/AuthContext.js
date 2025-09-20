import React, { createContext, useContext, useState, useEffect } from 'react';
import { login as loginApi } from '../network/auth'; // Import the login function we just created

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [driver, setDriver] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('driver_token'));
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // This effect runs on startup to check if a user is already logged in
        // from a previous session.
        const storedDriver = localStorage.getItem('driver_details');
        if (token && storedDriver) {
            setDriver(JSON.parse(storedDriver));
        }
        setLoading(false);
    }, [token]);

    const login = async (email, password) => {
        try {
            const response = await loginApi(email, password);
            const { token, user } = response.data;

            // Store the token and user details
            localStorage.setItem('driver_token', token);
            localStorage.setItem('driver_details', JSON.stringify(user));

            setToken(token);
            setDriver(user);
        } catch (error) {
            console.error("Login failed:", error);
            // Handle login errors (e.g., show a message to the user)
            throw error;
        }
    };

    const logout = () => {
        localStorage.removeItem('driver_token');
        localStorage.removeItem('driver_details');
        setToken(null);
        setDriver(null);
    };

    const value = {
        driver,
        token,
        login,
        logout,
        isAuthenticated: !!token,
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};