package com.watchdog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes; // Import SqlTypes

@Entity
@Table(name = "monitors")
public class Monitor {

    public enum MonitorType {
        HTTP_HTTPS,
        PING,
        PORT
    }

    public enum MonitorStatus {
        UP,
        DOWN,
        PAUSED,
        UNKNOWN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who owns this monitor

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Integer checkIntervalSeconds; // How often to check, in seconds

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MonitorType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    // Corrected: Initialize with MonitorStatus.UNKNOWN
    private MonitorStatus status = MonitorStatus.UNKNOWN;

    private LocalDateTime lastCheckedAt;

    private LocalDateTime lastStatusChangeAt;

    private String contentMatchString; // Optional: String to look for in the response

    private String httpMethod; // e.g., GET, POST, HEAD

    // Optional: JSON string or custom object to store HTTP headers
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON) // This annotation tells Hibernate to treat the String as JSONB
    private String httpHeaders;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // A monitor has many checks
    @OneToMany(mappedBy = "monitor", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp DESC") // Order checks by newest first
    private Set<MonitorCheck> checks = new HashSet<>();

    // A monitor can have multiple alert configurations
    @OneToMany(mappedBy = "monitor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AlertConfiguration> alertConfigurations = new HashSet<>();


    // You also need to add a cascade for AlertHistory records.
    @OneToMany(mappedBy = "monitor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AlertHistory> alertHistory = new HashSet<>();

    private LocalDateTime nextCheckAt;

    public LocalDateTime getNextCheckAt() {
        return nextCheckAt;
    }

    public void setNextCheckAt(LocalDateTime nextCheckAt) {
        this.nextCheckAt = nextCheckAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public MonitorType getType() {
        return type;
    }

    public void setType(MonitorType type) {
        this.type = type;
    }

    public MonitorStatus getStatus() {
        return status;
    }

    public void setStatus(MonitorStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public LocalDateTime getLastStatusChangeAt() {
        return lastStatusChangeAt;
    }

    public void setLastStatusChangeAt(LocalDateTime lastStatusChangeAt) {
        this.lastStatusChangeAt = lastStatusChangeAt;
    }

    public String getContentMatchString() {
        return contentMatchString;
    }

    public void setContentMatchString(String contentMatchString) {
        this.contentMatchString = contentMatchString;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(String httpHeaders) {
        this.httpHeaders = httpHeaders;
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

    public Set<MonitorCheck> getChecks() {
        return checks;
    }

    public void setChecks(Set<MonitorCheck> checks) {
        this.checks = checks;
    }

    public Set<AlertConfiguration> getAlertConfigurations() {
        return alertConfigurations;
    }

    public void setAlertConfigurations(Set<AlertConfiguration> alertConfigurations) {
        this.alertConfigurations = alertConfigurations;
    }

    public Set<AlertHistory> getAlertHistory() {
        return alertHistory;
    }

    public void setAlertHistory(Set<AlertHistory> alertHistory) {
        this.alertHistory = alertHistory;
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
