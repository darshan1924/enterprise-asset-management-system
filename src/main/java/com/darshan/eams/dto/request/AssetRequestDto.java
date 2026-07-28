package com.darshan.eams.dto.request;

import com.darshan.eams.validation.UniqueAssetCode;
import com.darshan.eams.validation.group.OnCreate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AssetRequestDto {

    @NotBlank(message = "Asset code is required")
    @Size(max = 30, message = "Asset code must not exceed 30 characters")
    @UniqueAssetCode(groups = OnCreate.class)
    private String assetCode;

    @NotBlank(message = "Asset name is required")
    @Size(max = 100, message = "Asset name must not exceed 100 characters")
    private String assetName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate purchaseDate;

    @NotNull(message = "Purchase cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase cost cannot be negative")
    private BigDecimal purchaseCost;

    private LocalDate warrantyExpiryDate;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private Long vendorId;
}