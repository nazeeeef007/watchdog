package com.watchdog.repository;

import com.watchdog.entity.Monitor;
import com.watchdog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    /**
     * Finds all Monitors belonging to a specific User.
     * @param user The User entity.
     * @return A list of Monitors.
     */
    List<Monitor> findByUser(User user);

    /**
     * Finds a Monitor by its ID and the owning User.
     * Useful for ensuring a user can only access their own monitors.
     * @param id The ID of the monitor.
     * @param user The User entity.
     * @return An Optional containing the Monitor if found and owned by the user, otherwise empty.
     */
    Optional<Monitor> findByIdAndUser(Long id, User user);

    /**
     * Finds the next Monitor due for a check and acquires an exclusive lock on it
     * to prevent other workers from processing the same monitor concurrently.
     *
     * This query is critical for the in-house queueing system.
     * It looks for a monitor where 'next_check_at' is in the past, orders by that
     * timestamp to find the oldest overdue monitor, and uses 'FOR UPDATE SKIP LOCKED'
     * to ensure thread-safe processing across multiple worker instances.
     *
     * @param now The current timestamp.
     * @return The Monitor to be checked, or null if no monitor is due.
     */
    @Query(value = "SELECT * FROM monitors m WHERE m.next_check_at < :now AND m.status <> 'PAUSED' ORDER BY m.next_check_at ASC LIMIT 1 FOR UPDATE SKIP LOCKED",
            nativeQuery = true)
    Monitor findAndLockNextMonitorDueForCheck(@Param("now") LocalDateTime now);
}