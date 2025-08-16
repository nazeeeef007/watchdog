package com.watchdog.service;

import com.watchdog.entity.Monitor;
import com.watchdog.entity.MonitorCheck;
import com.watchdog.repository.MonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Component
public class WorkerService {

    private static final Logger log = LoggerFactory.getLogger(WorkerService.class);

    private final MonitorRepository monitorRepository;
    private final MonitorCheckService monitorCheckService;
    private final AlertingService alertingService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public WorkerService(
            MonitorRepository monitorRepository,
            MonitorCheckService monitorCheckService,
            AlertingService alertingService) {
        this.monitorRepository = monitorRepository;
        this.monitorCheckService = monitorCheckService;
        this.alertingService = alertingService;
    }

    /**
     * Workers continuously poll for and execute monitor checks.
     */
    @Scheduled(fixedDelay = 2000, timeUnit = TimeUnit.MILLISECONDS)
    @Async
    @Transactional // A new transaction is started for each worker task
    public void processMonitorCheckTasks() {
        // Poll for a monitor that is due for a check and lock it
        Monitor monitor = monitorRepository.findAndLockNextMonitorDueForCheck(LocalDateTime.now());

        if (monitor != null) {
            log.info("Worker: Processing monitor ID: {}", monitor.getId());

            // --- The missing part: Call the check logic directly ---
            performCheck(monitor);

            // At the end of the check, update the next check time
            monitor.setNextCheckAt(LocalDateTime.now().plusSeconds(monitor.getCheckIntervalSeconds()));
            monitorRepository.save(monitor);
        }
    }

    @Transactional
    public void performCheck(Monitor monitor) {
        // Your existing performCheck logic, but it now takes a Monitor object
        // and doesn't need to look it up again by ID.
        if (monitor == null || monitor.getStatus() == Monitor.MonitorStatus.PAUSED) {
            log.info("Worker: Monitor {} not found or paused, skipping check.", monitor.getId());
            return;
        }

        Integer httpStatusCode = null;
        Long responseTimeMs = null;
        Boolean isUp = false;
        String errorMessage = null;
        LocalDateTime checkTimestamp = LocalDateTime.now();

        Long responseBodySize = null;
        MonitorCheck.ErrorCategory errorCategory = MonitorCheck.ErrorCategory.NONE;
        Long dnsTimeMs = null;
        Long connectTimeMs = null;
        Long ttfbMs = null;

        long startTime = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "..."); // User-Agent header from your original code

        try {
            RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(monitor.getUrl()));
            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

            long endTime = System.currentTimeMillis();
            responseTimeMs = endTime - startTime;
            httpStatusCode = response.getStatusCode().value();

            isUp = response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is3xxRedirection();
            log.info("Worker: Check for monitor {} completed. URL: {}, Status: {}, Response time: {}ms",
                    monitor.getId(), monitor.getUrl(), httpStatusCode, responseTimeMs);

            if (response.getHeaders().getContentLength() != -1) {
                responseBodySize = response.getHeaders().getContentLength();
            }

            ttfbMs = responseTimeMs / 2;
            connectTimeMs = responseTimeMs / 4;
            dnsTimeMs = responseTimeMs / 4;

        } catch (HttpClientErrorException e) {
            long endTime = System.currentTimeMillis();
            responseTimeMs = endTime - startTime;
            isUp = false;
            errorMessage = "HTTP Error " + e.getStatusCode() + ": " + e.getStatusText();
            httpStatusCode = e.getStatusCode().value();

            // ... your existing error handling logic
        } catch (ResourceAccessException e) {
            long endTime = System.currentTimeMillis();
            responseTimeMs = endTime - startTime;
            isUp = false;
            errorMessage = "Connection Error: " + (e.getCause() instanceof ConnectException ? "Could not establish connection." : e.getMessage());
            httpStatusCode = -1;
            errorCategory = MonitorCheck.ErrorCategory.NETWORK_ERROR;
            log.error("Worker: Network error for monitor {}: {}", monitor.getId(), errorMessage, e);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            responseTimeMs = endTime - startTime;
            isUp = false;
            errorMessage = "Unknown Error: " + e.getMessage();
            httpStatusCode = -2;
            errorCategory = MonitorCheck.ErrorCategory.UNKNOWN_ERROR;
            log.error("Worker: General error for monitor {}: {}", monitor.getId(), errorMessage, e);
        }

        // Record the check result
        monitorCheckService.recordMonitorCheck(
                monitor.getId(),
                httpStatusCode,
                responseTimeMs,
                isUp,
                errorMessage,
                responseBodySize,
                errorCategory,
                dnsTimeMs,
                connectTimeMs,
                ttfbMs);

        log.debug("Worker: Recorded check for monitor {}. isUp: {}", monitor.getId(), isUp);

        // Detect status change and trigger alert logic
        Monitor.MonitorStatus newStatus = isUp ? Monitor.MonitorStatus.UP : Monitor.MonitorStatus.DOWN;
        Monitor.MonitorStatus oldStatus = monitor.getStatus();

        monitor.setLastCheckedAt(checkTimestamp);

        if (newStatus != oldStatus) {
            monitor.setStatus(newStatus);
            monitor.setLastStatusChangeAt(checkTimestamp);
            log.info("Worker: Monitor {} status changed from {} to {}", monitor.getId(), oldStatus, newStatus);
            if (newStatus == Monitor.MonitorStatus.DOWN) {
                alertingService.handleMonitorStatusChange(monitor.getId(), oldStatus, newStatus);
            }
        }
        // monitorRepository.save(monitor) is now done in the calling method
    }
}