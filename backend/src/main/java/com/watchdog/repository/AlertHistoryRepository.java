package com.watchdog.repository;

import com.watchdog.entity.AlertConfiguration;
import com.watchdog.entity.AlertHistory;
import com.watchdog.entity.Monitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {

    /**
     * Finds all AlertHistory records for a specific Monitor, with pagination, ordered by timestamp descending.
     * This method is used when no time range is specified.
     * @param monitor The Monitor entity.
     * @param pageable Pagination information.
     * @return A Page of AlertHistory records.
     */
    Page<AlertHistory> findByMonitorOrderByTimestampDesc(Monitor monitor, Pageable pageable);

    /**
     * Finds AlertHistory records for a specific Monitor within a time range, with pagination.
     * @param monitor The Monitor entity.
     * @param startTime The start of the time range.
     * @param endTime The end of the time range.
     * @param pageable Pagination information.
     * @return A Page of AlertHistory records.
     */
    Page<AlertHistory> findByMonitorAndTimestampBetweenOrderByTimestampDesc(Monitor monitor, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * Finds the latest alert sent for a given monitor and alert configuration.
     * This can be used for implementing alert throttling.
     * @param monitor The Monitor entity.
     * @param alertConfiguration The AlertConfiguration entity.
     * @param pageable A Pageable object to limit to 1 result. Use PageRequest.of(0, 1).
     * @return A Page containing the latest AlertHistory record (or empty if none).
     */
    Page<AlertHistory> findByMonitorAndAlertConfigurationOrderByTimestampDesc(Monitor monitor, AlertConfiguration alertConfiguration, Pageable pageable);


    /**
     * Retrieves paginated alert history for a specific user across all their monitors,
     * within a specified time range, ordered by timestamp descending.
     * This is the new method you requested.
     */
    Page<AlertHistory> findByMonitor_User_IdAndTimestampBetweenOrderByTimestampDesc(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable);
    /**
     * Retrieves all paginated alert history for a specific user across all their monitors,
     * ordered by timestamp descending. This new method is for when no time range is specified.
     * @param userId The ID of the user.
     * @param pageable Pagination information.
     * @return A Page of AlertHistory records.
     */
    Page<AlertHistory> findByMonitor_User_IdOrderByTimestampDesc(
            Long userId,
            Pageable pageable);
    /**
     * Counts the number of alerts sent for a specific monitor within a time window.
     * Useful for implementing alert storm prevention (e.g., "don't send more than X alerts in Y minutes").
     * @param monitor The Monitor entity.
     * @param timestampAfter The start of the time window.
     * @return The count of alerts.
     */
    long countByMonitorAndTimestampAfter(Monitor monitor, LocalDateTime timestampAfter);

    @Modifying
    @Transactional
    void deleteAllByMonitorId(Long monitorId);
}
