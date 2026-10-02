import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });

  const [token, setToken] = useState(() => localStorage.getItem('jwtToken') || null);

  const login = (authData) => {
    setUser({
      id: authData.id,
      email: authData.email,
      fullName: authData.fullName,
      role: authData.role
    });
    setToken(authData.token);
    localStorage.setItem('jwtToken', authData.token);
    localStorage.setItem('user', JSON.stringify({
      id: authData.id,
      email: authData.email,
      fullName: authData.fullName,
      role: authData.role
    }));
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
