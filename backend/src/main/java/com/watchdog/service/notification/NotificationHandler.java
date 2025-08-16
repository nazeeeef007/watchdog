package com.watchdog.service.notification;

public interface NotificationHandler {
    void sendNotification(String destination, String message);
    // You might add an AlertConfiguration.AlertType getType() method here for mapping
}