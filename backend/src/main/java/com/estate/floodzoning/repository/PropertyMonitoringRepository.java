package com.estate.floodzoning.repository;

import com.estate.floodzoning.domain.PropertyMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyMonitoringRepository extends JpaRepository<PropertyMonitoring, Long> {

    List<PropertyMonitoring> findByMonitoringEnabledTrue();

    List<PropertyMonitoring> findByStatusChangedTrue();
}
