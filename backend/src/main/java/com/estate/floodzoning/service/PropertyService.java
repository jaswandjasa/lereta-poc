package com.estate.floodzoning.service;

import com.estate.floodzoning.dto.PropertyDto;
import com.estate.floodzoning.exception.ResourceNotFoundException;
import com.estate.floodzoning.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyService {

    private final PropertyRepository repository;

    public List<PropertyDto> getAllProperties() {
        log.debug("Fetching all properties");
        return repository.findAll()
                .stream()
                .map(p -> new PropertyDto(p.getId(), p.getPropertyName(), p.getLatitude(), p.getLongitude()))
                .toList();
    }

    public PropertyDto getPropertyById(Long id) {
        return repository.findById(id)
                .map(p -> new PropertyDto(p.getId(), p.getPropertyName(), p.getLatitude(), p.getLongitude()))
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
    }

    public List<PropertyDto> searchProperties(String name) {
        log.debug("Searching properties by name: {}", name);
        return repository.findByPropertyNameContainingIgnoreCase(name)
                .stream()
                .map(p -> new PropertyDto(p.getId(), p.getPropertyName(), p.getLatitude(), p.getLongitude()))
                .toList();
    }
}
