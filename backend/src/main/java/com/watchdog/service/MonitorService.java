package com.watchdog.service;

import com.watchdog.entity.Monitor;
import com.watchdog.entity.User;
import com.watchdog.repository.MonitorRepository;
import com.watchdog.repository.MonitorCheckRepository;
import com.watchdog.repository.AlertConfigurationRepository; // Import the new repository
import com.watchdog.repository.AlertHistoryRepository;
import com.watchdog.dto.CreateMonitorRequest;
import com.watchdog.dto.MonitorDTO;
import com.watchdog.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MonitorService {

    private final MonitorRepository monitorRepository;
    private final AuthService authService; // To get User entities
    private final MonitorCheckRepository monitorCheckRepository;
    private final AlertConfigurationRepository alertConfigurationRepository; // Add new repository
    private final AlertHistoryRepository alertHistoryRepository;

    @Autowired
    public MonitorService(MonitorRepository monitorRepository,
                          AuthService authService,
                          MonitorCheckRepository monitorCheckRepository,
                          AlertConfigurationRepository alertConfigurationRepository,
                          AlertHistoryRepository alertHistoryRepository) {
        this.monitorRepository = monitorRepository;
        this.authService = authService;
        this.monitorCheckRepository = monitorCheckRepository;
        this.alertConfigurationRepository = alertConfigurationRepository; // Initialize it
        this.alertHistoryRepository = alertHistoryRepository; // Initialize it
    }

    @Transactional
    public MonitorDTO createMonitor(Long userId, CreateMonitorRequest request) {
        User user = authService.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Monitor monitor = new Monitor();
        monitor.setUser(user);
        monitor.setUrl(request.getUrl());
        monitor.setCheckIntervalSeconds(request.getCheckIntervalSeconds());
        monitor.setType(request.getType());
        monitor.setStatus(Monitor.MonitorStatus.UNKNOWN); // Initial status
        monitor.setCreatedAt(LocalDateTime.now());
        monitor.setUpdatedAt(LocalDateTime.now());
        // --- New: Set initial next_check_at ---
        monitor.setNextCheckAt(LocalDateTime.now());

        Monitor savedMonitor = monitorRepository.save(monitor);
        return convertToMonitorDTO(savedMonitor);
    }

    @Transactional(readOnly = true)
    public List<MonitorDTO> getMonitorsForUser(Long userId) {
        User user = authService.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return monitorRepository.findByUser(user).stream()
                .map(this::convertToMonitorDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MonitorDTO getMonitorByIdAndUser(Long monitorId, Long userId) {
        User user = authService.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Monitor monitor = monitorRepository.findByIdAndUser(monitorId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found or not owned by user with ID: " + monitorId));
        return convertToMonitorDTO(monitor);
    }

    @Transactional
    public MonitorDTO updateMonitor(Long monitorId, Long userId, CreateMonitorRequest request) { // Reusing request DTO
        User user = authService.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Monitor monitor = monitorRepository.findByIdAndUser(monitorId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found or not owned by user with ID: " + monitorId));

        monitor.setUrl(request.getUrl());
        monitor.setCheckIntervalSeconds(request.getCheckIntervalSeconds());
        monitor.setType(request.getType());
        monitor.setUpdatedAt(LocalDateTime.now());
        // --- New: Recalculate next_check_at on update ---
        // This is important if the check interval has changed
        monitor.setNextCheckAt(LocalDateTime.now().plusSeconds(monitor.getCheckIntervalSeconds()));

        Monitor updatedMonitor = monitorRepository.save(monitor);
        return convertToMonitorDTO(updatedMonitor);
    }




    @Transactional
    public void deleteMonitor(Long monitorId, Long userId) {
        User user = authService.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Monitor monitor = monitorRepository.findByIdAndUser(monitorId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found or not owned by user with ID: " + monitorId));

        monitorRepository.delete(monitor); // cascade removes checks, alert configs, alert histories
    }

    // This method will be called by the Scheduler/Worker service internally
    @Transactional
    public void updateMonitorStatusAndLastChecked(Long monitorId, Monitor.MonitorStatus newStatus, LocalDateTime lastCheckedAt) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found with ID: " + monitorId));

        // Only update lastStatusChangeAt if the status actually changed
        if (monitor.getStatus() != newStatus) {
            monitor.setLastStatusChangeAt(LocalDateTime.now());
        }
        monitor.setStatus(newStatus);
        monitor.setLastCheckedAt(lastCheckedAt);
        monitorRepository.save(monitor);
    }

    // Helper method to convert Entity to DTO
    private MonitorDTO convertToMonitorDTO(Monitor monitor) {
        MonitorDTO dto = new MonitorDTO();
        dto.setId(monitor.getId());
        dto.setUserId(monitor.getUser().getId());
        dto.setUrl(monitor.getUrl());
        dto.setCheckIntervalSeconds(monitor.getCheckIntervalSeconds());
        dto.setType(monitor.getType());
        dto.setStatus(monitor.getStatus());
        dto.setLastCheckedAt(monitor.getLastCheckedAt());
        dto.setLastStatusChangeAt(monitor.getLastStatusChangeAt());
        dto.setCreatedAt(monitor.getCreatedAt());
        return dto;
    }
}