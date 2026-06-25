package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.SystemConfigDto;
import com.example.daugiaonline.application.dto.SystemConfigUpdateRequest;
import com.example.daugiaonline.application.service.SystemConfigService;
import com.example.daugiaonline.entity.SystemConfig;
import com.example.daugiaonline.infrastructure.repository.SystemConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    @PostConstruct
    @Override
    @Transactional
    public void initializeDefaultConfigs() {
        createIfNotFound("DEFAULT_SHIPPING_FEE", "30000", "Phí giao hàng mặc định");
        createIfNotFound("FREE_SHIPPING_THRESHOLD", "5000000", "Đơn tối thiểu để Freeship");
    }

    private void createIfNotFound(String key, String value, String description) {
        if (systemConfigRepository.findByConfigKey(key).isEmpty()) {
            systemConfigRepository.save(SystemConfig.builder()
                    .configKey(key)
                    .configValue(value)
                    .description(description)
                    .build());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemConfigDto> getAllConfigs() {
        return systemConfigRepository.findAll().stream()
                .map(config -> SystemConfigDto.builder()
                        .configKey(config.getConfigKey())
                        .configValue(config.getConfigValue())
                        .description(config.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateConfigs(SystemConfigUpdateRequest request) {
        if (request.getConfigs() != null) {
            for (SystemConfigDto dto : request.getConfigs()) {
                systemConfigRepository.findByConfigKey(dto.getConfigKey()).ifPresent(config -> {
                    config.setConfigValue(dto.getConfigValue());
                    systemConfigRepository.save(config);
                });
            }
        }
    }
}
