package com.darshan.eams.dto.response;

import com.darshan.eams.enums.AssetCondition;
import com.darshan.eams.enums.AssetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponseDto {

    private Long id;

    private String assetCode;

    private String assetName;

    private String description;

    private LocalDate purchaseDate;

    private BigDecimal purchaseCost;

    private LocalDate warrantyExpiryDate;

    private String location;

    private String imageUrl;

    private AssetStatus status;

    private AssetCondition condition;

    private Long categoryId;

    private String categoryName;

    private Long vendorId;

    private String vendorName;

    private String currentHolderName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}