// Corrected AlertHistoryController.java
package com.watchdog.controller;

import com.watchdog.dto.AlertHistoryDTO;
import com.watchdog.service.AlertHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.watchdog.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api") // <-- Corrected class-level mapping
public class AlertHistoryController {

    private final AlertHistoryService alertHistoryService;

    @Autowired
    public AlertHistoryController(AlertHistoryService alertHistoryService) {
        this.alertHistoryService = alertHistoryService;
    }

    @GetMapping("/monitors/{monitorId}/history/alerts") // <-- Specific path for a single monitor
    public ResponseEntity<Page<AlertHistoryDTO>> getAlertHistoryForMonitor(
            @PathVariable Long monitorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        Page<AlertHistoryDTO> history = alertHistoryService.getAlertHistoryForMonitor(
                monitorId, userId, startTime, endTime, page, size);
        return ResponseEntity.ok(history);
    }

    /**
     * New endpoint to get all alert history for the authenticated user across all monitors.
     */
    @GetMapping("/alerts/history") // <-- Specific path for all alerts
    public ResponseEntity<Page<AlertHistoryDTO>> getAllAlertHistoryForUser(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        if (startTime == null) startTime = LocalDateTime.now().minusDays(1);
        if (endTime == null) endTime = LocalDateTime.now();
        Page<AlertHistoryDTO> history = alertHistoryService.getAlertHistoryForUser(
                userId, startTime, endTime, page, size);
        return ResponseEntity.ok(history);
    }
}