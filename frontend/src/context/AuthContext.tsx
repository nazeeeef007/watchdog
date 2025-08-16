// src/contexts/AuthContext.tsx

import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import type { ReactNode } from 'react';
import { authService } from '../api/authService';
import type { LoginRequest } from '@/types/LoginRequest';

import type { UserRegistrationRequest } from '@/types/UserRegistrationRequest';

import type { AuthenticationResponse } from '@/types/AuthenticationResponse';

// Define the shape of the authentication context
interface AuthContextType {
  isAuthenticated: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: UserRegistrationRequest) => Promise<void>;
  logout: () => void;
  token: string | null;
  isLoading: boolean; // Added isLoading property
}

// Create the AuthContext
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// AuthProvider component to wrap your application or parts of it
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true); // Initialize isLoading to true

  // Effect to check authentication status on initial load (e.g., after page refresh)
  useEffect(() => {
    const checkAuthStatus = () => {
      const storedToken = authService.getToken();
      if (storedToken) {
        setIsAuthenticated(true);
        setToken(storedToken);
      } else {
        setIsAuthenticated(false);
        setToken(null);
      }
      setIsLoading(false); // Set loading to false after the check is complete
    };

    checkAuthStatus();
  }, []); // Empty dependency array means this runs once on mount

  // Callback for user login
  const login = useCallback(async (credentials: LoginRequest) => {
    setIsLoading(true); // Set loading to true at the start
    try {
      const response: AuthenticationResponse = await authService.login(credentials);
      setIsAuthenticated(true);
      setToken(response.token);
      // Store token in localStorage immediately after successful login
      localStorage.setItem('authToken', response.token);
    } catch (error) {
      setIsAuthenticated(false);
      setToken(null);
      // Remove token from localStorage on failed login attempt
      localStorage.removeItem('authToken');
      console.error('Login failed in AuthContext:', error);
      throw error;
    } finally {
      setIsLoading(false); // Set loading to false when the promise resolves or rejects
    }
  }, []);

  // Callback for user registration
  const register = useCallback(async (userData: UserRegistrationRequest) => {
    setIsLoading(true); // Set loading to true at the start
    try {
      const response: AuthenticationResponse = await authService.register(userData);
      setIsAuthenticated(true);
      setToken(response.token);
      // Store token in localStorage immediately after successful registration
      localStorage.setItem('authToken', response.token);
    } catch (error) {
      setIsAuthenticated(false);
      setToken(null);
      // Remove token from localStorage on failed registration attempt
      localStorage.removeItem('authToken');
      console.error('Registration failed in AuthContext:', error);
      throw error;
    } finally {
      setIsLoading(false); // Set loading to false when the promise resolves or rejects
    }
  }, []);

  // Callback for user logout
  const logout = useCallback(() => {
    authService.logout(); // This already removes from localStorage
    setIsAuthenticated(false);
    setToken(null);
  }, []);

  // The value provided to consumers of this context
  const contextValue = React.useMemo(() => ({
    isAuthenticated,
    login,
    register,
    logout,
    token,
    isLoading, // Included isLoading in the context value
  }), [isAuthenticated, login, register, logout, token, isLoading]);

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to easily consume the AuthContext
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
