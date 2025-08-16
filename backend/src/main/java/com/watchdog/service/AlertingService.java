// src/main/java/com/watchdog/service/AlertingService.java
package com.watchdog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.watchdog.entity.AlertConfiguration;
import com.watchdog.entity.AlertHistory;
import com.watchdog.entity.Monitor;
import com.watchdog.entity.MonitorCheck;
import com.watchdog.exception.ResourceNotFoundException;
import com.watchdog.repository.AlertHistoryRepository;
import com.watchdog.repository.MonitorCheckRepository;
import com.watchdog.repository.MonitorRepository;
import com.watchdog.service.notification.EmailNotificationHandler;
import com.watchdog.service.notification.NotificationHandler;
import com.watchdog.service.notification.WebhookNotificationHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// DTO for internal alert events via Redis Pub/Sub
class MonitorStatusChangeEvent {
    public Long monitorId;
    public Monitor.MonitorStatus oldStatus;
    public Monitor.MonitorStatus newStatus;
    public LocalDateTime timestamp;
}

@Service
public class AlertingService{

    private static final Logger log = LoggerFactory.getLogger(AlertingService.class);

    private final MonitorRepository monitorRepository;
    private final MonitorCheckRepository monitorCheckRepository;
    private final AlertConfigurationService alertConfigurationService;
    private final AlertHistoryService alertHistoryService;
    private final Map<AlertConfiguration.AlertType, NotificationHandler> notificationHandlers;
    private final ObjectMapper objectMapper;
    private final AlertHistoryRepository alertHistoryRepository;
    private static final int ALERT_THROTTLE_MINUTES = 60;

    @Autowired
    public AlertingService(
            MonitorRepository monitorRepository,
            MonitorCheckRepository monitorCheckRepository,
            AlertConfigurationService alertConfigurationService,
            AlertHistoryService alertHistoryService,
            EmailNotificationHandler emailNotificationHandler,
            WebhookNotificationHandler webhookNotificationHandler,
            RedisMessageListenerContainer redisContainer,
            AlertHistoryRepository alertHistoryRepository) {
        this.monitorRepository = monitorRepository;
        this.monitorCheckRepository = monitorCheckRepository;
        this.alertConfigurationService = alertConfigurationService;
        this.alertHistoryService = alertHistoryService;
        this.alertHistoryRepository = alertHistoryRepository;

        this.notificationHandlers = new ConcurrentHashMap<>();
        this.notificationHandlers.put(AlertConfiguration.AlertType.EMAIL, emailNotificationHandler);
        this.notificationHandlers.put(AlertConfiguration.AlertType.GENERIC_WEBHOOK, webhookNotificationHandler);

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }



    /**
     * This method is called by the WorkerService to publish status change events.
     */


    public void handleMonitorStatusChange(Long monitorId, Monitor.MonitorStatus oldStatus, Monitor.MonitorStatus newStatus) {
        // No need to publish to Redis. Just call the processing method directly.
        log.info("Alerting: Processing status change for monitor ID: {}", monitorId);
        processStatusChange(monitorId, oldStatus, newStatus);
    }


    @Transactional
    public void processStatusChange(Long monitorId, Monitor.MonitorStatus oldStatus, Monitor.MonitorStatus newStatus) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found for alerting: " + monitorId));

        List<AlertConfiguration> configs = alertConfigurationService.getEnabledAlertConfigurationsForMonitor(monitor.getId());

        for (AlertConfiguration config : configs) {
            if (newStatus == Monitor.MonitorStatus.DOWN ) {
//              // && oldStatus != Monitor.MonitorStatus.DOWN
                if (isOutageConfirmed(monitor, config)) {
                    log.info("Alerting: Confirmed outage for monitor {} based on config {}", monitorId, config.getId());
                    sendAlert(monitor, config, "Monitor " + monitor.getUrl() + " is DOWN!");
                }
            } else if (newStatus == Monitor.MonitorStatus.UP && oldStatus != Monitor.MonitorStatus.UP) {
                if (isRecoveryConfirmed(monitor, config)) {
                    log.info("Alerting: Confirmed recovery for monitor {} based on config {}", monitorId, config.getId());
                    sendAlert(monitor, config, "Monitor " + monitor.getUrl() + " is UP again!");
                }
            }
        }
    }

    private boolean isOutageConfirmed(Monitor monitor, AlertConfiguration config) {
        List<MonitorCheck> lastChecks = monitorCheckRepository.findTopByMonitorOrderByTimestampDesc(
                monitor, PageRequest.of(0, config.getFailureThreshold()));

        if (lastChecks.size() < config.getFailureThreshold()) {
            return false;
        }

        long downChecks = lastChecks.stream().filter(check -> !check.getIsUp()).count();
        return downChecks >= config.getFailureThreshold();
    }

    private boolean isRecoveryConfirmed(Monitor monitor, AlertConfiguration config) {
        if (config.getRecoveryThreshold() == null || config.getRecoveryThreshold() <= 0) {
            return false;
        }

        List<MonitorCheck> lastChecks = monitorCheckRepository.findTopByMonitorOrderByTimestampDesc(
                monitor, PageRequest.of(0, config.getRecoveryThreshold()));

        if (lastChecks.size() < config.getRecoveryThreshold()) {
            return false;
        }

        long upChecks = lastChecks.stream().filter(MonitorCheck::getIsUp).count();
        return upChecks >= config.getRecoveryThreshold();
    }

    private void sendAlert(Monitor monitor, AlertConfiguration config, String message) {
        if (isThrottled(monitor, config)) {
            log.info("Alerting: Throttling alert for monitor {}, type {}", monitor.getId(), config.getType());
            alertHistoryService.recordAlertHistory(monitor, config, message + " (throttled)", AlertHistory.AlertStatus.THROTTLED);
            return;
        }

        NotificationHandler handler = notificationHandlers.get(config.getType());
        if (handler != null) {
            try {
                handler.sendNotification(config.getDestination(), message);
                alertHistoryService.recordAlertHistory(monitor, config, message, AlertHistory.AlertStatus.SENT);
                log.info("Alerting: Sent {} alert for monitor {}", config.getType(), monitor.getId());
            } catch (Exception e) {
                log.error("Alerting: Failed to send {} alert for monitor {}: {}", config.getType(), monitor.getId(), e.getMessage(), e);
                alertHistoryService.recordAlertHistory(monitor, config, message + " (failed: " + e.getMessage() + ")", AlertHistory.AlertStatus.FAILED);
            }
        } else {
            log.warn("Alerting: No handler found for alert type: {}", config.getType());
        }
    }

    private boolean isThrottled(Monitor monitor, AlertConfiguration config) {
        LocalDateTime throttleWindowStart = LocalDateTime.now().minus(Duration.ofMinutes(ALERT_THROTTLE_MINUTES));
        Page<AlertHistory> latestAlertsPage = alertHistoryRepository.findByMonitorAndAlertConfigurationOrderByTimestampDesc(
                monitor, config, PageRequest.of(0, 1));
        List<AlertHistory> latestAlerts = latestAlertsPage.getContent();

        if (latestAlerts.isEmpty()) {
            return false;
        }

        AlertHistory lastAlert = latestAlerts.get(0);
        return lastAlert.getTimestamp().isAfter(throttleWindowStart) &&
                lastAlert.getStatus() == AlertHistory.AlertStatus.SENT;
    }
}
