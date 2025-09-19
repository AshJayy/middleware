import React, { createContext, useContext, useMemo, useState, useCallback } from 'react';
import { authEndpoints } from '../network/auth';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  // Persist only in-memory until refresh as requested
  const [customer, setCustomer] = useState(null);

  const signin = useCallback(async (username, password) => {
    const payload = { username, password };
    const res = await authEndpoints.signin(payload);
    // Support either { customer: {...} } or the customer object directly
    const cust = res?.customer ?? res ?? null;
    setCustomer(cust);
    return cust;
  }, []);

  const signout = useCallback(() => setCustomer(null), []);

  const getCustomerId = useCallback(() => customer?.customerId ?? customer?.id ?? null, [customer]);

  const value = useMemo(() => ({
    customer,
    isAuthenticated: !!customer,
    signin,
    signout,
    getCustomerId,
  }), [customer, signin, signout, getCustomerId]);

  return (
    <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider');
  return ctx;
};

export default AuthContext;

