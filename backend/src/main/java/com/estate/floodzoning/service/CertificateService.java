package com.estate.floodzoning.service;

// FUTURE: This module can be extracted to a standalone Certification Microservice.
// Dependencies: FloodService (→ GIS Core API), PropertyService (→ Property API)
// Extraction order: 1st (least coupled — standalone PDF generation)

import com.estate.floodzoning.domain.CertificateAudit;
import com.estate.floodzoning.dto.CertificateVerificationDto;
import com.estate.floodzoning.dto.FloodCertificateDto;
import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.NearestZoneResponse;
import com.estate.floodzoning.dto.PropertyDto;
import com.estate.floodzoning.enums.VerificationStatus;
import com.estate.floodzoning.repository.CertificateAuditRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {

    private final CertificateAuditRepository auditRepository;
    private final PropertyService propertyService;
    private final FloodService floodService;

    @Transactional
    public byte[] generateCertificate(Long propertyId) {
        log.info("Generating flood certificate for propertyId={}", propertyId);

        PropertyDto property = propertyService.getPropertyById(propertyId);
        FloodResponse floodResult = floodService.checkFloodRisk(property.latitude(), property.longitude());
        NearestZoneResponse nearest = floodService.findNearestFloodZone(property.latitude(), property.longitude());

        Long seq = auditRepository.getNextCertificateSequence();
        String certNumber = String.format("CERT-%s-%06d",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), seq);

        LocalDateTime now = LocalDateTime.now();

        FloodCertificateDto certDto = new FloodCertificateDto(
                certNumber,
                property.propertyName(),
                property.latitude(),
                property.longitude(),
                floodResult.riskLevel().name(),
                floodResult.zoneName(),
                nearest.zoneName(),
                nearest.nearestDistanceMeters(),
                null,
                now
        );

        String qrReference = "http://localhost:8080/api/certificate/verify/" + certNumber;

        byte[] pdfBytes = buildPdf(certDto, qrReference);

        String pdfHash = computeSha256(pdfBytes);

        CertificateAudit audit = new CertificateAudit();
        audit.setCertificateNumber(certNumber);
        audit.setPropertyId(propertyId);
        audit.setPropertyNameSnapshot(property.propertyName());
        audit.setLatitudeSnapshot(property.latitude());
        audit.setLongitudeSnapshot(property.longitude());
        audit.setRiskLevel(floodResult.riskLevel().name());
        audit.setZoneName(floodResult.zoneName());
        audit.setPdfHash(pdfHash);
        audit.setGeneratedAt(now);
        audit.setGeneratedBy("system");
        audit.setQrReference(qrReference);
        auditRepository.save(audit);

        log.info("Certificate generated: certNumber={}, propertyId={}, hash={}", certNumber, propertyId, pdfHash);
        return pdfBytes;
    }

    private byte[] buildPdf(FloodCertificateDto cert, String qrReference) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
        Font certIdFont = FontFactory.getFont(FontFactory.COURIER, 10, Color.GRAY);

        Paragraph title = new Paragraph("FLOOD ZONE DETERMINATION CERTIFICATE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(8);
        document.add(title);

        Paragraph certLine = new Paragraph("Certificate: " + cert.certificateNumber(), certIdFont);
        certLine.setAlignment(Element.ALIGN_CENTER);
        certLine.setSpacingAfter(20);
        document.add(certLine);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90);
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);

        addRow(table, "Property Name", cert.propertyName(), headerFont, normalFont);
        addRow(table, "Latitude", String.format("%.6f", cert.latitude()), headerFont, normalFont);
        addRow(table, "Longitude", String.format("%.6f", cert.longitude()), headerFont, normalFont);
        addRow(table, "Flood Zone", cert.zoneName(), headerFont, normalFont);
        addRow(table, "Risk Level", cert.riskLevel(), headerFont, normalFont);
        addRow(table, "Nearest Zone", cert.nearestZone(), headerFont, normalFont);
        addRow(table, "Generated At",
                cert.generatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                headerFont, normalFont);

        document.add(table);

        // Embed QR code
        try {
            byte[] qrBytes = generateQrImage(qrReference);
            Image qrImage = Image.getInstance(qrBytes);
            qrImage.scaleToFit(100, 100);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            document.add(qrImage);

            Paragraph qrLabel = new Paragraph("Scan to verify this certificate",
                    FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY));
            qrLabel.setAlignment(Element.ALIGN_CENTER);
            qrLabel.setSpacingAfter(10);
            document.add(qrLabel);
        } catch (Exception e) {
            log.error("QR generation failed for cert={}", cert.certificateNumber(), e);
            throw new RuntimeException("Failed to generate QR code for certificate", e);
        }

        Paragraph disclaimer = new Paragraph(
                "This certificate is generated based on current flood zone data and Oracle Spatial analysis. " +
                "Flood zone boundaries may change over time. This document is for informational purposes only.",
                FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY));
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        disclaimer.setSpacingBefore(30);
        document.add(disclaimer);

        document.close();
        return baos.toByteArray();
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorderColor(Color.LIGHT_GRAY);
        labelCell.setPadding(8);
        labelCell.setBackgroundColor(new Color(245, 245, 245));
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorderColor(Color.LIGHT_GRAY);
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }

    public CertificateVerificationDto verifyCertificate(String certificateNumber) {
        log.info("Verifying certificate: {}", certificateNumber);

        var auditOpt = auditRepository.findByCertificateNumber(certificateNumber);
        if (auditOpt.isEmpty()) {
            log.warn("Certificate not found: {}", certificateNumber);
            return new CertificateVerificationDto(
                    certificateNumber, null, null, null, null, null,
                    VerificationStatus.NOT_FOUND);
        }

        CertificateAudit audit = auditOpt.get();
        VerificationStatus status = (audit.getPdfHash() != null && !audit.getPdfHash().isBlank())
                ? VerificationStatus.VALID
                : VerificationStatus.INVALID;

        log.info("Certificate verified: certNumber={}, status={}", certificateNumber, status);
        return new CertificateVerificationDto(
                audit.getCertificateNumber(),
                audit.getPropertyNameSnapshot(),
                audit.getRiskLevel(),
                audit.getZoneName(),
                audit.getGeneratedAt(),
                audit.getPdfHash(),
                status);
    }

    private byte[] generateQrImage(String content) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream qrOut = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(matrix, "PNG", qrOut);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write QR image", e);
        }
        return qrOut.toByteArray();
    }

    private String computeSha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 not available", e);
            return "HASH_UNAVAILABLE";
        }
    }
}
