package com.darshan.eams.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDto {

    private Long id;

    private String action;

    private String entityName;

    private Long entityId;

    private String details;

    private String performedByUsername;

    private String ipAddress;

    private LocalDateTime createdAt;
}
