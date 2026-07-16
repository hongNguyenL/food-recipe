package com.nguyen.foodrecipe.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AdminAuditService {

    public void log(String adminUsername, Long adminId, String action, String targetType, Long targetId, String details) {
        AuditEvent event = new AuditEvent(adminUsername, adminId, action, targetType, targetId, details, LocalDateTime.now());
        log.info("ADMIN_AUDIT event={}", event);
    }

    public record AuditEvent(
        String adminUsername,
        Long adminId,
        String action,
        String targetType,
        Long targetId,
        String details,
        LocalDateTime timestamp
    ) {}
}
