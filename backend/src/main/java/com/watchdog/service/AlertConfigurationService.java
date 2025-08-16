package com.watchdog.service;

import com.watchdog.entity.AlertConfiguration;
import com.watchdog.entity.Monitor;
import com.watchdog.repository.AlertConfigurationRepository;
import com.watchdog.repository.MonitorRepository;
import com.watchdog.dto.CreateAlertConfigRequest;
import com.watchdog.dto.AlertConfigDTO;
import com.watchdog.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertConfigurationService {

    private final AlertConfigurationRepository alertConfigurationRepository;
    private final MonitorRepository monitorRepository;

    @Autowired
    public AlertConfigurationService(AlertConfigurationRepository alertConfigurationRepository, MonitorRepository monitorRepository) {
        this.alertConfigurationRepository = alertConfigurationRepository;
        this.monitorRepository = monitorRepository;
    }

    @Transactional
    public AlertConfigDTO createAlertConfiguration(Long monitorId, Long userId, CreateAlertConfigRequest request) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found or not owned by user with ID: " + monitorId));

        AlertConfiguration config = new AlertConfiguration();
        config.setMonitor(monitor);
        config.setType(request.getType());
        config.setDestination(request.getDestination());
        config.setEnabled(request.getEnabled());

        // Added the threshold values from the request
        config.setFailureThreshold(request.getFailureThreshold());
        config.setRecoveryThreshold(request.getRecoveryThreshold());

        config.setCreatedAt(LocalDateTime.now());
        // The PrePersist hook will set createdAt, but explicitly setting it here is also fine
        // config.setCreatedAt(LocalDateTime.now());
        // This is not needed as it's handled by @PreUpdate or is null on creation
        // config.setUpdatedAt(LocalDateTime.now());

        AlertConfiguration savedConfig = alertConfigurationRepository.save(config);
        return convertToDTO(savedConfig);
    }

    @Transactional(readOnly = true)
    public List<AlertConfigDTO> getAlertConfigurationsForMonitor(Long monitorId, Long userId) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found or not owned by user with ID: " + monitorId));

        return alertConfigurationRepository.findByMonitor(monitor).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlertConfigDTO getAlertConfigurationById(Long configId, Long monitorId, Long userId) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found or not owned by user with ID: " + monitorId));

        AlertConfiguration config = alertConfigurationRepository.findByIdAndMonitor(configId, monitor)
                .orElseThrow(() -> new ResourceNotFoundException("Alert configuration not found or not associated with monitor/user."));
        return convertToDTO(config);
    }

    @Transactional
    public AlertConfigDTO updateAlertConfiguration(Long configId, Long monitorId, Long userId, CreateAlertConfigRequest request) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found or not owned by user with ID: " + monitorId));

        AlertConfiguration config = alertConfigurationRepository.findByIdAndMonitor(configId, monitor)
                .orElseThrow(() -> new ResourceNotFoundException("Alert configuration not found or not associated with monitor/user."));

        config.setType(request.getType());
        config.setDestination(request.getDestination());
        config.setEnabled(request.getEnabled());

        // Added the threshold values from the request
        config.setFailureThreshold(request.getFailureThreshold());
        config.setRecoveryThreshold(request.getRecoveryThreshold());

        // The @PreUpdate hook will set this, so it's not strictly necessary here
        // config.setUpdatedAt(LocalDateTime.now());

        AlertConfiguration updatedConfig = alertConfigurationRepository.save(config);
        return convertToDTO(updatedConfig);
    }

    @Transactional
    public void deleteAlertConfiguration(Long configId, Long monitorId, Long userId) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found or not owned by user with ID: " + monitorId));

        AlertConfiguration config = alertConfigurationRepository.findByIdAndMonitor(configId, monitor)
                .orElseThrow(() -> new ResourceNotFoundException("Alert configuration not found or not associated with monitor/user."));

        alertConfigurationRepository.delete(config);
    }

    // Helper method to convert Entity to DTO
    private AlertConfigDTO convertToDTO(AlertConfiguration config) {
        AlertConfigDTO dto = new AlertConfigDTO();
        dto.setId(config.getId());
        dto.setMonitorId(config.getMonitor().getId());
        dto.setType(config.getType());
        dto.setDestination(config.getDestination());
        dto.setEnabled(config.getEnabled());
        dto.setFailureThreshold(config.getFailureThreshold());
        dto.setRecoveryThreshold(config.getRecoveryThreshold());
        dto.setCreatedAt(config.getCreatedAt());
        dto.setUpdatedAt(config.getUpdatedAt());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<AlertConfiguration> getEnabledAlertConfigurationsForMonitor(Long monitorId) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found with ID: " + monitorId));
        return alertConfigurationRepository.findByMonitorAndEnabled(monitor, true);
    }
}
