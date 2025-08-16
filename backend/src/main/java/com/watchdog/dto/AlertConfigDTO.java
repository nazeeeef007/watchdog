package com.watchdog.dto;

import com.watchdog.entity.AlertConfiguration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

// Corrected AlertConfigDTO
public class AlertConfigDTO {
    private Long id;
    private Long monitorId;
    private AlertConfiguration.AlertType type;
    private String destination;
    private Boolean enabled;
    private Integer failureThreshold; // Added to match the entity
    private Integer recoveryThreshold; // Added to match the entity
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters...
    // All existing getters and setters are still here, but I've omitted them
    // for brevity. You just need to add the new ones for the thresholds.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(AlertConfiguration.AlertType type) {
        this.type = type;
    }

    public AlertConfiguration.AlertType getType() {
        return type;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    public Long getMonitorId() {
        return monitorId;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Integer getFailureThreshold() {
        return failureThreshold;
    }

    public void setFailureThreshold(Integer failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    public Integer getRecoveryThreshold() {
        return recoveryThreshold;
    }

    public void setRecoveryThreshold(Integer recoveryThreshold) {
        this.recoveryThreshold = recoveryThreshold;
    }
}