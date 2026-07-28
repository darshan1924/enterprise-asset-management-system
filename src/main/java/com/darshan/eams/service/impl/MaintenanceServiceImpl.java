package com.darshan.eams.service.impl;

import com.darshan.eams.dto.request.MaintenanceRequestDto;
import com.darshan.eams.dto.response.MaintenanceResponseDto;
import com.darshan.eams.entity.Asset;
import com.darshan.eams.entity.MaintenanceRecord;
import com.darshan.eams.entity.Vendor;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.enums.MaintenanceStatus;
import com.darshan.eams.exception.BusinessRuleViolationException;
import com.darshan.eams.exception.InvalidOperationException;
import com.darshan.eams.exception.ResourceNotFoundException;
import com.darshan.eams.mapper.MaintenanceMapper;
import com.darshan.eams.repository.AssetRepository;
import com.darshan.eams.repository.MaintenanceRecordRepository;
import com.darshan.eams.repository.VendorRepository;
import com.darshan.eams.service.interfaces.AuditService;
import com.darshan.eams.service.interfaces.MaintenanceService;
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
public class MaintenanceServiceImpl implements MaintenanceService {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceServiceImpl.class);

    private final MaintenanceRecordRepository maintenanceRepository;

    private final AssetRepository assetRepository;

    private final VendorRepository vendorRepository;

    private final MaintenanceMapper maintenanceMapper;

    private final AuditService auditService;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public MaintenanceResponseDto create(MaintenanceRequestDto requestDto) {
        Asset asset = assetRepository.findById(requestDto.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", "id", requestDto.getAssetId()));

        if (asset.isDeleted() || asset.getStatus() == AssetStatus.ALLOCATED) {
            throw new BusinessRuleViolationException(
                    "Cannot raise maintenance for asset '" + asset.getAssetName()
                            + "' while it is currently allocated to an employee");
        }
        if (asset.getStatus() == AssetStatus.UNDER_MAINTENANCE) {
            throw new BusinessRuleViolationException(
                    "Asset '" + asset.getAssetName() + "' already has an active maintenance ticket");
        }

        MaintenanceRecord record = maintenanceMapper.toEntity(requestDto);
        record.setAsset(asset);
        record.setStatus(MaintenanceStatus.PENDING);

        if (requestDto.getVendorId() != null) {
            Vendor vendor = vendorRepository.findById(requestDto.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", requestDto.getVendorId()));
            record.setVendor(vendor);
        }

        MaintenanceRecord saved = maintenanceRepository.save(record);

        asset.setStatus(AssetStatus.UNDER_MAINTENANCE);
        assetRepository.save(asset);
        auditService.log("CREATE", "MaintenanceRecord", saved.getId(), "Raised maintenance ticket for asset: " + asset.getAssetCode());
        log.info("Maintenance ticket created: id={}, assetId={}", saved.getId(), asset.getId());
        return maintenanceMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public MaintenanceResponseDto update(Long id, MaintenanceRequestDto requestDto) {
        MaintenanceRecord record = findRecordOrThrow(id);

        if (record.getStatus() == MaintenanceStatus.COMPLETED || record.getStatus() == MaintenanceStatus.CANCELLED) {
            throw new InvalidOperationException(
                    "Cannot modify maintenance ticket id=" + id + " because it is already " + record.getStatus());
        }

        if (requestDto.getVendorId() != null) {
            if (record.getVendor() == null || !record.getVendor().getId().equals(requestDto.getVendorId())) {
                Vendor vendor = vendorRepository.findById(requestDto.getVendorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", requestDto.getVendorId()));
                record.setVendor(vendor);
            }
        }

        maintenanceMapper.updateEntity(record, requestDto);
        MaintenanceRecord updated = maintenanceRepository.save(record);
        log.info("Maintenance ticket updated: id={}", updated.getId());
        return maintenanceMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public MaintenanceResponseDto complete(Long id) {
        MaintenanceRecord record = findRecordOrThrow(id);

        if (record.getStatus() != MaintenanceStatus.PENDING && record.getStatus() != MaintenanceStatus.IN_PROGRESS) {
            throw new InvalidOperationException(
                    "Maintenance ticket id=" + id + " cannot be completed from status " + record.getStatus());
        }

        record.setStatus(MaintenanceStatus.COMPLETED);
        record.setCompletedDate(LocalDate.now());
        MaintenanceRecord updated = maintenanceRepository.save(record);

        Asset asset = record.getAsset();
        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);
        auditService.log("COMPLETE", "MaintenanceRecord", id, "Completed maintenance for asset: " + asset.getAssetCode());
        log.info("Maintenance ticket completed: id={}, assetId={}", id, asset.getId());
        return maintenanceMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public MaintenanceResponseDto cancel(Long id) {
        MaintenanceRecord record = findRecordOrThrow(id);

        if (record.getStatus() == MaintenanceStatus.COMPLETED || record.getStatus() == MaintenanceStatus.CANCELLED) {
            throw new InvalidOperationException(
                    "Maintenance ticket id=" + id + " cannot be cancelled from status " + record.getStatus());
        }

        record.setStatus(MaintenanceStatus.CANCELLED);
        MaintenanceRecord updated = maintenanceRepository.save(record);

        Asset asset = record.getAsset();
        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);
        auditService.log("CANCEL", "MaintenanceRecord", id, "Cancelled maintenance for asset: " + asset.getAssetCode());
        log.info("Maintenance ticket cancelled: id={}, assetId={}", id, asset.getId());
        return maintenanceMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceResponseDto getById(Long id) {
        return maintenanceMapper.toResponseDto(findRecordOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceResponseDto> getByAsset(Long assetId, Pageable pageable) {
        return maintenanceRepository.findByAssetIdAndDeletedFalse(assetId, pageable)
                .map(maintenanceMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceResponseDto> getAll(Pageable pageable) {
        return maintenanceRepository.findAll(pageable).map(maintenanceMapper::toResponseDto);
    }

    private MaintenanceRecord findRecordOrThrow(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance Record", "id", id));
    }
}