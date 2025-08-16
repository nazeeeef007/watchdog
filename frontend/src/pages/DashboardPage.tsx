// src/pages/DashboardPage.tsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { PlusCircle } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { MainLayout } from "../components/MainLayout";
import { MonitorCard } from "../components/MonitorCard";
import { Button } from "../components/ui/button";
import { Alert, AlertDescription } from "../components/ui/alert";
import { Card } from "../components/ui/card";
import { useMonitors } from '../hooks/useMonitors'; // Import the new custom hook


export const DashboardPage: React.FC = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const { monitors, isLoading, error, deleteMonitor } = useMonitors();

  // A helper function to handle navigation for a specific monitor
  const handleViewHistory = (monitorId: number) => {
    navigate(`/alert-history/${monitorId}`);
  };

  const handleViewConfig = (monitorId: number) => {
    navigate(`/alert-config/${monitorId}`)
  }

  const handleUpdateMonitor = (monitorId: number) => {
    navigate(`/create-monitor/${monitorId}`);
  }

  const handleDeleteMonitor = (monitorId: number) => {
    deleteMonitor(monitorId);
  }

  const handleViewChecks = (monitorId: number) => {
    navigate(`/monitor-check/${monitorId}`);
  }

  return (
    <MainLayout onLogout={logout}>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-3xl font-bold text-gray-900">Your Monitors</h2>
        <Button onClick={() => navigate('/create-monitor')} className="w-auto">
          <PlusCircle className="w-5 h-5 mr-2" />
          Create Monitor
        </Button>
      </div>
      {isLoading && (
        <div className="text-center text-gray-600">
          Loading monitors...
        </div>
      )}
      {error && (
        <Alert variant="destructive" className="mb-4">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}
      {!isLoading && !error && monitors.length === 0 && (
        <Card className="p-6 text-center text-gray-500">
          You haven't added any monitors yet. Click "Create Monitor" to get started!
        </Card>
      )}
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {monitors.map((monitor) => (
          // You will need to add a button or link inside MonitorCard
          // that uses this function. For example:
          <MonitorCard
            key={monitor.id}
            monitor={monitor}
            onViewHistory={() => handleViewHistory(monitor.id)}
            onViewConfig={() => handleViewConfig(monitor.id)}
            onUpdateMonitor={() => handleUpdateMonitor(monitor.id)}
            onDeleteMonitor={() => handleDeleteMonitor(monitor.id)}
            onViewChecks={() => handleViewChecks(monitor.id)}
          />
        ))}
      </div>
    </MainLayout>
  );
};
