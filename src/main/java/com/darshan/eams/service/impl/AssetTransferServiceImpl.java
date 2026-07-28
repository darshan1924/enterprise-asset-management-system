package com.darshan.eams.service.impl;

import com.darshan.eams.dto.request.AssetTransferRequestDto;
import com.darshan.eams.dto.response.AssetTransferResponseDto;
import com.darshan.eams.entity.Asset;
import com.darshan.eams.entity.AssetAllocation;
import com.darshan.eams.entity.AssetTransfer;
import com.darshan.eams.entity.Department;
import com.darshan.eams.entity.Employee;
import com.darshan.eams.enums.AllocationStatus;
import com.darshan.eams.enums.TransferStatus;
import com.darshan.eams.exception.InvalidOperationException;
import com.darshan.eams.exception.ResourceNotFoundException;
import com.darshan.eams.mapper.AssetTransferMapper;
import com.darshan.eams.repository.AssetAllocationRepository;
import com.darshan.eams.repository.AssetRepository;
import com.darshan.eams.repository.AssetTransferRepository;
import com.darshan.eams.repository.DepartmentRepository;
import com.darshan.eams.repository.EmployeeRepository;
import com.darshan.eams.service.interfaces.AssetTransferService;
import com.darshan.eams.service.interfaces.AuditService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetTransferServiceImpl implements AssetTransferService {

    private static final Logger log = LoggerFactory.getLogger(AssetTransferServiceImpl.class);

    private final AssetTransferRepository transferRepository;

    private final AssetRepository assetRepository;

    private final DepartmentRepository departmentRepository;

    private final EmployeeRepository employeeRepository;

    private final AssetAllocationRepository allocationRepository;

    private final AssetTransferMapper transferMapper;

    private final AuditService auditService;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public AssetTransferResponseDto initiate(AssetTransferRequestDto requestDto) {
        Asset asset = assetRepository.findById(requestDto.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", "id", requestDto.getAssetId()));

        AssetTransfer transfer = transferMapper.toEntity(requestDto);
        transfer.setAsset(asset);
        transfer.setStatus(TransferStatus.PENDING);

        if (requestDto.getFromDepartmentId() != null)
            transfer.setFromDepartment(resolveDepartment(requestDto.getFromDepartmentId()));

        if (requestDto.getToDepartmentId() != null)
            transfer.setToDepartment(resolveDepartment(requestDto.getToDepartmentId()));

        if (requestDto.getFromEmployeeId() != null)
            transfer.setFromEmployee(resolveEmployee(requestDto.getFromEmployeeId()));

        if (requestDto.getToEmployeeId() != null)
            transfer.setToEmployee(resolveEmployee(requestDto.getToEmployeeId()));


        AssetTransfer saved = transferRepository.save(transfer);
        auditService.log("INITIATE", "AssetTransfer", saved.getId(), "Initiated transfer for asset: " + asset.getAssetCode());
        log.info("Asset transfer initiated: id={}, assetId={}", saved.getId(), asset.getId());
        return transferMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public AssetTransferResponseDto approve(Long id) {
        AssetTransfer transfer = findTransferOrThrow(id);

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new InvalidOperationException(
                    "Transfer id=" + id + " cannot be approved from status " + transfer.getStatus());
        }

        transfer.setStatus(TransferStatus.APPROVED);
        AssetTransfer updated = transferRepository.save(transfer);
        auditService.log("APPROVE", "AssetTransfer", id, "Approved transfer for asset: " + transfer.getAsset().getAssetCode());
        log.info("Asset transfer approved: id={}", id);
        return transferMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public AssetTransferResponseDto complete(Long id) {
        AssetTransfer transfer = findTransferOrThrow(id);

        if (transfer.getStatus() != TransferStatus.APPROVED) {
            throw new InvalidOperationException(
                    "Transfer id=" + id + " cannot be completed from status " + transfer.getStatus()
                            + " (must be APPROVED first)");
        }

        if (transfer.getToEmployee() != null) {
            allocationRepository.findFirstByAssetIdAndStatusOrderByIdDesc(transfer.getAsset().getId(), AllocationStatus.ACTIVE)
                    .ifPresent(allocation -> syncAllocationEmployee(allocation, transfer.getToEmployee()));
        }

        transfer.setStatus(TransferStatus.COMPLETED);
        AssetTransfer updated = transferRepository.save(transfer);
        auditService.log("COMPLETE", "AssetTransfer", id, "Completed transfer for asset: " + transfer.getAsset().getAssetCode());
        log.info("Asset transfer completed: id={}, assetId={}", id, transfer.getAsset().getId());
        return transferMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public AssetTransferResponseDto reject(Long id) {
        AssetTransfer transfer = findTransferOrThrow(id);

        if (transfer.getStatus() == TransferStatus.COMPLETED)
            throw new InvalidOperationException("Transfer id=" + id + " is already completed and cannot be rejected");

        transfer.setStatus(TransferStatus.REJECTED);
        AssetTransfer updated = transferRepository.save(transfer);
        auditService.log("REJECT", "AssetTransfer", id, "Rejected transfer for asset: " + transfer.getAsset().getAssetCode());
        log.info("Asset transfer rejected: id={}", id);
        return transferMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetTransferResponseDto getById(Long id) {
        return transferMapper.toResponseDto(findTransferOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetTransferResponseDto> getHistoryByAsset(Long assetId, Pageable pageable) {
        return transferRepository.findTransferHistoryByAsset(assetId, pageable)
                .map(transferMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetTransferResponseDto> getAll(Pageable pageable) {
        return transferRepository.findAll(pageable).map(transferMapper::toResponseDto);
    }

    private AssetTransfer findTransferOrThrow(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset Transfer", "id", id));
    }

    private Department resolveDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    private Employee resolveEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    private void syncAllocationEmployee(AssetAllocation allocation, Employee newEmployee) {
        allocation.setEmployee(newEmployee);
        allocationRepository.save(allocation);
    }
}