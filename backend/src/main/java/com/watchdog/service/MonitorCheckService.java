package com.watchdog.service;

import com.watchdog.entity.Monitor;
import com.watchdog.entity.MonitorCheck;
import com.watchdog.repository.MonitorCheckRepository;
import com.watchdog.repository.MonitorRepository;
import com.watchdog.dto.MonitorCheckDTO;
import com.watchdog.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MonitorCheckService {

    private final MonitorCheckRepository monitorCheckRepository;
    private final MonitorRepository monitorRepository;

    @Autowired
    public MonitorCheckService(MonitorCheckRepository monitorCheckRepository, MonitorRepository monitorRepository) {
        this.monitorCheckRepository = monitorCheckRepository;
        this.monitorRepository = monitorRepository;
    }

    /**
     * Records a new monitor check. This will be primarily called by the WorkerService.
     *
     * @param monitorId      The ID of the monitor being checked.
     * @param httpStatusCode The HTTP status code received.
     * @param responseTimeMs The total response time in milliseconds.
     * @param isUp           Whether the check determined the monitor is "up".
     * @param errorMessage   Any error message if the check failed.
     * @param responseBodySize The size of the response body in bytes.
     * @param errorCategory  The category of error that occurred.
     * @param dnsTimeMs      Time spent on DNS lookup.
     * @param connectTimeMs  Time spent connecting to the server.
     * @param ttfbMs         Time to first byte.
     * @return The created MonitorCheckDTO.
     */
    @Transactional
    public MonitorCheckDTO recordMonitorCheck(
            Long monitorId,
            Integer httpStatusCode,
            Long responseTimeMs,
            Boolean isUp,
            String errorMessage,
            Long responseBodySize,
            MonitorCheck.ErrorCategory errorCategory,
            Long dnsTimeMs,
            Long connectTimeMs,
            Long ttfbMs) {

        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found with ID: " + monitorId));

        MonitorCheck monitorCheck = new MonitorCheck();
        monitorCheck.setMonitor(monitor);
        monitorCheck.setHttpStatusCode(httpStatusCode);
        monitorCheck.setResponseTimeMs(responseTimeMs);
        monitorCheck.setIsUp(isUp);
        monitorCheck.setErrorMessage(errorMessage);

        // Set new fields
        monitorCheck.setResponseBodySize(responseBodySize);
        monitorCheck.setErrorCategory(errorCategory);
        monitorCheck.setDnsTimeMs(dnsTimeMs);
        monitorCheck.setConnectTimeMs(connectTimeMs);
        monitorCheck.setTtfbMs(ttfbMs);

        MonitorCheck savedCheck = monitorCheckRepository.save(monitorCheck);
        return convertToDTO(savedCheck);
    }

    /**
     * Retrieves the latest N monitor checks for a given monitor, ordered by timestamp descending.
     * This might be used for quick status checks or recent history on a dashboard.
     *
     * @param monitorId The ID of the monitor.
     * @param userId    The ID of the user (for ownership check).
     * @param limit     The maximum number of checks to retrieve.
     * @return A list of MonitorCheckDTOs.
     */
    @Transactional(readOnly = true)
    public List<MonitorCheckDTO> getLatestMonitorChecks(Long monitorId, Long userId, int limit) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found with ID: " + monitorId));
        // Additional check for ownership if monitorId isn't tied to user in repo
        // This is simplified here assuming MonitorService already validated user ownership on monitor
        // or a similar check is done at the controller level.

        // Corrected: Pass PageRequest.of(0, limit) instead of just limit
        return monitorCheckRepository.findTopByMonitorOrderByTimestampDesc(monitor, PageRequest.of(0, limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves paginated monitor checks for a given monitor within a time range.
     *
     * @param monitorId The ID of the monitor.
     * @param userId    The ID of the user (for ownership check).
     * @param startTime The start of the time range.
     * @param endTime   The end of the time range.
     * @param page      The page number (0-indexed).
     * @param size      The number of items per page.
     * @return A Page of MonitorCheckDTOs.
     */
    @Transactional(readOnly = true)
    public Page<MonitorCheckDTO> getMonitorChecksInTimeRange(
            Long monitorId,
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int page,
            int size) {

        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found with ID: " + monitorId));
        // Again, assume monitor ownership is handled upstream or at controller.

        Pageable pageable = PageRequest.of(page, size);
        return monitorCheckRepository.findByMonitorAndTimestampBetweenOrderByTimestampDesc(monitor, startTime, endTime, pageable)
                .map(this::convertToDTO);
    }

    // Helper method to convert Entity to DTO
    private MonitorCheckDTO convertToDTO(MonitorCheck check) {
        MonitorCheckDTO dto = new MonitorCheckDTO();
        dto.setId(check.getId());
        dto.setMonitorId(check.getMonitor().getId());
        dto.setTimestamp(check.getTimestamp());
        dto.setHttpStatusCode(check.getHttpStatusCode());
        dto.setResponseTimeMs(check.getResponseTimeMs());
        dto.setErrorMessage(check.getErrorMessage());
        dto.setUp(check.getIsUp()); // Updated to use the correct getter

        // Set new fields
        dto.setResponseBodySize(check.getResponseBodySize());
        dto.setErrorCategory(check.getErrorCategory());
        dto.setDnsTimeMs(check.getDnsTimeMs());
        dto.setConnectTimeMs(check.getConnectTimeMs());
        dto.setTtfbMs(check.getTtfbMs());

        return dto;
    }

    // Helper to get Monitor entity for internal use if needed (e.g. by WorkerService)
    @Transactional(readOnly = true)
    public Optional<Monitor> findMonitorEntityById(Long monitorId) {
        return monitorRepository.findById(monitorId);
    }

    // --- NEW METHOD to get all checks for a user, regardless of monitor ---
    @Transactional(readOnly = true)
    public Page<MonitorCheckDTO> getAllChecksForUser(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        return monitorCheckRepository.findByMonitor_User_IdAndTimestampBetweenOrderByTimestampDesc(
                        userId,
                        startTime,
                        endTime,
                        pageable)
                .map(this::convertToDTO);
    }
}
