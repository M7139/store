package com.ga.store.controller;

import com.ga.store.dto.AuditLogResponse;
import com.ga.store.model.AuditLog;
import com.ga.store.service.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(
            AuditLogService auditLogService) {

        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {

        List<AuditLogResponse> response =
                auditLogService.getAllAuditLogs()
                        .stream()
                        .map(this::createAuditLogResponse)
                        .toList();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    private AuditLogResponse createAuditLogResponse(
            AuditLog auditLog) {

        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getUserId(),
                auditLog.getAction(),
                auditLog.getDescription(),
                auditLog.getCreatedAt()
        );
    }
}