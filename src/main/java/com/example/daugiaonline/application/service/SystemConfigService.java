package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.SystemConfigDto;
import com.example.daugiaonline.application.dto.SystemConfigUpdateRequest;

import java.util.List;

public interface SystemConfigService {
    List<SystemConfigDto> getAllConfigs();
    void updateConfigs(SystemConfigUpdateRequest request);
    void initializeDefaultConfigs();
}
