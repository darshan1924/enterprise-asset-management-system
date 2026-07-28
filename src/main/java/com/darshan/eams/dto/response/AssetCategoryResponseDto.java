package com.darshan.eams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetCategoryResponseDto {

    private Long id;

    private String categoryName;

    private String description;

    private long assetCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}