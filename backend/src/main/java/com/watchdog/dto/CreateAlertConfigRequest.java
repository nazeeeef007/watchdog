package com.watchdog.dto;

import com.watchdog.entity.AlertConfiguration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Corrected CreateAlertConfigRequest
public class CreateAlertConfigRequest {
    @NotNull(message = "Alert type cannot be null")
    private AlertConfiguration.AlertType type;

    @NotBlank(message = "Destination cannot be empty")
    private String destination;

    @NotNull(message = "Enabled status cannot be null")
    private Boolean enabled;

    @NotNull(message = "Failure threshold cannot be null")
    private Integer failureThreshold; // Added to match the entity

    @NotNull(message = "Recovery threshold cannot be null")
    private Integer recoveryThreshold; // Added to match the entity

    // Getters and Setters...
    // All existing getters and setters are still here, but I've omitted them
    // for brevity. You just need to add the new ones for the thresholds.


    public void setDestination(@NotBlank(message = "Destination cannot be empty") String destination) {
        this.destination = destination;
    }

    public @NotBlank(message = "Destination cannot be empty") String getDestination() {
        return destination;
    }

    public @NotNull(message = "Enabled status cannot be null") Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(@NotNull(message = "Enabled status cannot be null") Boolean enabled) {
        this.enabled = enabled;
    }

    public @NotNull(message = "Alert type cannot be null") AlertConfiguration.AlertType getType() {
        return type;
    }

    public void setType(@NotNull(message = "Alert type cannot be null") AlertConfiguration.AlertType type) {
        this.type = type;
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
