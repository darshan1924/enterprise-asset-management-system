package com.darshan.eams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetReportDto {

    private List<AssetResponseDto> assets;

    private Map<String, Long> countByCategory;

    private Map<String, Long> countByStatus;

    private Map<String, Long> countByVendor;

    private long totalCount;
}