// src/hooks/useAlertConfig.ts
import { useState, useEffect, useCallback } from "react";
import { monitorService } from "@/api/monitorService";
import { useAuth } from "@/context/AuthContext";
import type { AlertConfigDTO } from "@/types/AlertConfigDTO";
import type { CreateAlertConfigRequest } from "@/types/CreateAlertConfigRequest";

/**
 * Interface for the return value of the useAlertConfig hook.
 * This now includes functions for CRUD operations.
 */
interface UseAlertConfigResult {
  alertConfigs: AlertConfigDTO[] | null;
  isLoading: boolean;
  error: string | null;
  refetchAlertConfigs: () => void;
  createAlertConfig: (newConfig: CreateAlertConfigRequest) => Promise<void>;
  updateAlertConfig: (configId: number, updatedConfig: CreateAlertConfigRequest) => Promise<void>;
  deleteAlertConfig: (configId: number) => Promise<void>;
}

/**
 * Custom hook to fetch, create, update, and delete alert configurations for a specific monitor.
 * It handles loading states, errors, and automatically refetches data after mutations.
 * @param monitorId The ID of the monitor to manage alert configurations for.
 */
export const useAlertConfig = (
  monitorId: number
): UseAlertConfigResult => {
  const { token } = useAuth();
  const [alertConfigs, setAlertConfigs] = useState<AlertConfigDTO[] | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  // A memoized function to fetch the alert configurations
  const fetchAlerts = useCallback(async () => {
    if (!token || !monitorId) {
      setError("Authentication token or monitor ID is missing.");
      setIsLoading(false);
      setAlertConfigs(null);
      return;
    }

    setIsLoading(true);
    setError(null);
    try {
      const fetchedConfigs = await monitorService.getAlertConfigurations(token, monitorId);
      setAlertConfigs(fetchedConfigs);
    } catch (err: any) {
      console.error("Failed to fetch alert configurations:", err);
      setError(err.message || "Failed to load alert configurations.");
      setAlertConfigs(null);
    } finally {
      setIsLoading(false);
    }
  }, [token, monitorId, refreshTrigger]);

  // useEffect to trigger the initial fetch and subsequent refetches
  useEffect(() => {
    fetchAlerts();
  }, [fetchAlerts]);

  // Function to manually trigger a refetch
  const refetchAlertConfigs = useCallback(() => {
    setRefreshTrigger(prev => prev + 1);
  }, []);

  // Function to create a new alert configuration
  const createAlertConfig = useCallback(async (newConfig: CreateAlertConfigRequest) => {
    if (!token || !monitorId) {
      throw new Error("Authentication token or monitor ID is missing.");
    }
    try {
      await monitorService.createAlertConfiguration(token, monitorId, newConfig);
      refetchAlertConfigs(); // Automatically refetch data after a successful creation
    } catch (err: any) {
      console.error("Failed to create alert configuration:", err);
      throw new Error(err.message || "Failed to create alert configuration.");
    }
  }, [token, monitorId, refetchAlertConfigs]);

  // Function to update an existing alert configuration
  const updateAlertConfig = useCallback(async (configId: number, updatedConfig: CreateAlertConfigRequest) => {
    if (!token || !monitorId) {
      throw new Error("Authentication token or monitor ID is missing.");
    }
    try {
      await monitorService.updateAlertConfiguration(token, monitorId, configId, updatedConfig);
      refetchAlertConfigs(); // Automatically refetch data after a successful update
    } catch (err: any) {
      console.error("Failed to update alert configuration:", err);
      throw new Error(err.message || "Failed to update alert configuration.");
    }
  }, [token, monitorId, refetchAlertConfigs]);

  // Function to delete an alert configuration
  const deleteAlertConfig = useCallback(async (configId: number) => {
    if (!token || !monitorId) {
      throw new Error("Authentication token or monitor ID is missing.");
    }
    try {
      await monitorService.deleteAlertConfiguration(token, monitorId, configId);
      refetchAlertConfigs(); // Automatically refetch data after a successful deletion
    } catch (err: any) {
      console.error("Failed to delete alert configuration:", err);
      throw new Error(err.message || "Failed to delete alert configuration.");
    }
  }, [token, monitorId, refetchAlertConfigs]);

  return {
    alertConfigs,
    isLoading,
    error,
    refetchAlertConfigs,
    createAlertConfig,
    updateAlertConfig,
    deleteAlertConfig,
  };
};
