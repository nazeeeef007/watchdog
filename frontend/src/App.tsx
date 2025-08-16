import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

// Import all page components using named exports
import { LandingPage } from './pages/LandingPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { CreateMonitorPage } from './pages/CreateMonitorPage';
import { AlertHistoryPage } from './pages/AlertHistoryPage';
import AlertConfigPage from './pages/AlertConfigPage'; // Import the new AlertConfigPage
import MonitorCheckPage from './pages/MonitorCheckPage';

const App: React.FC = () => {
  const { isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg font-medium text-gray-700">Loading...</div>
      </div>
    );
  }

  return (
    <Routes>
      {/* Public routes visible to all users */}
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      {/* Protected routes wrapped by ProtectedRoute */}
      <Route element={<ProtectedRoute />}>
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/create-monitor/:monitorId?" element={<CreateMonitorPage />} />
        
        {/* Dynamic route for viewing alert history for a specific monitor */}
        <Route path="/alert-history/:monitorId?" element={<AlertHistoryPage />} />

        {/* This is the new dynamic route for managing alert configurations */}
        <Route path="/alert-config/:monitorId" element={<AlertConfigPage />} />
        <Route path="/monitor-check/:monitorId?" element={<MonitorCheckPage />} />
        

      </Route>

      {/* Fallback route for 404 pages */}
      <Route path="*" element={
        <div className="flex flex-col items-center justify-center min-h-screen text-center bg-gray-50">
          <h1 className="text-4xl font-bold text-gray-900">404 - Page Not Found</h1>
          <p className="mt-2 text-lg text-gray-600">The page you're looking for doesn't exist.</p>
        </div>
      } />
    </Routes>
  );
};

export default App;
