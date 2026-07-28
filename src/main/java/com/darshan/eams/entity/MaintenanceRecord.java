package com.darshan.eams.entity;

import com.darshan.eams.enums.MaintenanceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord extends BaseEntity {

    @NotNull(message = "Asset is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(name = "fk_maintenance_asset"))
    private Asset asset;

    @NotBlank(message = "Title is required")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", foreignKey = @ForeignKey(name = "fk_maintenance_vendor"))
    private Vendor vendor;

    @NotBlank(message = "Issue description is required")
    @Size(max = 500, message = "Issue description must not exceed 500 characters")
    @Column(name = "issue_description", nullable = false, length = 500)
    private String issueDescription;

    @NotNull(message = "Reported date is required")
    @Column(name = "reported_date", nullable = false)
    private LocalDate reportedDate;

    @Column(name = "expected_completion_date")
    private LocalDate expectedCompletionDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Maintenance cost cannot be negative")
    @Column(name = "maintenance_cost", precision = 12, scale = 2)
    private BigDecimal maintenanceCost;

    @NotNull(message = "Maintenance status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MaintenanceStatus status = MaintenanceStatus.PENDING;

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    @Column(name = "remarks", length = 255)
    private String remarks;
}