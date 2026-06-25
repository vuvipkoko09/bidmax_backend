package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.LocationDto;

import java.util.List;

public interface LocationService {
    LocationDto createLocation(LocationDto request);
    List<LocationDto> getAllLocations();
    void deleteLocation(Long id);
}
