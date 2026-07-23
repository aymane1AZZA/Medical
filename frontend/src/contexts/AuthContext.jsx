import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { login as loginRequest } from '../services/authService';
import { clearSession, isTokenExpired, readToken, readUser, saveSession } from '../utils/authStorage';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => readToken());
  const [user, setUser] = useState(() => readUser());
  const [bootstrapped, setBootstrapped] = useState(false);

  useEffect(() => {
    if (token && isTokenExpired(token)) {
      clearSession();
      setToken(null);
      setUser(null);
    }
    setBootstrapped(true);
  }, [token]);

  const signIn = useCallback(async (values) => {
    const response = await loginRequest(values);
    saveSession(response);
    setToken(response.token);
    setUser(response.user);
    return response.user;
  }, []);

  const signOut = useCallback(() => {
    clearSession();
    setToken(null);
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({
      token,
      user,
      isAuthenticated: Boolean(token && user && !isTokenExpired(token)),
      bootstrapped,
      signIn,
      signOut,
    }),
    [bootstrapped, signIn, signOut, token, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth doit être utilisé dans AuthProvider.');
  }
  return context;
}
