package com.estate.floodzoning.service;

// FUTURE: Extractable module boundary

import com.estate.floodzoning.domain.PropertyRiskHistory;
import com.estate.floodzoning.dto.RiskHistoryDto;
import com.estate.floodzoning.repository.PropertyRiskHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskHistoryService {

    private final PropertyRiskHistoryRepository historyRepository;

    @Transactional
    public void recordChange(Long propertyId, String oldRisk, String newRisk,
                             String oldZone, String newZone, String triggerSource) {
        // Safeguard: prevent duplicate row if same-second cycle repeats
        List<PropertyRiskHistory> recent = historyRepository
                .findByPropertyIdOrderByChangedAtDesc(propertyId);
        if (!recent.isEmpty()) {
            PropertyRiskHistory last = recent.get(0);
            if (oldRisk.equals(last.getOldRisk()) && newRisk.equals(last.getNewRisk())
                    && oldZone.equals(last.getOldZone()) && newZone.equals(last.getNewZone())
                    && last.getChangedAt() != null
                    && last.getChangedAt().isAfter(LocalDateTime.now().minusSeconds(30))) {
                log.debug("Skipping duplicate history row for property={}", propertyId);
                return;
            }
        }

        PropertyRiskHistory entry = new PropertyRiskHistory();
        entry.setPropertyId(propertyId);
        entry.setOldRisk(oldRisk);
        entry.setNewRisk(newRisk);
        entry.setOldZone(oldZone);
        entry.setNewZone(newZone);
        entry.setChangedAt(LocalDateTime.now());
        entry.setTriggerSource(triggerSource);
        historyRepository.save(entry);

        log.info("Risk history recorded: property={}, {} → {}, zone {} → {}, source={}",
                propertyId, oldRisk, newRisk, oldZone, newZone, triggerSource);
    }

    public List<RiskHistoryDto> getHistory(Long propertyId) {
        log.debug("Fetching risk history for property={}", propertyId);
        return historyRepository.findByPropertyIdOrderByChangedAtDesc(propertyId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private RiskHistoryDto toDto(PropertyRiskHistory h) {
        return new RiskHistoryDto(
                h.getId(), h.getPropertyId(),
                h.getOldRisk(), h.getNewRisk(),
                h.getOldZone(), h.getNewZone(),
                h.getChangedAt(), h.getTriggerSource());
    }
}
