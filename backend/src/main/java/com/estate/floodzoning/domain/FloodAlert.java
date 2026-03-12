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
@Table(name = "flood_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FloodAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fa_seq")
    @SequenceGenerator(name = "fa_seq", sequenceName = "flood_alerts_seq", allocationSize = 1)
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

    @Column(name = "alert_type", nullable = false)
    private String alertType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "acknowledged")
    private Boolean acknowledged;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;
}
