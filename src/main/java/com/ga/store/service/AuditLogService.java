package com.ga.store.service;

import com.ga.store.model.AuditLog;
import com.ga.store.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository =
                auditLogRepository;
    }

    public void createAuditLog(
            Long userId,
            String action,
            String description) {

        AuditLog auditLog =
                new AuditLog(
                        userId,
                        action,
                        description
                );

        auditLogRepository.save(
                auditLog
        );
    }

    public List<AuditLog> getAllAuditLogs() {

        return auditLogRepository
                .findAllByOrderByCreatedAtDesc();
    }
}