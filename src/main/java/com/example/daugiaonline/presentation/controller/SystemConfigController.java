package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.SystemConfigDto;
import com.example.daugiaonline.application.dto.SystemConfigUpdateRequest;
import com.example.daugiaonline.application.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping("/public/configs")
    public ResponseEntity<List<SystemConfigDto>> getAllConfigs() {
        return ResponseEntity.ok(systemConfigService.getAllConfigs());
    }

    @PutMapping("/admin/configs")
    public ResponseEntity<Void> updateConfigs(@RequestBody SystemConfigUpdateRequest request) {
        systemConfigService.updateConfigs(request);
        return ResponseEntity.ok().build();
    }
}
