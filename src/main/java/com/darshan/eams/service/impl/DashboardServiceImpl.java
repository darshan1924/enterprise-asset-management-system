package com.darshan.eams.service.impl;

import com.darshan.eams.dto.response.AssetResponseDto;
import com.darshan.eams.dto.response.DashboardStatsDto;
import com.darshan.eams.dto.response.RecentActivityDto;
import com.darshan.eams.entity.AuditLog;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.enums.MaintenanceStatus;
import com.darshan.eams.enums.TransferStatus;
import com.darshan.eams.mapper.AssetMapper;
import com.darshan.eams.repository.AssetAllocationRepository;
import com.darshan.eams.repository.AssetRepository;
import com.darshan.eams.repository.AssetTransferRepository;
import com.darshan.eams.repository.AuditLogRepository;
import com.darshan.eams.repository.DepartmentRepository;
import com.darshan.eams.repository.EmployeeRepository;
import com.darshan.eams.repository.MaintenanceRecordRepository;
import com.darshan.eams.repository.VendorRepository;
import com.darshan.eams.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AssetRepository assetRepository;

    private final EmployeeRepository employeeRepository;

    private final DepartmentRepository departmentRepository;

    private final VendorRepository vendorRepository;

    private final MaintenanceRecordRepository maintenanceRecordRepository;

    private final AssetAllocationRepository assetAllocationRepository;

    private final AssetTransferRepository assetTransferRepository;

    private final AuditLogRepository auditLogRepository;

    private final AssetMapper assetMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        long totalAssets = assetRepository.countTotalAssets();
        long availableAssets = assetRepository.countByStatusAndDeletedFalse(AssetStatus.AVAILABLE);
        long allocatedAssets = assetRepository.countByStatusAndDeletedFalse(AssetStatus.ALLOCATED);
        long underMaintenanceAssets = assetRepository.countByStatusAndDeletedFalse(AssetStatus.UNDER_MAINTENANCE);
        long retiredAssets = assetRepository.countByStatusAndDeletedFalse(AssetStatus.RETIRED);

        long totalEmployees = employeeRepository.countActiveEmployees();
        long totalDepartments = departmentRepository.countActiveDepartments();
        long totalVendors = vendorRepository.findByDeletedFalse().size();

        BigDecimal totalAssetValue = assetRepository.sumTotalPurchaseCost();

        long pendingMaintenanceCount = maintenanceRecordRepository.countByStatusAndDeletedFalse(MaintenanceStatus.PENDING)
                + maintenanceRecordRepository.countByStatusAndDeletedFalse(MaintenanceStatus.IN_PROGRESS);

        long overdueAllocationsCount = assetAllocationRepository.findOverdueAllocations(LocalDate.now()).size();

        long pendingTransfersCount = assetTransferRepository.countByStatusAndDeletedFalse(TransferStatus.PENDING);

        BigDecimal averageVendorRating = computeAverageVendorRating();

        Map<String, Long> assetsByCategory = buildCategoryDistribution();
        Map<String, Long> assetsByStatus = buildStatusDistribution();

        List<RecentActivityDto> recentActivities = auditLogRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(this::toRecentActivityDto)
                .collect(Collectors.toList());

        List<AssetResponseDto> upcomingMaintenanceAssets = buildUpcomingMaintenanceAssets();

        return DashboardStatsDto.builder()
                .totalAssets(totalAssets)
                .availableAssets(availableAssets)
                .allocatedAssets(allocatedAssets)
                .underMaintenanceAssets(underMaintenanceAssets)
                .retiredAssets(retiredAssets)
                .totalEmployees(totalEmployees)
                .totalDepartments(totalDepartments)
                .totalVendors(totalVendors)
                .totalAssetValue(totalAssetValue)
                .pendingMaintenanceCount(pendingMaintenanceCount)
                .overdueAllocationsCount(overdueAllocationsCount)
                .pendingTransfersCount(pendingTransfersCount)
                .averageVendorRating(averageVendorRating)
                .assetsByCategory(assetsByCategory)
                .assetsByStatus(assetsByStatus)
                .recentActivities(recentActivities)
                .upcomingMaintenanceAssets(upcomingMaintenanceAssets)
                .build();
    }

    private BigDecimal computeAverageVendorRating() {
        List<BigDecimal> ratings = vendorRepository.findByDeletedFalse().stream()
                .map(v -> v.getRating())
                .filter(r -> r != null)
                .collect(Collectors.toList());

        if (ratings.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = ratings.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(ratings.size()), 2, RoundingMode.HALF_UP);
    }

    private Map<String, Long> buildCategoryDistribution() {
        List<Object[]> rows = assetRepository.countAssetsGroupedByCategory();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    private Map<String, Long> buildStatusDistribution() {
        List<Object[]> rows = assetRepository.countAssetsGroupedByStatus();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Object statusValue = row[0];
            String key = (statusValue instanceof Enum) ? ((Enum<?>) statusValue).name() : String.valueOf(statusValue);
            result.put(key, (Long) row[1]);
        }
        return result;
    }

    private List<AssetResponseDto> buildUpcomingMaintenanceAssets() {
        LocalDate today = LocalDate.now();
        LocalDate in30Days = today.plusDays(30);

        return maintenanceRecordRepository.findUpcomingDueMaintenance(today, in30Days)
                .stream()
                .map(record -> assetMapper.toResponseDto(record.getAsset()))
                .distinct()
                .collect(Collectors.toList());
    }

    private RecentActivityDto toRecentActivityDto(AuditLog auditLog) {
        String performedByName = auditLog.getPerformedBy() != null
                ? auditLog.getPerformedBy().getUsername()
                : "System";

        return RecentActivityDto.builder()
                .message(auditLog.getAction() + " — " + auditLog.getEntityName()
                        + (auditLog.getEntityId() != null ? " #" + auditLog.getEntityId() : ""))
                .performedBy(performedByName)
                .occurredAt(auditLog.getCreatedAt())
                .build();
    }
}