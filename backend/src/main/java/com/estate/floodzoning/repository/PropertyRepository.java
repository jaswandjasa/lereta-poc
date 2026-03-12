package com.estate.floodzoning.repository;

import com.estate.floodzoning.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByPropertyNameContainingIgnoreCase(String name);
}
