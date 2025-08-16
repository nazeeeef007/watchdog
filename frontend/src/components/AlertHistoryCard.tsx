// src/components/AlertHistoryCard.tsx
import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import type { AlertHistoryDTO } from "@/types/AlertHistoryDTO";

interface AlertHistoryCardProps {
    alert: AlertHistoryDTO;
}

/**
 * A reusable card component to display a single alert history entry.
 * It's designed to be used within the AlertHistoryPage.
 *
 * @param {AlertHistoryCardProps} { alert } - The data object for the alert to display.
 */
export const AlertHistoryCard: React.FC<AlertHistoryCardProps> = ({ alert }) => {
    return (
        <Card className="p-4">
            <CardHeader>
                <CardTitle className="text-lg">Alert ID: {alert.id}</CardTitle>
            </CardHeader>
            <CardContent>
                {/*
                  The 'alertType' and 'destination' properties do not exist on AlertHistoryDTO.
                  We'll use the 'message' property from the DTO instead, which contains
                  the alert's detailed message.
                */}
                <p className="font-semibold">Status: {alert.status}</p>
                <p>Message: {alert.message}</p>
                <p>Timestamp: {new Date(alert.timestamp).toLocaleString()}</p>
            </CardContent>
        </Card>
    );
};
