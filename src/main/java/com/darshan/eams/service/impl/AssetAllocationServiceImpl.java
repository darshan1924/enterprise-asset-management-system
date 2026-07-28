package com.darshan.eams.service.impl;

import com.darshan.eams.dto.request.AssetAllocationRequestDto;
import com.darshan.eams.dto.response.AssetAllocationResponseDto;
import com.darshan.eams.entity.Asset;
import com.darshan.eams.entity.AssetAllocation;
import com.darshan.eams.entity.Employee;
import com.darshan.eams.enums.AllocationStatus;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.enums.EmployeeStatus;
import com.darshan.eams.exception.BusinessRuleViolationException;
import com.darshan.eams.exception.InvalidOperationException;
import com.darshan.eams.exception.ResourceNotFoundException;
import com.darshan.eams.mapper.AssetAllocationMapper;
import com.darshan.eams.repository.AssetAllocationRepository;
import com.darshan.eams.repository.AssetRepository;
import com.darshan.eams.repository.EmployeeRepository;
import com.darshan.eams.service.interfaces.AssetAllocationService;
import com.darshan.eams.service.interfaces.AuditService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AssetAllocationServiceImpl implements AssetAllocationService {

    private static final Logger log = LoggerFactory.getLogger(AssetAllocationServiceImpl.class);

    private final AssetAllocationRepository allocationRepository;

    private final AssetRepository assetRepository;

    private final EmployeeRepository employeeRepository;

    private final AssetAllocationMapper allocationMapper;

    private final AuditService auditService;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public AssetAllocationResponseDto allocate(AssetAllocationRequestDto requestDto) {
        Asset asset = assetRepository.findById(requestDto.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", "id", requestDto.getAssetId()));

        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", requestDto.getEmployeeId()));

        if (asset.isDeleted() || asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new BusinessRuleViolationException(
                    "Asset '" + asset.getAssetName() + "' is not available for allocation (current status: "
                            + asset.getStatus() + ")");
        }

        if (employee.isDeleted() || employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new BusinessRuleViolationException(
                    "Employee '" + employee.getFirstName() + " " + employee.getLastName()
                            + "' is not active and cannot receive asset allocations");
        }

        AssetAllocation allocation = allocationMapper.toEntity(requestDto);
        allocation.setAsset(asset);
        allocation.setEmployee(employee);
        allocation.setStatus(AllocationStatus.ACTIVE);

        if (allocation.getAllocationDate() == null) {
            allocation.setAllocationDate(LocalDate.now());
        }

        AssetAllocation saved = allocationRepository.save(allocation);

        asset.setStatus(AssetStatus.ALLOCATED);
        assetRepository.save(asset);
        auditService.log("ALLOCATE", "Asset", asset.getId(),
                "Allocated " + asset.getAssetCode() + " to " + employee.getFirstName() + " " + employee.getLastName());
        log.info("Asset allocated: assetId={}, employeeId={}, allocationId={}",
                asset.getId(), employee.getId(), saved.getId());

        return allocationMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public AssetAllocationResponseDto returnAsset(Long allocationId, String remarks) {
        AssetAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", "id", allocationId));

        if (allocation.getStatus() != AllocationStatus.ACTIVE) {
            throw new InvalidOperationException(
                    "Allocation record id=" + allocationId + " is not currently active and cannot be returned");
        }

        allocation.setStatus(AllocationStatus.RETURNED);
        allocation.setActualReturnDate(LocalDate.now());
        if (remarks != null && !remarks.isBlank()) {
            allocation.setRemarks(remarks);
        }
        AssetAllocation updated = allocationRepository.save(allocation);

        Asset asset = allocation.getAsset();
        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);
        auditService.log("RETURN", "Asset", asset.getId(), "Returned asset: " + asset.getAssetCode());
        log.info("Asset returned: assetId={}, allocationId={}", asset.getId(), allocationId);
        return allocationMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetAllocationResponseDto getById(Long id) {
        AssetAllocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", "id", id));
        return allocationMapper.toResponseDto(allocation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetAllocationResponseDto> getByEmployee(Long employeeId, Pageable pageable) {
        return allocationRepository.findByEmployeeIdAndDeletedFalse(employeeId, pageable)
                .map(allocationMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetAllocationResponseDto> getByAsset(Long assetId, Pageable pageable) {
        return allocationRepository.findByAssetIdAndDeletedFalse(assetId, pageable)
                .map(allocationMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetAllocationResponseDto> getAll(Pageable pageable) {
        return allocationRepository.findAll(pageable).map(allocationMapper::toResponseDto);
    }
}