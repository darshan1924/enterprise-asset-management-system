package com.darshan.eams.mapper;

import com.darshan.eams.dto.request.AssetCategoryRequestDto;
import com.darshan.eams.dto.response.AssetCategoryResponseDto;
import com.darshan.eams.entity.AssetCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssetCategoryMapper {

    public AssetCategory toEntity(AssetCategoryRequestDto dto) {
        if (dto == null)
            return null;

        AssetCategory category = new AssetCategory();
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        return category;
    }

    public void updateEntity(AssetCategory category, AssetCategoryRequestDto dto) {
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
    }

    public AssetCategoryResponseDto toResponseDto(AssetCategory category) {
        return toResponseDto(category, 0L);
    }

    public AssetCategoryResponseDto toResponseDto(AssetCategory category, long assetCount) {
        if (category == null)
            return null;

        return AssetCategoryResponseDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .assetCount(assetCount)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public List<AssetCategoryResponseDto> toResponseDtoList(List<AssetCategory> categories) {
        return categories.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}