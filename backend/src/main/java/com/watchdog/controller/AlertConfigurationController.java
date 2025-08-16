package com.watchdog.controller;

import com.watchdog.dto.AlertConfigDTO;
import com.watchdog.dto.CreateAlertConfigRequest;
import com.watchdog.security.CustomUserDetails;
import com.watchdog.service.AlertConfigurationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitors/{monitorId}/alerts")
public class AlertConfigurationController {

    private final AlertConfigurationService alertConfigurationService;

    @Autowired
    public AlertConfigurationController(AlertConfigurationService alertConfigurationService) {
        this.alertConfigurationService = alertConfigurationService;
    }

    // The hardcoded getCurrentUserId() method has been removed.
    // We now get the user's ID directly from the authenticated session.

    @PostMapping
    public ResponseEntity<AlertConfigDTO> createAlertConfiguration(
            @PathVariable Long monitorId,
            @AuthenticationPrincipal CustomUserDetails authenticatedUser,
            @Valid @RequestBody CreateAlertConfigRequest request) {
        // We now get the actual user ID from the authenticated user object.
        Long userId = authenticatedUser.getId();
        AlertConfigDTO newConfig = alertConfigurationService.createAlertConfiguration(monitorId, userId, request);
        return new ResponseEntity<>(newConfig, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AlertConfigDTO>> getAlertConfigurationsForMonitor(
            @PathVariable Long monitorId,
            @AuthenticationPrincipal CustomUserDetails authenticatedUser) {
        Long userId = authenticatedUser.getId();
        List<AlertConfigDTO> configs = alertConfigurationService.getAlertConfigurationsForMonitor(monitorId, userId);
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/{configId}")
    public ResponseEntity<AlertConfigDTO> getAlertConfigurationById(
            @PathVariable Long monitorId,
            @PathVariable Long configId,
            @AuthenticationPrincipal CustomUserDetails authenticatedUser) {
        Long userId = authenticatedUser.getId();
        AlertConfigDTO config = alertConfigurationService.getAlertConfigurationById(configId, monitorId, userId);
        return ResponseEntity.ok(config);
    }

    @PutMapping("/{configId}")
    public ResponseEntity<AlertConfigDTO> updateAlertConfiguration(
            @PathVariable Long monitorId,
            @PathVariable Long configId,
            @AuthenticationPrincipal CustomUserDetails authenticatedUser,
            @Valid @RequestBody CreateAlertConfigRequest request) {
        Long userId = authenticatedUser.getId();
        AlertConfigDTO updatedConfig = alertConfigurationService.updateAlertConfiguration(configId, monitorId, userId, request);
        return ResponseEntity.ok(updatedConfig);
    }

    @DeleteMapping("/{configId}")
    public ResponseEntity<Void> deleteAlertConfiguration(
            @PathVariable Long monitorId,
            @PathVariable Long configId,
            @AuthenticationPrincipal CustomUserDetails authenticatedUser) {
        Long userId = authenticatedUser.getId();
        alertConfigurationService.deleteAlertConfiguration(configId, monitorId, userId);
        return ResponseEntity.noContent().build();
    }
}
