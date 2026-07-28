package com.darshan.eams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    private long totalAssets;

    private long availableAssets;

    private long allocatedAssets;

    private long underMaintenanceAssets;

    private long retiredAssets;

    private long totalEmployees;

    private long totalDepartments;

    private long totalVendors;

    private BigDecimal totalAssetValue;

    private long pendingMaintenanceCount;

    private long overdueAllocationsCount;

    private long pendingTransfersCount;

    private BigDecimal averageVendorRating;

    private Map<String, Long> assetsByCategory;

    private Map<String, Long> assetsByStatus;

    private List<RecentActivityDto> recentActivities;

    private List<AssetResponseDto> upcomingMaintenanceAssets;
}