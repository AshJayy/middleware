import React, { createContext, useContext, useState } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [driver, setDriver] = useState({
        id: 'D-123', // A hardcoded, fake driver ID for testing
        name: 'John Driver',
        email: 'john.driver@swiftlogistics.com'
    });

    // In the future, a real login function would set this object.
    const value = {
        driver,
        // login: (email, password) => { /* Future login logic */ },
        // logout: () => { /* Future logout logic */ }
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
    return useContext(AuthContext);
};