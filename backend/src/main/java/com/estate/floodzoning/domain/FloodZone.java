package com.estate.floodzoning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "flood_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FloodZone {

    @Id
    private Long id;

    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "risk_level")
    private String riskLevel;

    @Lob
    @Column(name = "geojson")
    private String geojson;
}
