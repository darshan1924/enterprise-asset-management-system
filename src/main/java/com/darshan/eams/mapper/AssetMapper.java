package com.darshan.eams.mapper;

import com.darshan.eams.dto.request.AssetRequestDto;
import com.darshan.eams.dto.response.AssetResponseDto;
import com.darshan.eams.entity.Asset;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssetMapper {

    public Asset toEntity(AssetRequestDto dto) {
        if (dto == null)
            return null;

        Asset asset = new Asset();
        asset.setAssetCode(dto.getAssetCode());
        asset.setAssetName(dto.getAssetName());
        asset.setDescription(dto.getDescription());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setPurchaseCost(dto.getPurchaseCost());
        asset.setWarrantyExpiryDate(dto.getWarrantyExpiryDate());
        asset.setLocation(dto.getLocation());
        return asset;
    }

    public void updateEntity(Asset asset, AssetRequestDto dto) {
        asset.setAssetCode(dto.getAssetCode());
        asset.setAssetName(dto.getAssetName());
        asset.setDescription(dto.getDescription());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setPurchaseCost(dto.getPurchaseCost());
        asset.setWarrantyExpiryDate(dto.getWarrantyExpiryDate());
        asset.setLocation(dto.getLocation());
    }

    public AssetResponseDto toResponseDto(Asset asset) {
        return toResponseDto(asset, null);
    }

    public AssetResponseDto toResponseDto(Asset asset, String currentHolderName) {
        if (asset == null)
            return null;

        return AssetResponseDto.builder()
                .id(asset.getId())
                .assetCode(asset.getAssetCode())
                .assetName(asset.getAssetName())
                .description(asset.getDescription())
                .purchaseDate(asset.getPurchaseDate())
                .purchaseCost(asset.getPurchaseCost())
                .warrantyExpiryDate(asset.getWarrantyExpiryDate())
                .location(asset.getLocation())
                .imageUrl(asset.getImageUrl())
                .status(asset.getStatus())
                .condition(asset.getCondition())
                .categoryId(asset.getCategory() != null ? asset.getCategory().getId() : null)
                .categoryName(asset.getCategory() != null ? asset.getCategory().getCategoryName() : null)
                .vendorId(asset.getVendor() != null ? asset.getVendor().getId() : null)
                .vendorName(asset.getVendor() != null ? asset.getVendor().getVendorName() : null)
                .currentHolderName(currentHolderName)
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }

    public List<AssetResponseDto> toResponseDtoList(List<Asset> assets) {
        return assets.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}