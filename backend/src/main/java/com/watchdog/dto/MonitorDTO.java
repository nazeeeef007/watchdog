package com.watchdog.dto;

import com.watchdog.entity.Monitor;

import java.time.LocalDateTime;


public class MonitorDTO {
    private Long id;
    private Long userId; // Include user ID for context
    private String url;
    private Integer checkIntervalSeconds;
    private Monitor.MonitorType type;
    private Monitor.MonitorStatus status;
    private LocalDateTime lastCheckedAt;
    private LocalDateTime lastStatusChangeAt;
    private LocalDateTime createdAt;

    public Monitor.MonitorStatus getStatus() {
        return status;
    }

    public void setStatus(Monitor.MonitorStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastStatusChangeAt() {
        return lastStatusChangeAt;
    }

    public void setLastStatusChangeAt(LocalDateTime lastStatusChangeAt) {
        this.lastStatusChangeAt = lastStatusChangeAt;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setType(Monitor.MonitorType type) {
        this.type = type;
    }

    public Monitor.MonitorType getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getCheckIntervalSeconds() {
        return checkIntervalSeconds;
    }

    public void setCheckIntervalSeconds(Integer checkIntervalSeconds) {
        this.checkIntervalSeconds = checkIntervalSeconds;
    }
}