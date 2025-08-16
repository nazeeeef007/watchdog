// src/api/authService.ts

import type { LoginRequest } from "@/types/LoginRequest";
import type { AuthenticationResponse } from "@/types/AuthenticationResponse";
import type { UserRegistrationRequest } from "@/types/UserRegistrationRequest";
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthenticationResponse> => {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Login failed');
    }

    return response.json();
  },

  register: async (user: UserRegistrationRequest): Promise<AuthenticationResponse> => {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(user),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Registration failed');
    }

    return response.json();
  },

  getToken: () => {
    console.log('AuthService: Retrieving token from localStorage.');
    return localStorage.getItem('authToken');
  },

  logout: () => {
    console.log('AuthService: Removing token from localStorage.');
    localStorage.removeItem('authToken');
  }
};