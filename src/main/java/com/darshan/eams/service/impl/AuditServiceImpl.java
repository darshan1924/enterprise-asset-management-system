package com.darshan.eams.service.impl;

import com.darshan.eams.dto.response.AuditLogResponseDto;
import com.darshan.eams.entity.AuditLog;
import com.darshan.eams.entity.User;
import com.darshan.eams.repository.AuditLogRepository;
import com.darshan.eams.repository.UserRepository;
import com.darshan.eams.service.interfaces.AuditService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void log(String action, String entityName, Long entityId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLog.setPerformedBy(resolveCurrentUser());

        auditLogRepository.save(auditLog);
        log.info("Audit logged: action={}, entity={}, entityId={}", action, entityName, entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> getEntityHistory(String entityName, Long entityId, Pageable pageable) {
        return auditLogRepository.findByEntityNameAndEntityId(entityName, entityId, pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponseDto);
    }

    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String username = authentication.getName();
        return userRepository.findByUsernameWithRole(username).orElse(null);
    }

    private AuditLogResponseDto toResponseDto(AuditLog auditLog) {
        return AuditLogResponseDto.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .entityName(auditLog.getEntityName())
                .entityId(auditLog.getEntityId())
                .details(auditLog.getDetails())
                .performedByUsername(auditLog.getPerformedBy() != null ? auditLog.getPerformedBy().getUsername() : "System")
                .ipAddress(auditLog.getIpAddress())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}