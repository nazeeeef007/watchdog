package com.watchdog.controller;

import com.watchdog.dto.CreateMonitorRequest;
import com.watchdog.dto.MonitorDTO;
import com.watchdog.security.CustomUserDetails;
import com.watchdog.service.MonitorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitors")
public class MonitorController {

    private final MonitorService monitorService;

    @Autowired
    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    /**
     * Handles GET /api/monitors
     * This method fetches all monitors for the currently authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<MonitorDTO>> getMonitorsForAuthenticatedUser(@AuthenticationPrincipal CustomUserDetails authenticatedUser) {
        // The CustomUserDetails object will never be null here if the SecurityConfig is correct.
        // The framework will throw an exception before this if the user is not authenticated.
        Long userId = authenticatedUser.getId();
        List<MonitorDTO> monitors = monitorService.getMonitorsForUser(userId);
        return ResponseEntity.ok(monitors);
    }

    @PostMapping
    public ResponseEntity<MonitorDTO> createMonitor(
            @AuthenticationPrincipal CustomUserDetails authenticatedUser,
            @Valid @RequestBody CreateMonitorRequest request) {
        Long userId = authenticatedUser.getId();
        MonitorDTO newMonitor = monitorService.createMonitor(userId, request);
        return new ResponseEntity<>(newMonitor, HttpStatus.CREATED);
    }

    @GetMapping("/{monitorId}")
    public ResponseEntity<MonitorDTO> getMonitorById(
            @PathVariable Long monitorId,
            @AuthenticationPrincipal CustomUserDetails authenticatedUser) {
        Long userId = authenticatedUser.getId();
        MonitorDTO monitor = monitorService.getMonitorByIdAndUser(monitorId, userId);
        return ResponseEntity.ok(monitor);
    }

    @PutMapping("/{monitorId}")
    public ResponseEntity<MonitorDTO> updateMonitor(
            @PathVariable Long monitorId,
            @AuthenticationPrincipal CustomUserDetails authenticatedUser,
            @Valid @RequestBody CreateMonitorRequest request) {
        Long userId = authenticatedUser.getId();
        MonitorDTO updatedMonitor = monitorService.updateMonitor(monitorId, userId, request);
        return ResponseEntity.ok(updatedMonitor);
    }

    @DeleteMapping("/{monitorId}")
    public ResponseEntity<Void> deleteMonitor(
            @PathVariable Long monitorId,
            @AuthenticationPrincipal CustomUserDetails authenticatedUser) {
        Long userId = authenticatedUser.getId();
        monitorService.deleteMonitor(monitorId, userId);
        return ResponseEntity.noContent().build();
    }
}
