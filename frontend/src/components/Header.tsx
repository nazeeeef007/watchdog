// src/components/Header.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from './ui/button';
import { LogIn, UserPlus } from 'lucide-react';
import watchdogLogo from '../assets/watchdog.png';

export const Header: React.FC = () => {
  const navigate = useNavigate();

  return (
    <header className="sticky top-0 z-50 w-full p-4 bg-white shadow-sm">
      <div className="container flex items-center justify-between mx-auto">
        {/* Logo + Title */}
        <div
          className="flex items-center space-x-2 cursor-pointer"
          onClick={() => navigate('/login')}
        >
          <img src={watchdogLogo} alt="Watchdog Logo" className="w-8 h-8" />
          <h1 className="text-2xl font-bold text-gray-800">Watchdog</h1>
        </div>

        <nav className="flex items-center space-x-4">
          <Button
            onClick={() => navigate('/login')}
            className="flex items-center"
            variant="ghost"
          >
            <LogIn className="w-4 h-4 mr-2" />
            Login
          </Button>
          <Button
            onClick={() => navigate('/register')}
            className="flex items-center"
          >
            <UserPlus className="w-4 h-4 mr-2" />
            Register
          </Button>
        </nav>
      </div>
    </header>
  );
};
