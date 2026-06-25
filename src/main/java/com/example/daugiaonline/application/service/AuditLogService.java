package com.example.daugiaonline.application.service;

import com.example.daugiaonline.application.dto.AuditLogRequest;
import com.example.daugiaonline.application.dto.AuditLogResponse;

import java.util.List;

public interface AuditLogService {
    List<AuditLogResponse> getRecentLogs();
    List<AuditLogResponse> getAllLogs();
    AuditLogResponse getAuditLogById(Long id);
    AuditLogResponse createAuditLog(AuditLogRequest request);
    AuditLogResponse updateAuditLog(Long id, AuditLogRequest request);
    void deleteAuditLog(Long id);
}
