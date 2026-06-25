package com.example.daugiaonline.application.service.impl;

import com.example.daugiaonline.application.dto.AuditLogRequest;
import com.example.daugiaonline.application.dto.AuditLogResponse;
import com.example.daugiaonline.application.service.AuditLogService;
import com.example.daugiaonline.entity.AuditLog;
import com.example.daugiaonline.infrastructure.repository.AuditLogRepository;
import com.example.daugiaonline.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getRecentLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse getAuditLogById(Long id) {
        return auditLogRepository.findById(id).map(this::mapToResponse).orElseThrow();
    }

    @Override
    @Transactional
    public AuditLogResponse createAuditLog(AuditLogRequest request) {
        AuditLog log = AuditLog.builder()
                .action(request.getAction())
                .details(request.getDetails())
                .user(request.getUserId() != null ? userRepository.findById(request.getUserId()).orElse(null) : null)
                .build();
        return mapToResponse(auditLogRepository.save(log));
    }

    @Override
    @Transactional
    public AuditLogResponse updateAuditLog(Long id, AuditLogRequest request) {
        AuditLog log = auditLogRepository.findById(id).orElseThrow();
        log.setAction(request.getAction());
        log.setDetails(request.getDetails());
        log.setUser(request.getUserId() != null ? userRepository.findById(request.getUserId()).orElse(null) : null);
        return mapToResponse(auditLogRepository.save(log));
    }

    @Override
    @Transactional
    public void deleteAuditLog(Long id) {
        auditLogRepository.deleteById(id);
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .details(log.getDetails())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .createdAt(log.getTimestamp() != null ? log.getTimestamp().toString() : null)
                .build();
    }
}
