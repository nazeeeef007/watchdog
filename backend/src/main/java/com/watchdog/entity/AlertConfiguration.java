package com.watchdog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_configurations")
public class AlertConfiguration {

    public enum AlertType {
        EMAIL,
        DISCORD_WEBHOOK,
        TELEGRAM,
        GENERIC_WEBHOOK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_id", nullable = false)
    private Monitor monitor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Column(nullable = false)
    private String destination; // Email address, webhook URL, Telegram chat ID etc.

    @Column(nullable = false)
    private Integer failureThreshold; // e.g., 3 consecutive failures before alerting

    private Integer recoveryThreshold; // e.g., 3 consecutive successes before alerting UP

    @Column(nullable = false)
    private Boolean enabled = true; // Is this alert configuration active?

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}