package com.gestion.stock.controller;

import com.gestion.stock.entity.Audit;
import com.gestion.stock.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/audits")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

 
    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public ResponseEntity<List<Audit>> getRecentAudits(@RequestParam(defaultValue = "100") int limit) {
        List<Audit> audits = auditService.getRecentAudits(limit);
        return ResponseEntity.ok(audits);
    }

    @GetMapping("/user-id/{userId}")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public ResponseEntity<List<Audit>> getUserHistoryById(@PathVariable Long userId) {
        List<Audit> audits = auditService.getUserHistoryById(userId);
        return ResponseEntity.ok(audits);
    }


    @GetMapping("/user/{username}")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public ResponseEntity<List<Audit>> getUserHistory(@PathVariable String username) {
        List<Audit> audits = auditService.getUserHistory(username);
        return ResponseEntity.ok(audits);
    }
}
