package com.darshan.eams.dto.response;

import com.darshan.eams.enums.TransferStatus;
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
public class AssetTransferResponseDto {

    private Long id;

    private Long assetId;

    private String assetCode;

    private String assetName;

    private Long fromDepartmentId;

    private String fromDepartmentName;

    private Long toDepartmentId;

    private String toDepartmentName;

    private Long fromEmployeeId;

    private String fromEmployeeName;

    private Long toEmployeeId;

    private String toEmployeeName;

    private LocalDate transferDate;

    private TransferStatus status;

    private String reason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}