import React, { createContext, useState, useEffect } from 'react';
import { apiRequest } from '../api/http';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [email, setEmail] = useState(localStorage.getItem('email'));
  const [isAdmin, setIsAdmin] = useState(localStorage.getItem('isAdmin') === 'true');
  const [loadingProfile, setLoadingProfile] = useState(false);
  const [userName, setUserName] = useState(localStorage.getItem('userName') || '');
  const [userStatus, setUserStatus] = useState(localStorage.getItem('userStatus') || '');

  const login = async (email, password) => {
    const data = await apiRequest('/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
    localStorage.setItem('token', data.token);
    localStorage.setItem('email', email);
    setToken(data.token);
    setEmail(email);
    await refreshProfile(data.token);
  };

  const refreshProfile = async (tok = token) => {
    if (!tok) return;
    setLoadingProfile(true);
    try {
      const me = await apiRequest('/me', {}, tok);
      // roles may be comma-separated string
  const roles = (me.roles ? me.roles.split(',') : (me.authorities || []));
      const admin = roles.includes('ROLE_ADMIN');
      setIsAdmin(admin);
      localStorage.setItem('isAdmin', String(admin));
  if (me.name) { setUserName(me.name); localStorage.setItem('userName', me.name); }
  if (me.status) { setUserStatus(me.status); localStorage.setItem('userStatus', me.status); }
    } catch (e) {
      console.warn('Profile fetch failed', e.message);
    } finally {
      setLoadingProfile(false);
    }
  };

  const logout = () => {
    localStorage.clear();
    setToken(null); setEmail(null); setIsAdmin(false); setUserName(''); setUserStatus('');
  };

  useEffect(() => { if (token) refreshProfile(token); }, []); // initial load

  return (
    <AuthContext.Provider value={{ token, email, isAdmin, userName, userStatus, login, logout, refreshProfile, loadingProfile }}>
      {children}
    </AuthContext.Provider>
  );
};
