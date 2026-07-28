package com.darshan.eams.service.impl;

import com.darshan.eams.dto.response.*;
import com.darshan.eams.entity.Asset;
import com.darshan.eams.entity.AssetAllocation;
import com.darshan.eams.entity.Department;
import com.darshan.eams.entity.MaintenanceRecord;
import com.darshan.eams.enums.AllocationStatus;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.enums.MaintenanceStatus;
import com.darshan.eams.mapper.AssetMapper;
import com.darshan.eams.mapper.MaintenanceMapper;
import com.darshan.eams.repository.AssetAllocationRepository;
import com.darshan.eams.repository.AssetRepository;
import com.darshan.eams.repository.DepartmentRepository;
import com.darshan.eams.repository.EmployeeRepository;
import com.darshan.eams.repository.MaintenanceRecordRepository;
import com.darshan.eams.service.interfaces.ReportService;
import com.darshan.eams.specification.AssetSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final DepartmentRepository departmentRepository;

    private final EmployeeRepository employeeRepository;

    private final AssetRepository assetRepository;

    private final AssetAllocationRepository assetAllocationRepository;

    private final MaintenanceRecordRepository maintenanceRecordRepository;

    private final AssetMapper assetMapper;

    private final MaintenanceMapper maintenanceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentReportDto> getDepartmentReport() {
        List<Department> departments = departmentRepository.findByDeletedFalse();

        Map<String, Long> allocatedCountByDeptName = new HashMap<>();
        for (Object[] row : assetRepository.countAllocatedAssetsGroupedByDepartment()) {
            allocatedCountByDeptName.put((String) row[0], (Long) row[1]);
        }

        Map<Long, BigDecimal> allocatedValueByDeptId = new HashMap<>();
        for (Object[] row : assetRepository.sumAllocatedAssetValueGroupedByDepartment()) {
            allocatedValueByDeptId.put((Long) row[0], (BigDecimal) row[1]);
        }

        return departments.stream()
                .map(dept -> DepartmentReportDto.builder()
                        .departmentId(dept.getId())
                        .departmentName(dept.getDepartmentName())
                        .departmentCode(dept.getDepartmentCode())
                        .employeeCount(employeeRepository.countByDepartmentIdAndDeletedFalse(dept.getId()))
                        .allocatedAssetsCount(allocatedCountByDeptName.getOrDefault(dept.getDepartmentName(), 0L))
                        .allocatedAssetsValue(allocatedValueByDeptId.getOrDefault(dept.getId(), BigDecimal.ZERO))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssetReportDto getAssetReport(Long categoryId, AssetStatus status, Long vendorId) {
        List<Asset> assets = assetRepository
                .findAll(AssetSpecification.withFilters(null, categoryId, status, vendorId), Pageable.unpaged())
                .getContent();

        List<AssetResponseDto> assetDtos = assets.stream()
                .map(this::mapAssetWithCurrentHolder)
                .collect(Collectors.toList());

        Map<String, Long> countByCategory = new LinkedHashMap<>();
        Map<String, Long> countByStatus = new LinkedHashMap<>();
        Map<String, Long> countByVendor = new LinkedHashMap<>();

        for (Asset asset : assets) {
            String categoryName = asset.getCategory() != null ? asset.getCategory().getCategoryName() : "Uncategorized";
            countByCategory.merge(categoryName, 1L, Long::sum);

            countByStatus.merge(asset.getStatus().name(), 1L, Long::sum);

            String vendorName = asset.getVendor() != null ? asset.getVendor().getVendorName() : "No Vendor";
            countByVendor.merge(vendorName, 1L, Long::sum);
        }

        return AssetReportDto.builder()
                .assets(assetDtos)
                .countByCategory(countByCategory)
                .countByStatus(countByStatus)
                .countByVendor(countByVendor)
                .totalCount(assets.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceReportDto getMaintenanceReport(LocalDate startDate, LocalDate endDate, MaintenanceStatus status) {
        List<MaintenanceRecord> records = maintenanceRecordRepository.findForReport(startDate, endDate, status);

        BigDecimal totalCost = records.stream()
                .map(r -> r.getMaintenanceCost() != null ? r.getMaintenanceCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageCost = records.isEmpty()
                ? BigDecimal.ZERO
                : totalCost.divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);

        Map<String, Long> countByStatus = new LinkedHashMap<>();
        Map<String, BigDecimal> costByVendor = new LinkedHashMap<>();

        for (MaintenanceRecord record : records) {
            countByStatus.merge(record.getStatus().name(), 1L, Long::sum);

            String vendorName = record.getVendor() != null ? record.getVendor().getVendorName() : "No Vendor";
            BigDecimal cost = record.getMaintenanceCost() != null ? record.getMaintenanceCost() : BigDecimal.ZERO;
            costByVendor.merge(vendorName, cost, BigDecimal::add);
        }

        List<MaintenanceResponseDto> recordDtos = records.stream()
                .map(maintenanceMapper::toResponseDto)
                .collect(Collectors.toList());

        return MaintenanceReportDto.builder()
                .records(recordDtos)
                .totalTicketCount(records.size())
                .totalCost(totalCost)
                .averageCost(averageCost)
                .countByStatus(countByStatus)
                .costByVendor(costByVendor)
                .build();
    }

    private AssetResponseDto mapAssetWithCurrentHolder(Asset asset) {
        String currentHolderName = null;
        Optional<AssetAllocation> activeAllocation =
                assetAllocationRepository.findFirstByAssetIdAndStatusOrderByIdDesc(asset.getId(), AllocationStatus.ACTIVE);
        if (activeAllocation.isPresent()) {
            var employee = activeAllocation.get().getEmployee();
            currentHolderName = employee.getFirstName() + " " + employee.getLastName();
        }
        return assetMapper.toResponseDto(asset, currentHolderName);
    }
}