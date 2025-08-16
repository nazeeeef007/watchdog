package com.watchdog.service.notification;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebhookNotificationHandler implements NotificationHandler {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendNotification(String destination, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create a simple JSON payload
            Map<String, String> payload = new HashMap<>();
            payload.put("alertMessage", message);
            payload.put("service", "Sentry Uptime Monitoring");
            payload.put("timestamp", String.valueOf(System.currentTimeMillis()));

            HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

            // Post to the webhook URL
            restTemplate.postForEntity(destination, request, String.class);
            System.out.println("Webhook sent to: " + destination + " with message: " + message);

        } catch (Exception e) {
            System.err.println("Failed to send webhook to " + destination + ": " + e.getMessage());
            throw new RuntimeException("Webhook sending failed", e);
        }
    }
}