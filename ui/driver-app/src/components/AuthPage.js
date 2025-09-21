import React, { useState } from 'react';
import Login from './Login';
import SignUp from './SignUp';
import { useAuth } from '../context/AuthContext';

const AuthPage = () => {
  const [showSignUp, setShowSignUp] = useState(false);
  const { isAuthenticated } = useAuth();

  if (isAuthenticated) return null; // Authenticated users should not see this

  return showSignUp ? (
    <SignUp onSwitchToLogin={() => setShowSignUp(false)} />
  ) : (
    <Login onSwitchToSignUp={() => setShowSignUp(true)} />
  );
};

export default AuthPage;

