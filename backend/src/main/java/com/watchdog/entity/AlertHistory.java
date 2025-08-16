package com.watchdog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_history")
public class AlertHistory {

    // Define AlertStatus enum within the entity, as it's specific to AlertHistory's outcome
    public enum AlertStatus {
        SENT,
        FAILED,
        THROTTLED // If an alert was suppressed due to throttling
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_id", nullable = false)
    private Monitor monitor; // Assuming Monitor entity exists

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_config_id") // Can be null if it's a system-generated alert not tied to a specific config
    private AlertConfiguration alertConfiguration; // Assuming AlertConfiguration entity exists

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String message; // The actual message sent

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status; // SENT, FAILED, THROTTLED

    // The 'type' field is now removed from AlertHistory, as it's defined by AlertConfiguration.
    // If you need the type in AlertHistory, you would get it via alertConfiguration.getType().

    private String failureReason; // If status is FAILED

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public AlertConfiguration getAlertConfiguration() {
        return alertConfiguration;
    }

    public void setAlertConfiguration(AlertConfiguration alertConfiguration) {
        this.alertConfiguration = alertConfiguration;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
