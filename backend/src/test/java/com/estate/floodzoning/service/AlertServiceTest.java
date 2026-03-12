package com.estate.floodzoning.service;

import com.estate.floodzoning.domain.FloodAlert;
import com.estate.floodzoning.enums.AlertType;
import com.estate.floodzoning.repository.FloodAlertRepository;
import com.estate.floodzoning.repository.PropertyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private FloodAlertRepository alertRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    @DisplayName("Should create RISK_INCREASE alert when risk goes from LOW to HIGH")
    void shouldCreateRiskIncreaseAlert() {
        when(alertRepository.findTopByPropertyIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        when(alertRepository.save(any(FloodAlert.class))).thenAnswer(i -> i.getArgument(0));

        alertService.createAlert(1L, "LOW", "HIGH", "No Zone", "Nullah Lai Flood Plain");

        ArgumentCaptor<FloodAlert> captor = ArgumentCaptor.forClass(FloodAlert.class);
        verify(alertRepository).save(captor.capture());

        FloodAlert saved = captor.getValue();
        assertThat(saved.getAlertType()).isEqualTo(AlertType.RISK_INCREASE.name());
        assertThat(saved.getOldRisk()).isEqualTo("LOW");
        assertThat(saved.getNewRisk()).isEqualTo("HIGH");
        assertThat(saved.getAcknowledged()).isFalse();
    }

    @Test
    @DisplayName("Should create RISK_DECREASE alert when risk drops")
    void shouldCreateRiskDecreaseAlert() {
        when(alertRepository.findTopByPropertyIdOrderByCreatedAtDesc(2L)).thenReturn(Optional.empty());
        when(alertRepository.save(any(FloodAlert.class))).thenAnswer(i -> i.getArgument(0));

        alertService.createAlert(2L, "HIGH", "LOW", "Nullah Lai", "No Zone");

        ArgumentCaptor<FloodAlert> captor = ArgumentCaptor.forClass(FloodAlert.class);
        verify(alertRepository).save(captor.capture());
        assertThat(captor.getValue().getAlertType()).isEqualTo(AlertType.RISK_DECREASE.name());
    }

    @Test
    @DisplayName("Should create ZONE_CHANGED alert when only zone changes")
    void shouldCreateZoneChangedAlert() {
        when(alertRepository.findTopByPropertyIdOrderByCreatedAtDesc(3L)).thenReturn(Optional.empty());
        when(alertRepository.save(any(FloodAlert.class))).thenAnswer(i -> i.getArgument(0));

        alertService.createAlert(3L, "HIGH", "HIGH", "Zone A", "Zone B");

        ArgumentCaptor<FloodAlert> captor = ArgumentCaptor.forClass(FloodAlert.class);
        verify(alertRepository).save(captor.capture());
        assertThat(captor.getValue().getAlertType()).isEqualTo(AlertType.ZONE_CHANGED.name());
    }

    @Test
    @DisplayName("Should suppress duplicate alert for identical latest transition")
    void shouldSuppressDuplicateAlert() {
        FloodAlert existing = new FloodAlert();
        existing.setPropertyId(1L);
        existing.setOldRisk("LOW");
        existing.setNewRisk("HIGH");
        existing.setOldZone("No Zone");
        existing.setNewZone("Nullah Lai");
        existing.setCreatedAt(LocalDateTime.now());

        when(alertRepository.findTopByPropertyIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(existing));

        alertService.createAlert(1L, "LOW", "HIGH", "No Zone", "Nullah Lai");

        verify(alertRepository, never()).save(any(FloodAlert.class));
    }

    @Test
    @DisplayName("Should acknowledge alert and set timestamp")
    void shouldAcknowledgeAlert() {
        FloodAlert alert = new FloodAlert();
        alert.setId(10L);
        alert.setAcknowledged(false);

        when(alertRepository.findById(10L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(FloodAlert.class))).thenAnswer(i -> i.getArgument(0));

        alertService.acknowledge(10L);

        assertThat(alert.getAcknowledged()).isTrue();
        assertThat(alert.getAcknowledgedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should derive correct alert type from risk ordinals")
    void shouldDeriveAlertTypes() {
        assertThat(alertService.deriveAlertType("LOW", "HIGH", "A", "B"))
                .isEqualTo(AlertType.RISK_INCREASE.name());
        assertThat(alertService.deriveAlertType("HIGH", "MEDIUM", "A", "B"))
                .isEqualTo(AlertType.RISK_DECREASE.name());
        assertThat(alertService.deriveAlertType("HIGH", "HIGH", "A", "B"))
                .isEqualTo(AlertType.ZONE_CHANGED.name());
    }
}
