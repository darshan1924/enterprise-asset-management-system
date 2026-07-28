package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.response.AuditLogResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditService {

    void log(String action, String entityName, Long entityId, String details);

    Page<AuditLogResponseDto> getEntityHistory(String entityName, Long entityId, Pageable pageable);

    Page<AuditLogResponseDto> getAllLogs(Pageable pageable);
}
