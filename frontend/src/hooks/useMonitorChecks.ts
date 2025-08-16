import { monitorService } from "@/api/monitorService";
import type { MonitorCheckDTO } from "@/types/MonitorCheckDTO";
import { useAuth } from "@/context/AuthContext";
import { useState, useCallback, useEffect } from "react";
import type { PaginatedResponse } from "@/types/PaginatedResponse";

// Define the shape of the data returned by the hook
interface UseMonitorChecksResult {
    monitorChecks: MonitorCheckDTO[];
    pageInfo: {
        page: number;
        size: number;
        totalPages: number;
        totalElements: number;
    };
    isLoading: boolean;
    error: string | null;
    refetchMonitorChecks: () => void;
    // Function to set which monitor to fetch checks for
    selectMonitor: (monitorId: number | null) => void;
    selectedMonitorId: number | null;
}

// Refactored hook that can fetch all checks or checks for a specific monitor
export const useMonitorChecks = (initialMonitorId: number | null = null): UseMonitorChecksResult => {
    const { token, logout } = useAuth();
    const [monitorChecks, setMonitorChecks] = useState<MonitorCheckDTO[]>([]);
    const [pageInfo, setPageInfo] = useState({ page: 0, size: 0, totalPages: 0, totalElements: 0 });
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [refreshTrigger, setRefreshTrigger] = useState(0);
    const [selectedMonitorId, setSelectedMonitorId] = useState<number | null>(initialMonitorId);

    // This is the core fetching logic
    const fetchData = useCallback(async () => {
        if (!token) {
            setError('Authentication token missing.');
            setIsLoading(false);
            setMonitorChecks([]);
            return;
        }

        setIsLoading(true);
        setError(null);
        try {
            let fetchedData: PaginatedResponse<MonitorCheckDTO>;
            if (selectedMonitorId !== null) {
                // Fetch checks for a specific monitor
                fetchedData = await monitorService.getMonitorChecksByMonitorId(token, selectedMonitorId);
            } else {
                // Fetch all checks for the user
                fetchedData = await monitorService.getAllMonitorChecksForUser(token);
            }
            
            // Correctly handle the PaginatedResponse
            setMonitorChecks(fetchedData.content);
            setPageInfo({
                page: fetchedData.pageable.pageNumber,
                size: fetchedData.pageable.pageSize,
                totalPages: fetchedData.totalPages,
                totalElements: fetchedData.totalElements,
            });
        } catch (err: any) {
            setError(err.message || 'Failed to load monitor checks.');
            setMonitorChecks([]);
            if (err.message === 'Authentication token missing.') {
                logout();
            }
        } finally {
            setIsLoading(false);
        }
    }, [token, selectedMonitorId, refreshTrigger, logout]);

    // This effect runs whenever the token, selectedMonitorId, or refreshTrigger changes
    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const refetchMonitorChecks = useCallback(() => {
        setRefreshTrigger(prev => prev + 1);
    }, []);

    const selectMonitor = useCallback((monitorId: number | null) => {
        setSelectedMonitorId(monitorId);
    }, []);

    console.log(monitorChecks);
    // The hook now returns a single, consistent result object
    return {
        monitorChecks,
        pageInfo,
        isLoading,
        error,
        refetchMonitorChecks,
        selectMonitor,
        selectedMonitorId,
    };
};
