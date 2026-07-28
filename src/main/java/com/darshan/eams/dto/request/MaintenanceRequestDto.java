package com.darshan.eams.dto.request;

import com.darshan.eams.validation.ValidDateRange;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ValidDateRange(startField = "reportedDate", endField = "expectedCompletionDate",
        message = "Expected completion date must not be before reported date")
public class MaintenanceRequestDto {

    @NotNull(message = "Asset is required")
    private Long assetId;

    private Long vendorId;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Issue description is required")
    @Size(max = 500, message = "Issue description must not exceed 500 characters")
    private String issueDescription;

    @NotNull(message = "Reported date is required")
    private LocalDate reportedDate;

    private LocalDate expectedCompletionDate;

    private LocalDate completedDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Maintenance cost cannot be negative")
    private BigDecimal maintenanceCost;

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    private String remarks;
}