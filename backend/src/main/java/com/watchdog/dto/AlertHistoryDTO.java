package com.watchdog.dto;

import com.watchdog.entity.AlertHistory;

import java.time.LocalDateTime;


public class AlertHistoryDTO {
    private Long id;
    private Long monitorId;
    private Long alertConfigurationId;
    private LocalDateTime timestamp;
    private AlertHistory.AlertStatus status; // SENT, FAILED, THROTTLED
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(AlertHistory.AlertStatus status) {
        this.status = status;
    }

    public AlertHistory.AlertStatus getStatus() {
        return status;
    }

    public void setAlertConfigurationId(Long alertConfigurationId) {
        this.alertConfigurationId = alertConfigurationId;
    }

    public Long getAlertConfigurationId() {
        return alertConfigurationId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}