package com.watchdog.repository;

import com.watchdog.entity.Monitor;
import com.watchdog.entity.MonitorCheck;
import org.springframework.data.domain.Page; // Added: Import Page for paginated results
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MonitorCheckRepository extends JpaRepository<MonitorCheck, Long> {

    /**
     * Finds all MonitorChecks for a specific Monitor, ordered by timestamp descending.
     * @param monitor The Monitor entity.
     * @return A list of MonitorChecks.
     */
    List<MonitorCheck> findByMonitorOrderByTimestampDesc(Monitor monitor);

    /**
     * Finds the latest MonitorCheck for a specific Monitor.
     * @param monitor The Monitor entity.
     * @param pageable A Pageable object to limit to 1 result. Use PageRequest.of(0, 1).
     * @return A list containing the latest MonitorCheck (or empty if none).
     */
    List<MonitorCheck> findByMonitorOrderByTimestampDesc(Monitor monitor, Pageable pageable);

    /**
     * Finds the last N monitor checks for a given monitor, useful for calculating uptime/downtime thresholds.
     *
     * @param monitor The Monitor entity.
     * @param pageable A Pageable object to specify the number of results (e.g., PageRequest.of(0, N)).
     * @return A list of the last N MonitorChecks.
     */
    List<MonitorCheck> findTopByMonitorOrderByTimestampDesc(Monitor monitor, Pageable pageable);


    /**
     * Counts the number of MonitorChecks for a monitor that are considered "down" within a given time range.
     * @param monitor The Monitor entity.
     * @param startTime The start of the time range.
     * @param endTime The end of the time range.
     * @return The count of "down" checks.
     */
    long countByMonitorAndIsUpFalseAndTimestampBetween(Monitor monitor, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Counts the total number of MonitorChecks for a monitor within a given time range.
     * @param monitor The Monitor entity.
     * @param startTime The start of the time range.
     * @param endTime The end of the time range.
     * @return The total count of checks.
     */
    long countByMonitorAndTimestampBetween(Monitor monitor, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Retrieves paginated monitor checks for a given monitor within a specified time range,
     * ordered by timestamp descending.
     *
     * @param monitor The Monitor entity.
     * @param startTime The start of the time range.
     * @param endTime The end of the time range.
     * @param pageable A Pageable object for pagination and sorting.
     * @return A Page of MonitorCheck entities.
     */
    Page<MonitorCheck> findByMonitorAndTimestampBetweenOrderByTimestampDesc(
            Monitor monitor,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable);


    // --- NEW METHOD to find all checks for a user, regardless of monitor ---
    Page<MonitorCheck> findByMonitor_User_IdAndTimestampBetweenOrderByTimestampDesc(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable);

    /**
     * Deletes all MonitorCheck entities associated with a given monitor ID.
     * This is used to maintain data integrity when deleting a Monitor.
     * @param monitorId The ID of the Monitor whose checks should be deleted.
     */
    @Modifying
    @Transactional
    void deleteAllByMonitorId(Long monitorId);
}
