package com.estate.floodzoning.dto;

import com.estate.floodzoning.enums.VerificationStatus;

import java.time.LocalDateTime;

public record CertificateVerificationDto(
        String certificateNumber,
        String propertyName,
        String riskLevel,
        String zoneName,
        LocalDateTime issuedAt,
        String pdfHash,
        VerificationStatus verificationStatus
) {}
