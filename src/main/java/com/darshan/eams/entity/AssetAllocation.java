package com.darshan.eams.entity;

import com.darshan.eams.enums.AllocationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "asset_allocations")
public class AssetAllocation extends BaseEntity {

    @NotNull(message = "Asset is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(name = "fk_allocation_asset"))
    private Asset asset;

    @NotNull(message = "Employee is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, foreignKey = @ForeignKey(name = "fk_allocation_employee"))
    private Employee employee;

    @NotNull(message = "Allocation date is required")
    @Column(name = "allocation_date", nullable = false)
    private LocalDate allocationDate;

    @Column(name = "expected_return_date")
    private LocalDate expectedReturnDate;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @NotNull(message = "Allocation status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AllocationStatus status = AllocationStatus.ACTIVE;

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    @Column(name = "remarks", length = 255)
    private String remarks;
}