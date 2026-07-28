package com.darshan.eams.entity;

import com.darshan.eams.enums.AssetCondition;
import com.darshan.eams.enums.AssetStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
@Table(name = "assets",
        uniqueConstraints = @UniqueConstraint(name = "uk_asset_code", columnNames = "asset_code"))
public class Asset extends BaseEntity {

    @NotBlank(message = "Asset code is required")
    @Size(max = 30, message = "Asset code must not exceed 30 characters")
    @Column(name = "asset_code", nullable = false, length = 30)
    private String assetCode;

    @NotBlank(message = "Asset name is required")
    @Size(max = 100, message = "Asset name must not exceed 100 characters")
    @Column(name = "asset_name", nullable = false, length = 100)
    private String assetName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @NotNull(message = "Purchase cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase cost cannot be negative")
    @Column(name = "purchase_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal purchaseCost;

    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiryDate;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    @Column(name = "location", length = 100)
    private String location;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @NotNull(message = "Asset status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AssetStatus status = AssetStatus.AVAILABLE;

    @NotNull(message = "Asset condition is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_condition", nullable = false, length = 20)
    private AssetCondition condition = AssetCondition.NEW;

    @NotNull(message = "Asset category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_asset_category"))
    private AssetCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", foreignKey = @ForeignKey(name = "fk_asset_vendor"))
    private Vendor vendor;
}