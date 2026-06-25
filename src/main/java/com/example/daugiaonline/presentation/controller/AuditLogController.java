package com.example.daugiaonline.presentation.controller;

import com.example.daugiaonline.application.dto.AuditLogRequest;
import com.example.daugiaonline.application.dto.AuditLogResponse;
import com.example.daugiaonline.application.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogResponse> getAuditLogById(@PathVariable Long id) {
        return ResponseEntity.ok(auditLogService.getAuditLogById(id));
    }

    @PostMapping
    public ResponseEntity<AuditLogResponse> createAuditLog(@RequestBody AuditLogRequest request) {
        return ResponseEntity.ok(auditLogService.createAuditLog(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuditLogResponse> updateAuditLog(@PathVariable Long id, @RequestBody AuditLogRequest request) {
        return ResponseEntity.ok(auditLogService.updateAuditLog(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditLog(@PathVariable Long id) {
        auditLogService.deleteAuditLog(id);
        return ResponseEntity.noContent().build();
    }
}
