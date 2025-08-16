// src/hooks/useAlertHistory.ts
import { useState, useEffect, useCallback } from "react";
import { monitorService } from '../api/monitorService'; // Ensure this path is correct
import { useAuth } from '../context/AuthContext'; // Ensure this path is correct
import type { AlertHistoryPageDTO } from '../types/AlertHistoryPageDTO'; // Ensure this path is correct

interface UseAlertHistoryResult {
    alertHistory: AlertHistoryPageDTO | null;
    isLoading: boolean;
    error: string | null;
    currentPage: number;
    totalPages: number;
    fetchNextPage: () => void;
    fetchPreviousPage: () => void;
    refetchAlertHistory: () => void;
}

/**
 * Custom hook to fetch and manage alert history.
 * It handles loading states, errors, and provides pagination controls.
 * @param monitorId The ID of the monitor to fetch history for. If undefined, fetches all user history.
 * @param initialPage The initial page number to load (defaults to 0).
 * @param pageSize The number of items per page (defaults to 20).
 */
export const useAlertHistory = (
    monitorId?: number, // The monitorId is now optional
    initialPage: number = 0,
    pageSize: number = 20
): UseAlertHistoryResult => {
    const { token } = useAuth();
    const [alertHistory, setAlertHistory] = useState<AlertHistoryPageDTO | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [currentPage, setCurrentPage] = useState<number>(initialPage);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    const fetchData = useCallback(async () => {
        if (!token) {
            setError('Authentication token missing.');
            setIsLoading(false);
            setAlertHistory(null);
            return;
        }

        setIsLoading(true);
        setError(null); // Clear previous errors
        try {
            let fetchedHistory;
            // Conditional fetch based on if monitorId is provided
            if (monitorId) {
                fetchedHistory = await monitorService.getAlertHistoryByMonitorId(token, monitorId, currentPage, pageSize);
            } else {
                fetchedHistory = await monitorService.getAlertHistory(token, currentPage, pageSize);
            }
            setAlertHistory(fetchedHistory);
            setTotalPages(fetchedHistory.totalPages);
        } catch (err: any) {
            console.error('Failed to fetch alert history in useAlertHistory hook:', err);
            setError(err.message || 'Failed to load alert history.');
            setAlertHistory(null);
        } finally {
            setIsLoading(false);
        }
    }, [token, monitorId, currentPage, pageSize, refreshTrigger]); // Dependencies for fetching

    // Effect to trigger initial fetch and subsequent refetches/pagination changes
    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const fetchNextPage = useCallback(() => {
        setCurrentPage(prev => Math.min(prev + 1, totalPages - 1));
    }, [totalPages]);

    const fetchPreviousPage = useCallback(() => {
        setCurrentPage(prev => Math.max(prev - 1, 0));
    }, []);

    const refetchAlertHistory = useCallback(() => {
        setRefreshTrigger(prev => prev + 1);
    }, []);

    return {
        alertHistory,
        isLoading,
        error,
        currentPage,
        totalPages,
        fetchNextPage,
        fetchPreviousPage,
        refetchAlertHistory,
    };
};