package com.estate.floodzoning.service;

// FUTURE: Extractable module boundary

import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.NearestZoneResponse;
import com.estate.floodzoning.dto.PropertyComparisonDto;
import com.estate.floodzoning.dto.PropertyDto;
import com.estate.floodzoning.repository.CertificateAuditRepository;
import com.estate.floodzoning.repository.FloodAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComparisonService {

    private final PropertyService propertyService;
    private final FloodService floodService;
    private final CertificateAuditRepository certificateAuditRepository;
    private final FloodAlertRepository floodAlertRepository;

    public PropertyComparisonDto compare(Long id1, Long id2) {
        log.info("Comparing properties: id1={}, id2={}", id1, id2);

        if (id1.equals(id2)) {
            throw new IllegalArgumentException("Cannot compare a property with itself");
        }

        PropertyComparisonDto.PropertySnapshot snap1 = buildSnapshot(id1);
        PropertyComparisonDto.PropertySnapshot snap2 = buildSnapshot(id2);

        return new PropertyComparisonDto(snap1, snap2);
    }

    private PropertyComparisonDto.PropertySnapshot buildSnapshot(Long propertyId) {
        PropertyDto property = propertyService.getPropertyById(propertyId);
        FloodResponse floodResult = floodService.checkFloodRisk(property.latitude(), property.longitude());
        NearestZoneResponse nearest = floodService.findNearestFloodZone(property.latitude(), property.longitude());

        long certCount = certificateAuditRepository.countByPropertyId(propertyId);
        long alertCount = floodAlertRepository.countByPropertyIdAndAcknowledgedFalse(propertyId);

        return new PropertyComparisonDto.PropertySnapshot(
                propertyId,
                property.propertyName(),
                floodResult.riskLevel().name(),
                nearest.zoneName(),
                certCount,
                alertCount
        );
    }
}
