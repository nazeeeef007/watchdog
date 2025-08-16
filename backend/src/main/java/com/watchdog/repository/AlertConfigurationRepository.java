package com.watchdog.repository;

import com.watchdog.entity.AlertConfiguration;
import com.watchdog.entity.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertConfigurationRepository extends JpaRepository<AlertConfiguration, Long> {

    /**
     * Finds all AlertConfigurations for a specific Monitor.
     * @param monitor The Monitor entity.
     * @return A list of AlertConfigurations.
     */
    List<AlertConfiguration> findByMonitor(Monitor monitor);

    /**
     * Finds an AlertConfiguration by its ID and the associated Monitor.
     * Useful for ensuring proper ownership/context.
     * @param id The ID of the alert configuration.
     * @param monitor The Monitor entity.
     * @return An Optional containing the AlertConfiguration if found, otherwise empty.
     */
    Optional<AlertConfiguration> findByIdAndMonitor(Long id, Monitor monitor);

    /**
     * Finds active alert configurations for a given monitor and alert type.
     * @param monitor The Monitor entity.
     * @param type The type of alert (e.g., EMAIL, WEBHOOK).
     * @param enabled If the configuration is enabled.
     * @return A list of matching AlertConfigurations.
     */
    List<AlertConfiguration> findByMonitorAndTypeAndEnabled(Monitor monitor, AlertConfiguration.AlertType type, Boolean enabled);

    /**
     * Finds all enabled alert configurations for a specific monitor.
     * @param monitor The Monitor entity.
     * @param enabled If the configuration is enabled.
     * @return A list of enabled AlertConfigurations for the monitor.
     */
    List<AlertConfiguration> findByMonitorAndEnabled(Monitor monitor, Boolean enabled);

    @Modifying
    @Transactional
    void deleteAllByMonitorId(Long monitorId);
}