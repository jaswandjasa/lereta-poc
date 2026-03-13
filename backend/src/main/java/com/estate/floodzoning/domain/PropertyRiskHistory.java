package com.estate.floodzoning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "property_risk_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRiskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rh_seq")
    @SequenceGenerator(name = "rh_seq", sequenceName = "property_risk_history_seq", allocationSize = 1)
    private Long id;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Column(name = "old_risk")
    private String oldRisk;

    @Column(name = "new_risk")
    private String newRisk;

    @Column(name = "old_zone")
    private String oldZone;

    @Column(name = "new_zone")
    private String newZone;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "trigger_source")
    private String triggerSource;
}
