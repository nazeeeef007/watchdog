// Corrected AlertHistoryService.java
package com.watchdog.service;

import com.watchdog.dto.AlertHistoryDTO;
import com.watchdog.entity.AlertConfiguration;
import com.watchdog.entity.AlertHistory;
import com.watchdog.entity.Monitor;
import com.watchdog.exception.ResourceNotFoundException;
import com.watchdog.repository.AlertHistoryRepository;
import com.watchdog.repository.MonitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AlertHistoryService {

    private final AlertHistoryRepository alertHistoryRepository;
    private final MonitorRepository monitorRepository;

    @Autowired
    public AlertHistoryService(AlertHistoryRepository alertHistoryRepository, MonitorRepository monitorRepository) {
        this.alertHistoryRepository = alertHistoryRepository;
        this.monitorRepository = monitorRepository;
    }

    /**
     * Records a new alert history entry.
     */
    @Transactional
    public AlertHistoryDTO recordAlertHistory(
            Monitor monitor,
            AlertConfiguration alertConfiguration,
            String message,
            AlertHistory.AlertStatus status) {

        AlertHistory history = new AlertHistory();
        history.setMonitor(monitor);
        history.setAlertConfiguration(alertConfiguration);
        history.setTimestamp(LocalDateTime.now());
        history.setMessage(message);
        history.setStatus(status);

        AlertHistory savedHistory = alertHistoryRepository.save(history);
        return convertToDTO(savedHistory);
    }

    /**
     * Retrieves paginated alert history for a specific monitor within an optional time range.
     */
    @Transactional(readOnly = true)
    public Page<AlertHistoryDTO> getAlertHistoryForMonitor(
            Long monitorId,
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int page,
            int size) {

        Monitor monitor = monitorRepository.findById(monitorId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found or not owned by user with ID: " + monitorId));

        Pageable pageable = PageRequest.of(page, size);

        if (startTime != null && endTime != null) {
            // Fetch history within the specified time range
            return alertHistoryRepository.findByMonitorAndTimestampBetweenOrderByTimestampDesc(monitor, startTime, endTime, pageable)
                    .map(this::convertToDTO);
        } else {
            // If no time range is provided, fetch all history for the monitor
            return alertHistoryRepository.findByMonitorOrderByTimestampDesc(monitor, pageable)
                    .map(this::convertToDTO);
        }
    }

    /**
     * Retrieves paginated alert history for a specific user across all their monitors.
     */
    @Transactional(readOnly = true)
    public Page<AlertHistoryDTO> getAlertHistoryForUser(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Check if a time range is provided.
        if (startTime != null && endTime != null) {
            // If a time range is provided, use the method with the 'Between' clause.
            return alertHistoryRepository.findByMonitor_User_IdAndTimestampBetweenOrderByTimestampDesc(
                            userId, startTime, endTime, pageable)
                    .map(this::convertToDTO);
        } else {
            // If no time range is provided, use the new method without the 'Between' clause
            // to avoid passing extreme date values to the database.
            return alertHistoryRepository.findByMonitor_User_IdOrderByTimestampDesc(
                            userId, pageable)
                    .map(this::convertToDTO);
        }
    }

    // Helper method to convert Entity to DTO
    private AlertHistoryDTO convertToDTO(AlertHistory history) {
        AlertHistoryDTO dto = new AlertHistoryDTO();
        dto.setId(history.getId());
        dto.setMonitorId(history.getMonitor().getId());
        dto.setAlertConfigurationId(history.getAlertConfiguration() != null ? history.getAlertConfiguration().getId() : null);
        dto.setTimestamp(history.getTimestamp());
        dto.setStatus(history.getStatus());
        dto.setMessage(history.getMessage());
        return dto;
    }
}