package com.estate.floodzoning.repository;

import com.estate.floodzoning.domain.CertificateAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateAuditRepository extends JpaRepository<CertificateAudit, Long> {

    Optional<CertificateAudit> findByCertificateNumber(String certificateNumber);

    long countByPropertyId(Long propertyId);

    @Query(value = "SELECT certificate_seq.NEXTVAL FROM dual", nativeQuery = true)
    Long getNextCertificateSequence();
}
