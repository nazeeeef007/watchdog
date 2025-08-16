package com.watchdog.dto;

import com.watchdog.entity.MonitorCheck;
import java.time.LocalDateTime;

public class MonitorCheckDTO {
    private Long id;
    private Long monitorId;
    private LocalDateTime timestamp;
    private Integer httpStatusCode;
    private Long responseTimeMs;
    private Boolean isUp;
    private String errorMessage;

    // --- New Fields ---
    private Long responseBodySize;
    private MonitorCheck.ErrorCategory errorCategory;
    private Long dnsTimeMs;
    private Long connectTimeMs;
    private Long ttfbMs;

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Boolean getUp() {
        return isUp;
    }

    public void setUp(Boolean up) {
        isUp = up;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    // New getters and setters for the added fields
    public Long getResponseBodySize() {
        return responseBodySize;
    }

    public void setResponseBodySize(Long responseBodySize) {
        this.responseBodySize = responseBodySize;
    }

    public MonitorCheck.ErrorCategory getErrorCategory() {
        return errorCategory;
    }

    public void setErrorCategory(MonitorCheck.ErrorCategory errorCategory) {
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
}
