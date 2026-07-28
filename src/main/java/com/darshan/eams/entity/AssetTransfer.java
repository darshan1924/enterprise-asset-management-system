package com.darshan.eams.entity;

import com.darshan.eams.enums.TransferStatus;
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
@Table(name = "asset_transfers")
public class AssetTransfer extends BaseEntity {

    @NotNull(message = "Asset is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfer_asset"))
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_department_id", foreignKey = @ForeignKey(name = "fk_transfer_from_department"))
    private Department fromDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_department_id", foreignKey = @ForeignKey(name = "fk_transfer_to_department"))
    private Department toDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_employee_id", foreignKey = @ForeignKey(name = "fk_transfer_from_employee"))
    private Employee fromEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_employee_id", foreignKey = @ForeignKey(name = "fk_transfer_to_employee"))
    private Employee toEmployee;

    @NotNull(message = "Transfer date is required")
    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    @NotNull(message = "Transfer status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransferStatus status = TransferStatus.PENDING;

    @Size(max = 255, message = "Reason must not exceed 255 characters")
    @Column(name = "reason", length = 255)
    private String reason;
}