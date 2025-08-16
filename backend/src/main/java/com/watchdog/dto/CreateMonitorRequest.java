package com.watchdog.dto;

import com.watchdog.entity.Monitor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateMonitorRequest {
    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(http|https)://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/\\S*)?$", message = "Invalid URL format")
    private String url;

    @NotNull(message = "Check interval cannot be null")
    @Min(value = 30, message = "Check interval must be at least 30 seconds") // Minimum interval
    private Integer checkIntervalSeconds;

    @NotNull(message = "Monitor type cannot be null")
    private Monitor.MonitorType type;

    public @NotBlank(message = "URL cannot be empty") @Pattern(regexp = "^(http|https)://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/\\S*)?$", message = "Invalid URL format") String getUrl() {
        return url;
    }

    public void setUrl(@NotBlank(message = "URL cannot be empty") @Pattern(regexp = "^(http|https)://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/\\S*)?$", message = "Invalid URL format") String url) {
        this.url = url;
    }

    public void setCheckIntervalSeconds(@NotNull(message = "Check interval cannot be null") @Min(value = 30, message = "Check interval must be at least 30 seconds") Integer checkIntervalSeconds) {
        this.checkIntervalSeconds = checkIntervalSeconds;
    }

    public @NotNull(message = "Check interval cannot be null") @Min(value = 30, message = "Check interval must be at least 30 seconds") Integer getCheckIntervalSeconds() {
        return checkIntervalSeconds;
    }

    public @NotNull(message = "Monitor type cannot be null") Monitor.MonitorType getType() {
        return type;
    }

    public void setType(@NotNull(message = "Monitor type cannot be null") Monitor.MonitorType type) {
        this.type = type;
    }
}