package com.estate.floodzoning.repository;

import com.estate.floodzoning.domain.FloodAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FloodAlertRepository extends JpaRepository<FloodAlert, Long> {

    List<FloodAlert> findAllByOrderByCreatedAtDesc();

    List<FloodAlert> findByAcknowledgedFalseOrderByCreatedAtDesc();

    Optional<FloodAlert> findTopByPropertyIdOrderByCreatedAtDesc(Long propertyId);
}
