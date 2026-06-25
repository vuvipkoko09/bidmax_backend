package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.LocationDto;
import com.example.daugiaonline.application.service.LocationService;
import com.example.daugiaonline.entity.Location;
import com.example.daugiaonline.exception.ResourceNotFoundException;
import com.example.daugiaonline.infrastructure.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public LocationDto createLocation(LocationDto request) {
        Location location = Location.builder()
                .cityName(request.getCityName())
                .address(request.getAddress())
                .build();
        Location saved = locationRepository.save(location);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }

    private LocationDto mapToDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .cityName(location.getCityName())
                .address(location.getAddress())
                .build();
    }
}
