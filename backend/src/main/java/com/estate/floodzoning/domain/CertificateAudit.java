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
@Table(name = "certificate_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ca_seq")
    @SequenceGenerator(name = "ca_seq", sequenceName = "certificate_audit_seq", allocationSize = 1)
    private Long id;

    @Column(name = "certificate_number", nullable = false, unique = true)
    private String certificateNumber;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Column(name = "property_name_snapshot")
    private String propertyNameSnapshot;

    @Column(name = "latitude_snapshot")
    private Double latitudeSnapshot;

    @Column(name = "longitude_snapshot")
    private Double longitudeSnapshot;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "pdf_hash")
    private String pdfHash;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "generated_by")
    private String generatedBy;
}
