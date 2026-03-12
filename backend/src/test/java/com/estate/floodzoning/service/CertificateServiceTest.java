package com.estate.floodzoning.service;

import com.estate.floodzoning.domain.CertificateAudit;
import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.NearestZoneResponse;
import com.estate.floodzoning.dto.PropertyDto;
import com.estate.floodzoning.enums.RiskLevel;
import com.estate.floodzoning.repository.CertificateAuditRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateAuditRepository auditRepository;

    @Mock
    private PropertyService propertyService;

    @Mock
    private FloodService floodService;

    @InjectMocks
    private CertificateService certificateService;

    @Test
    @DisplayName("Should generate non-empty PDF bytes for valid property")
    void shouldGenerateNonEmptyPdf() {
        PropertyDto property = new PropertyDto(1L, "Riverside Heights", 33.60, 73.06);
        FloodResponse floodResult = new FloodResponse("Nullah Lai Flood Plain", RiskLevel.HIGH, true);
        NearestZoneResponse nearest = new NearestZoneResponse("Nullah Lai Flood Plain", RiskLevel.HIGH, true, 0.0);

        when(propertyService.getPropertyById(1L)).thenReturn(property);
        when(floodService.checkFloodRisk(33.60, 73.06)).thenReturn(floodResult);
        when(floodService.findNearestFloodZone(33.60, 73.06)).thenReturn(nearest);
        when(auditRepository.getNextCertificateSequence()).thenReturn(1L);
        when(auditRepository.save(any(CertificateAudit.class))).thenAnswer(i -> i.getArgument(0));

        byte[] pdf = certificateService.generateCertificate(1L);

        assertThat(pdf).isNotEmpty();
        assertThat(pdf.length).isGreaterThan(100);
        // PDF magic bytes
        assertThat(new String(pdf, 0, 5)).isEqualTo("%PDF-");
    }

    @Test
    @DisplayName("Should persist audit record with property snapshot and hash")
    void shouldPersistAuditWithSnapshot() {
        PropertyDto property = new PropertyDto(2L, "Lake View Residency", 33.70, 73.12);
        FloodResponse floodResult = new FloodResponse("Rawal Lake Overflow Zone", RiskLevel.MEDIUM, false);
        NearestZoneResponse nearest = new NearestZoneResponse("Rawal Lake Overflow Zone", RiskLevel.MEDIUM, false, 150.0);

        when(propertyService.getPropertyById(2L)).thenReturn(property);
        when(floodService.checkFloodRisk(33.70, 73.12)).thenReturn(floodResult);
        when(floodService.findNearestFloodZone(33.70, 73.12)).thenReturn(nearest);
        when(auditRepository.getNextCertificateSequence()).thenReturn(42L);
        when(auditRepository.save(any(CertificateAudit.class))).thenAnswer(i -> i.getArgument(0));

        certificateService.generateCertificate(2L);

        ArgumentCaptor<CertificateAudit> captor = ArgumentCaptor.forClass(CertificateAudit.class);
        verify(auditRepository).save(captor.capture());

        CertificateAudit saved = captor.getValue();
        assertThat(saved.getCertificateNumber()).startsWith("CERT-");
        assertThat(saved.getCertificateNumber()).endsWith("-000042");
        assertThat(saved.getPropertyNameSnapshot()).isEqualTo("Lake View Residency");
        assertThat(saved.getLatitudeSnapshot()).isEqualTo(33.70);
        assertThat(saved.getLongitudeSnapshot()).isEqualTo(73.12);
        assertThat(saved.getRiskLevel()).isEqualTo("MEDIUM");
        assertThat(saved.getPdfHash()).isNotBlank();
        assertThat(saved.getPdfHash()).hasSize(64); // SHA-256 hex = 64 chars
    }
}
