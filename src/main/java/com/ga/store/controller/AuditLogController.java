package com.ga.store.controller;

import com.ga.store.dto.AuditLogResponse;
import com.ga.store.model.AuditLog;
import com.ga.store.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(
        name = "Audit Logs",
        description = "Admin access to persistent system audit records"
)
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(
            AuditLogService auditLogService) {

        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all audit logs",
            description = "Returns system audit records ordered from newest to oldest. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Audit logs returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
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