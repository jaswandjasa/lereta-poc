package com.estate.floodzoning.service;

import com.estate.floodzoning.domain.PropertyRiskHistory;
import com.estate.floodzoning.dto.RiskHistoryDto;
import com.estate.floodzoning.repository.PropertyRiskHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskHistoryServiceTest {

    @Mock
    private PropertyRiskHistoryRepository historyRepository;

    @InjectMocks
    private RiskHistoryService riskHistoryService;

    @Test
    @DisplayName("Should record risk change with all fields")
    void shouldRecordRiskChange() {
        when(historyRepository.findByPropertyIdOrderByChangedAtDesc(1L))
                .thenReturn(Collections.emptyList());
        when(historyRepository.save(any(PropertyRiskHistory.class)))
                .thenAnswer(i -> i.getArgument(0));

        riskHistoryService.recordChange(1L, "LOW", "HIGH", "Zone A", "Zone B", "MONITORING");

        ArgumentCaptor<PropertyRiskHistory> captor = ArgumentCaptor.forClass(PropertyRiskHistory.class);
        verify(historyRepository).save(captor.capture());

        PropertyRiskHistory saved = captor.getValue();
        assertThat(saved.getPropertyId()).isEqualTo(1L);
        assertThat(saved.getOldRisk()).isEqualTo("LOW");
        assertThat(saved.getNewRisk()).isEqualTo("HIGH");
        assertThat(saved.getOldZone()).isEqualTo("Zone A");
        assertThat(saved.getNewZone()).isEqualTo("Zone B");
        assertThat(saved.getTriggerSource()).isEqualTo("MONITORING");
        assertThat(saved.getChangedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should skip duplicate history row within 30s window")
    void shouldSkipDuplicateWithin30Seconds() {
        PropertyRiskHistory recent = new PropertyRiskHistory();
        recent.setPropertyId(1L);
        recent.setOldRisk("LOW");
        recent.setNewRisk("HIGH");
        recent.setOldZone("Zone A");
        recent.setNewZone("Zone B");
        recent.setChangedAt(LocalDateTime.now().minusSeconds(5));

        when(historyRepository.findByPropertyIdOrderByChangedAtDesc(1L))
                .thenReturn(List.of(recent));

        riskHistoryService.recordChange(1L, "LOW", "HIGH", "Zone A", "Zone B", "MONITORING");

        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return history newest first")
    void shouldReturnHistoryNewestFirst() {
        PropertyRiskHistory h1 = new PropertyRiskHistory();
        h1.setId(1L);
        h1.setPropertyId(5L);
        h1.setOldRisk("LOW");
        h1.setNewRisk("MEDIUM");
        h1.setOldZone("A");
        h1.setNewZone("B");
        h1.setChangedAt(LocalDateTime.of(2026, 3, 12, 10, 0));
        h1.setTriggerSource("MONITORING");

        PropertyRiskHistory h2 = new PropertyRiskHistory();
        h2.setId(2L);
        h2.setPropertyId(5L);
        h2.setOldRisk("MEDIUM");
        h2.setNewRisk("HIGH");
        h2.setOldZone("B");
        h2.setNewZone("C");
        h2.setChangedAt(LocalDateTime.of(2026, 3, 13, 10, 0));
        h2.setTriggerSource("MONITORING");

        when(historyRepository.findByPropertyIdOrderByChangedAtDesc(5L))
                .thenReturn(List.of(h2, h1));

        List<RiskHistoryDto> result = riskHistoryService.getHistory(5L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).newRisk()).isEqualTo("HIGH");
        assertThat(result.get(1).newRisk()).isEqualTo("MEDIUM");
    }
}
