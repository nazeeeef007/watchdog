// src/hooks/useMonitors.ts
import { useState, useEffect, useCallback } from 'react';
import { monitorService } from '../api/monitorService';
import { useAuth } from '../context/AuthContext';
import type { MonitorDTO } from '../types/MonitorDTO';
import type { CreateMonitorRequest } from '../types/CreateMonitorRequest';

interface UseMonitorsResult {
  monitors: MonitorDTO[];
  isLoading: boolean;
  error: string | null;
  refetchMonitors: () => void;
  createMonitor: (newMonitor: CreateMonitorRequest) => Promise<MonitorDTO>;
  updateMonitor: (monitorId: number, updatedMonitor: CreateMonitorRequest) => Promise<MonitorDTO>;
  deleteMonitor: (monitorId: number) => Promise<void>;
  fetchMonitorById: (monitorId: number) => Promise<MonitorDTO | null>;
  isMutating: boolean;
  mutationError: string | null;
}

export const useMonitors = (): UseMonitorsResult => {
  const { token, logout } = useAuth();
  const [monitors, setMonitors] = useState<MonitorDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isMutating, setIsMutating] = useState(false);
  const [mutationError, setMutationError] = useState<string | null>(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const fetchMonitors = useCallback(async () => {
    if (!token) {
      setError('Authentication token missing.');
      setIsLoading(false);
      setMonitors([]);
      return;
    }
    setIsLoading(true);
    setError(null);
    try {
      const fetchedMonitors = await monitorService.getMonitors(token);
      setMonitors(fetchedMonitors);
    } catch (err: any) {
      setError(err.message || 'Failed to load monitors.');
      setMonitors([]);
    } finally {
      setIsLoading(false);
    }
  }, [token, refreshTrigger]);

  useEffect(() => {
    fetchMonitors();
  }, [fetchMonitors]);

  const refetchMonitors = useCallback(() => {
    setRefreshTrigger(prev => prev + 1);
  }, []);

  const requireToken = () => {
    if (!token) {
      setMutationError('Authentication token missing.');
      logout();
      throw new Error('Authentication token missing.');
    }
  };

  const createMonitor = useCallback(async (newMonitor: CreateMonitorRequest) => {
    requireToken();
    setIsMutating(true);
    setMutationError(null);
    try {
      const created = await monitorService.createMonitor(token!, newMonitor);
      setMonitors(prev => [...prev, created]);
      return created;
    } catch (err: any) {
      setMutationError(err.message || 'Failed to create monitor.');
      throw err;
    } finally {
      setIsMutating(false);
    }
  }, [token]);

  const updateMonitor = useCallback(async (monitorId: number, updatedMonitor: CreateMonitorRequest) => {
    requireToken();
    setIsMutating(true);
    setMutationError(null);
    try {
      const updated = await monitorService.updateMonitor(token!, monitorId, updatedMonitor);
      setMonitors(prev => prev.map(m => m.id === updated.id ? updated : m));
      return updated;
    } catch (err: any) {
      setMutationError(err.message || 'Failed to update monitor.');
      throw err;
    } finally {
      setIsMutating(false);
    }
  }, [token]);

  const deleteMonitor = useCallback(async (monitorId: number) => {
    requireToken();
    setIsMutating(true);
    setMutationError(null);
    try {
      await monitorService.deleteMonitor(token!, monitorId);
      setMonitors(prev => prev.filter(m => m.id !== monitorId));
    } catch (err: any) {
      setMutationError(err.message || 'Failed to delete monitor.');
      throw err;
    } finally {
      setIsMutating(false);
    }
  }, [token]);

  const fetchMonitorById = useCallback(async (monitorId: number): Promise<MonitorDTO | null> => {
    requireToken();
    try {
      return await monitorService.getMonitorById(token!, monitorId);
    } catch (err: any) {
      setError(err.message || 'Failed to load monitor.');
      return null;
    }
  }, [token]);

  return {
    monitors,
    isLoading,
    error,
    refetchMonitors,
    createMonitor,
    updateMonitor,
    deleteMonitor,
    fetchMonitorById,
    isMutating,
    mutationError,
  };
};
