package com.darshan.eams.dto.request;

import com.darshan.eams.validation.ValidDateRange;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@ValidDateRange(startField = "allocationDate", endField = "expectedReturnDate",
        message = "Expected return date must not be before allocation date")
public class AssetAllocationRequestDto {

    @NotNull(message = "Asset is required")
    private Long assetId;

    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotNull(message = "Allocation date is required")
    private LocalDate allocationDate;

    @FutureOrPresent(message = "Expected return date cannot be in the past")
    private LocalDate expectedReturnDate;

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    private String remarks;
}