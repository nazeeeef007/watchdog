package com.watchdog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitor_checks")
public class MonitorCheck {

    // A simple enum to categorize errors for better analysis
    public enum ErrorCategory {
        HTTP_CLIENT_ERROR, // 4xx status codes
        HTTP_SERVER_ERROR, // 5xx status codes
        NETWORK_ERROR,     // Connection refused, timeout, etc.
        SSL_ERROR,         // SSL certificate issues
        TIMEOUT_ERROR,     // The request timed out
        CONTENT_MISMATCH,  // The response didn't contain the expected string
        UNKNOWN_ERROR,     // A generic, uncategorized error
        NONE               // No error
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_id", nullable = false)
    private Monitor monitor;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private Integer httpStatusCode;

    private Long responseTimeMs; // Total response time in milliseconds

    @Column(columnDefinition = "TEXT")
    private String errorMessage; // Any error message if the check failed

    @Column(nullable = false)
    private Boolean isUp; // True if the site was considered "up", false otherwise

    // --- New Fields to be added ---

    // The size of the response body, extracted from the Content-Length header
    private Long responseBodySize;

    // A more structured way to store the type of error that occurred
    @Enumerated(EnumType.STRING)
    private ErrorCategory errorCategory = ErrorCategory.NONE;

    // A breakdown of the total response time for more detailed analysis
    private Long dnsTimeMs;      // Time spent on DNS lookup
    private Long connectTimeMs;  // Time spent connecting to the server
    private Long ttfbMs;         // Time to First Byte

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Boolean getIsUp() {
        return isUp;
    }

    public void setIsUp(Boolean isUp) {
        this.isUp = isUp;
    }

    // New getters and setters for the added fields
    public Long getResponseBodySize() {
        return responseBodySize;
    }

    public void setResponseBodySize(Long responseBodySize) {
        this.responseBodySize = responseBodySize;
    }

    public ErrorCategory getErrorCategory() {
        return errorCategory;
    }

    public void setErrorCategory(ErrorCategory errorCategory) {
        this.errorCategory = errorCategory;
    }

    public Long getDnsTimeMs() {
        return dnsTimeMs;
    }

    public void setDnsTimeMs(Long dnsTimeMs) {
        this.dnsTimeMs = dnsTimeMs;
    }

    public Long getConnectTimeMs() {
        return connectTimeMs;
    }

    public void setConnectTimeMs(Long connectTimeMs) {
        this.connectTimeMs = connectTimeMs;
    }

    public Long getTtfbMs() {
        return ttfbMs;
    }

    public void setTtfbMs(Long ttfbMs) {
        this.ttfbMs = ttfbMs;
    }

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
