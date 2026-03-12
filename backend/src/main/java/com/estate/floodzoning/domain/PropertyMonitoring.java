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
@Table(name = "property_monitoring")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMonitoring {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pm_seq")
    @SequenceGenerator(name = "pm_seq", sequenceName = "property_monitoring_seq", allocationSize = 1)
    private Long id;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Column(name = "last_zone")
    private String lastZone;

    @Column(name = "current_zone")
    private String currentZone;

    @Column(name = "last_risk")
    private String lastRisk;

    @Column(name = "current_risk")
    private String currentRisk;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;

    @Column(name = "status_changed")
    private Boolean statusChanged;

    @Column(name = "monitoring_enabled")
    private Boolean monitoringEnabled;
}
