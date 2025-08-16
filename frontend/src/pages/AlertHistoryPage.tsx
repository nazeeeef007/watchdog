// src/pages/AlertHistoryPage.tsx
import { useParams } from 'react-router-dom';
import { useAuth } from "@/context/AuthContext";
import { useAlertHistory } from "@/hooks/useAlertHistory";
import { MainLayout } from "@/components/MainLayout";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { AlertHistoryCard } from "@/components/AlertHistoryCard";

export const AlertHistoryPage = () => {
    const { logout } = useAuth();
    // Get the monitorId from the URL parameter
    const { monitorId } = useParams<{ monitorId: string }>();

    // Parse the monitorId from a string to a number.
    // Use a default value of 0 if the ID is not a valid number.
    const parsedMonitorId = monitorId ? parseInt(monitorId, 10) : undefined;
  
    // Use the custom hook to fetch the alert history for the parsedMonitorId.
    // The hook will handle fetching and pagination logic internally.
    const {
        alertHistory,
        isLoading,
        error,
        currentPage,
        totalPages,
        fetchNextPage,
        fetchPreviousPage,
        refetchAlertHistory
    } = useAlertHistory(parsedMonitorId);

   

    return (
        <MainLayout onLogout={logout}>
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-3xl font-bold text-gray-900">Alert History for Monitors {parsedMonitorId}</h2>
                <Button onClick={refetchAlertHistory} disabled={isLoading}>
                    Refresh History
                </Button>
            </div>

            {/* Display a loading message while data is being fetched. */}
            {isLoading && (
                <div className="text-center text-gray-600">Loading alert history...</div>
            )}

            {/* Display a destructive alert if there's an error. */}
            {error && (
                <Alert variant="destructive" className="mb-4">
                    <AlertDescription>{error}</AlertDescription>
                </Alert>
            )}

            {/* Display a message if there is no history to show. */}
            {!isLoading && !error && (!alertHistory || alertHistory.content.length === 0) && (
                <Card className="p-6 text-center text-gray-500">
                    No alert history found for this monitor.
                </Card>
            )}

            {/* Render the list of alerts and pagination controls if data is available. */}
            {!isLoading && !error && alertHistory && alertHistory.content.length > 0 && (
                <>
                    <div className="grid grid-cols-1 gap-4">
                        {alertHistory.content.map((alert) => (
                            // Use the new AlertHistoryCard component here
                            <AlertHistoryCard key={alert.id} alert={alert} />
                        ))}
                    </div>

                    <div className="flex justify-between items-center mt-6">
                        <Button onClick={fetchPreviousPage} disabled={currentPage <= 0 || isLoading}>
                            Previous
                        </Button>
                        <span className="text-gray-700">Page {currentPage + 1} of {totalPages}</span>
                        <Button onClick={fetchNextPage} disabled={currentPage >= totalPages - 1 || isLoading}>
                            Next
                        </Button>
                    </div>
                </>
            )}
        </MainLayout>
    );
};
