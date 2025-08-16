package com.watchdog.controller;

import com.watchdog.dto.MonitorCheckDTO;
import com.watchdog.service.MonitorCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.watchdog.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api") // Changed to a general API path
public class MonitorCheckController {

    private final MonitorCheckService monitorCheckService;

    @Autowired
    public MonitorCheckController(MonitorCheckService monitorCheckService) {
        this.monitorCheckService = monitorCheckService;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // Handle unauthenticated users
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    // Existing endpoints for a specific monitor
    @GetMapping("/monitors/{monitorId}/checks/latest")
    public ResponseEntity<List<MonitorCheckDTO>> getLatestChecksForMonitor(
            @PathVariable Long monitorId,
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = getCurrentUserId();
        List<MonitorCheckDTO> checks = monitorCheckService.getLatestMonitorChecks(monitorId, userId, limit);
        return ResponseEntity.ok(checks);
    }

    @GetMapping("/monitors/{monitorId}/checks")
    public ResponseEntity<Page<MonitorCheckDTO>> getChecksForMonitor(
            @PathVariable Long monitorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        if (startTime == null) startTime = LocalDateTime.now().minusDays(1);
        if (endTime == null) endTime = LocalDateTime.now();
        Page<MonitorCheckDTO> checks = monitorCheckService.getMonitorChecksInTimeRange(monitorId, userId, startTime, endTime, page, size);
        return ResponseEntity.ok(checks);
    }

    // NEW ENDPOINT to get all checks for a user, regardless of monitor
    @GetMapping("/checks/all")
    public ResponseEntity<Page<MonitorCheckDTO>> getAllChecksForUser(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (startTime == null) startTime = LocalDateTime.now().minusDays(1);
        if (endTime == null) endTime = LocalDateTime.now();

        Page<MonitorCheckDTO> checks = monitorCheckService.getAllChecksForUser(userId, startTime, endTime, page, size);
        return ResponseEntity.ok(checks);
    }
}
