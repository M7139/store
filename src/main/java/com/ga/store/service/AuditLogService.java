package com.ga.store.service;

import com.ga.store.model.AuditLog;
import com.ga.store.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles the creation and retrieval of persistent audit records.
 */
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository =
                auditLogRepository;
    }

    /**
     * Creates a permanent audit record for an important system action.
     *
     * @param userId ID of the user who performed the action
     * @param action action name
     * @param description description of what occurred
     */
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

    /**
     * Returns all audit logs ordered from newest to oldest.
     *
     * @return audit log history
     */
    public List<AuditLog> getAllAuditLogs() {

        return auditLogRepository
                .findAllByOrderByCreatedAtDesc();
    }
}