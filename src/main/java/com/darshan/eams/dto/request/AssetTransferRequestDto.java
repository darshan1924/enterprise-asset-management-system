package com.darshan.eams.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AssetTransferRequestDto {

    @NotNull(message = "Asset is required")
    private Long assetId;

    private Long fromDepartmentId;

    private Long toDepartmentId;

    private Long fromEmployeeId;

    private Long toEmployeeId;

    @NotNull(message = "Transfer date is required")
    private LocalDate transferDate;

    @Size(max = 255, message = "Reason must not exceed 255 characters")
    private String reason;
}