import type { MonitorDTO } from "@/types/MonitorDTO";
import type { CreateMonitorRequest } from "@/types/CreateMonitorRequest";
import type { AlertConfigDTO} from "@/types/AlertConfigDTO";
import type { CreateAlertConfigRequest } from "@/types/CreateAlertConfigRequest";
import type { AlertHistoryDTO } from "../types/AlertHistoryDTO";
import type { MonitorCheckDTO } from "@/types/MonitorCheckDTO";
import type { PaginatedResponse } from "@/types/PaginatedResponse";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const monitorService = {
  getMonitors: async (token: string): Promise<MonitorDTO[]> => {
    const response = await fetch(`${API_BASE_URL}/monitors`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch monitors');
    }

    return response.json();
  },

  getMonitorById: async (token: string, monitorId: number): Promise<MonitorDTO> => {
    const response = await fetch(`${API_BASE_URL}/monitors/${monitorId}`, {
      method:'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });
     if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch monitor by Id');
    }

    return response.json();
  },

  createMonitor: async (token: string, newMonitor: CreateMonitorRequest): Promise<MonitorDTO> => {
    const response = await fetch(`${API_BASE_URL}/monitors`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(newMonitor),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to create monitor');
    }

    return response.json();
  },

  deleteMonitor: async (token: string, monitorId: number): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/monitors/${monitorId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      }
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to delete monitor');
    }
  },

  updateMonitor: async (token: string, monitorId: number, updatedMonitor: CreateMonitorRequest): Promise<MonitorDTO> => {
    const response = await fetch(`${API_BASE_URL}/monitors/${monitorId}`, {
      method: 'PUT',
      headers:{
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(updatedMonitor),
    });
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update monitor');
    }
    return response.json();
  },



  getAlertConfigurations: async (token: string, monitorId: number): Promise<AlertConfigDTO[]> => {
    const response = await fetch(`${API_BASE_URL}/monitors/${monitorId}/alerts`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch alert configurations');
    }

    return response.json();
  },

  createAlertConfiguration: async (token: string, monitorId: number, newConfig: CreateAlertConfigRequest): Promise<AlertConfigDTO> => {
    const response = await fetch(`${API_BASE_URL}/monitors/${monitorId}/alerts`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(newConfig),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to create alert configuration');
    }

    return response.json();
  },

  updateAlertConfiguration: async (token: string, monitorId: number, configId: number, updatedConfig: CreateAlertConfigRequest): Promise<AlertConfigDTO> => {
    const response = await fetch(`${API_BASE_URL}/monitors/${monitorId}/alerts/${configId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(updatedConfig),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to update alert configuration');
    }

    return response.json();
  },

  deleteAlertConfiguration: async (token: string, monitorId: number, configId: number): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/monitors/${monitorId}/alerts/${configId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to delete alert configuration');
    }
  },

  getAlertHistoryByMonitorId: async (
    token: string,
    monitorId: number,
    page: number = 0,
    size: number = 20,
    startTime?: string, // Added optional startTime parameter
    endTime?: string    // Added optional endTime parameter
  ): Promise<PaginatedResponse<AlertHistoryDTO>> => {
    // Construct the URL with optional parameters
    const queryParams = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    if (startTime) {
      queryParams.append('startTime', startTime);
    }
    if (endTime) {
      queryParams.append('endTime', endTime);
    }

    const response = await fetch(`${API_BASE_URL}/monitors/${monitorId}/history/alerts?${queryParams.toString()}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch alert history');
    }

    return response.json();
  },

    getAlertHistory: async (
    token: string,
    page: number = 0,
    size: number = 20,
    startTime?: string, // Added optional startTime parameter
    endTime?: string    // Added optional endTime parameter
  ): Promise<PaginatedResponse<AlertHistoryDTO>> => {
    // Construct the URL with optional parameters
    const queryParams = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    if (startTime) {
      queryParams.append('startTime', startTime);
    }
    if (endTime) {
      queryParams.append('endTime', endTime);
    }

    const response = await fetch(`${API_BASE_URL}/alerts/history/?${queryParams.toString()}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to fetch alert history');
    }

    return response.json();
  },

  getMonitorChecksByMonitorId: async (
    token: string,
    monitorId: number,
    page: number = 0,
    size: number = 20,
    startTime?: string,
    endTime?: string
  ): Promise<PaginatedResponse<MonitorCheckDTO>> => {

    // Construct the URL with query parameters
    let url = `${API_BASE_URL}/monitors/${monitorId}/checks?page=${page}&size=${size}`;
    if (startTime) {
      url += `&startTime=${encodeURIComponent(startTime)}`;
    }
    if (endTime) {
      url += `&endTime=${encodeURIComponent(endTime)}`;
    }

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || `Failed to fetch Monitor Checks for Monitor ${monitorId}`);
    }

    return response.json();
  },

  // --- NEW FUNCTION for the new backend endpoint ---
// Function to fetch all checks for the current user, regardless of monitor
  getAllMonitorChecksForUser: async (
    token: string,
    page: number = 0,
    size: number = 20,
    startTime?: string,
    endTime?: string
  ): Promise<PaginatedResponse<MonitorCheckDTO>> => {
    // The URL for the new backend endpoint
    let url = `${API_BASE_URL}/checks/all?page=${page}&size=${size}`;
    if (startTime) {
      url += `&startTime=${encodeURIComponent(startTime)}`;
    }
    if (endTime) {
      url += `&endTime=${encodeURIComponent(endTime)}`;
    }

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || `Failed to fetch all Monitor Checks for user`);
    }

    return response.json();
  },

};
