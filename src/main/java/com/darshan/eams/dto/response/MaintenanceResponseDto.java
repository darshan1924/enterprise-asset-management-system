package com.darshan.eams.dto.response;

import com.darshan.eams.enums.MaintenanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceResponseDto {

    private Long id;

    private Long assetId;

    private String assetCode;

    private String assetName;

    private Long vendorId;

    private String vendorName;

    private String issueDescription;

    private LocalDate reportedDate;

    private LocalDate expectedCompletionDate;

    private LocalDate completedDate;

    private BigDecimal maintenanceCost;

    private MaintenanceStatus status;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
