package com.darshan.eams.dto.response;

import com.darshan.eams.enums.AllocationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetAllocationResponseDto {

    private Long id;

    private Long assetId;

    private String assetCode;

    private String assetName;

    private Long employeeId;

    private String employeeName;

    private LocalDate allocationDate;

    private LocalDate expectedReturnDate;

    private LocalDate actualReturnDate;

    private AllocationStatus status;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}