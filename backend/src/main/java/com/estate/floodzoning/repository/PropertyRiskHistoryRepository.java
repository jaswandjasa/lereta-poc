package com.estate.floodzoning.repository;

import com.estate.floodzoning.domain.PropertyRiskHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRiskHistoryRepository extends JpaRepository<PropertyRiskHistory, Long> {

    List<PropertyRiskHistory> findByPropertyIdOrderByChangedAtDesc(Long propertyId);
}
