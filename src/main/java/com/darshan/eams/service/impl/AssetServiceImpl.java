package com.darshan.eams.service.impl;

import com.darshan.eams.dto.request.AssetRequestDto;
import com.darshan.eams.dto.response.AssetResponseDto;
import com.darshan.eams.entity.Asset;
import com.darshan.eams.entity.AssetAllocation;
import com.darshan.eams.entity.AssetCategory;
import com.darshan.eams.entity.Vendor;
import com.darshan.eams.enums.AllocationStatus;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.exception.BusinessRuleViolationException;
import com.darshan.eams.exception.DuplicateResourceException;
import com.darshan.eams.exception.ResourceNotFoundException;
import com.darshan.eams.mapper.AssetMapper;
import com.darshan.eams.repository.AssetAllocationRepository;
import com.darshan.eams.repository.AssetCategoryRepository;
import com.darshan.eams.repository.AssetRepository;
import com.darshan.eams.repository.VendorRepository;
import com.darshan.eams.service.interfaces.AssetService;
import com.darshan.eams.service.interfaces.AuditService;
import com.darshan.eams.specification.AssetSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private static final Logger log = LoggerFactory.getLogger(AssetServiceImpl.class);

    private final AssetRepository assetRepository;

    private final AssetCategoryRepository categoryRepository;

    private final VendorRepository vendorRepository;

    private final AssetAllocationRepository assetAllocationRepository;

    private final AssetMapper assetMapper;

    private final AuditService auditService;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public AssetResponseDto create(AssetRequestDto requestDto) {
        if (assetRepository.existsByAssetCodeIgnoreCase(requestDto.getAssetCode()))
            throw new DuplicateResourceException("Asset", "code", requestDto.getAssetCode());

        AssetCategory category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset Category", "id", requestDto.getCategoryId()));

        Asset asset = assetMapper.toEntity(requestDto);
        asset.setCategory(category);

        if (requestDto.getVendorId() != null) {
            Vendor vendor = vendorRepository.findById(requestDto.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", requestDto.getVendorId()));
            asset.setVendor(vendor);
        }
        Asset saved = assetRepository.save(asset);
        auditService.log("CREATE", "Asset", saved.getId(), "Registered asset: " + saved.getAssetCode() + " — " + saved.getAssetName());
        log.info("Asset created: id={}, code={}", saved.getId(), saved.getAssetCode());
        return assetMapper.toResponseDto(saved, null);
    }

    @Override
    @Transactional
    public AssetResponseDto update(Long id, AssetRequestDto requestDto) {
        Asset asset = findActiveAssetOrThrow(id);

        if (!asset.getAssetCode().equalsIgnoreCase(requestDto.getAssetCode())
                && assetRepository.existsByAssetCodeIgnoreCase(requestDto.getAssetCode())) {
            throw new DuplicateResourceException("Asset", "code", requestDto.getAssetCode());
        }

        if (!asset.getCategory().getId().equals(requestDto.getCategoryId())) {
            AssetCategory category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset Category", "id", requestDto.getCategoryId()));
            asset.setCategory(category);
        }

        if (requestDto.getVendorId() != null) {
            if (asset.getVendor() == null || !asset.getVendor().getId().equals(requestDto.getVendorId())) {
                Vendor vendor = vendorRepository.findById(requestDto.getVendorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", requestDto.getVendorId()));
                asset.setVendor(vendor);
            }
        } else {
            asset.setVendor(null);
        }

        assetMapper.updateEntity(asset, requestDto);
        Asset updated = assetRepository.save(asset);
        auditService.log("UPDATE", "Asset", updated.getId(), "Updated asset: " + updated.getAssetCode());
        log.info("Asset updated: id={}", updated.getId());
        return mapWithCurrentHolder(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponseDto getById(Long id) {
        Asset asset = findActiveAssetOrThrow(id);
        return mapWithCurrentHolder(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponseDto> getAll(Pageable pageable) {
        return assetRepository.findByDeletedFalse(pageable).map(this::mapWithCurrentHolder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponseDto> filter(String keyword, Long categoryId, AssetStatus status, Long vendorId, Pageable pageable) {
        return assetRepository
                .findAll(AssetSpecification.withFilters(keyword, categoryId, status, vendorId), pageable)
                .map(this::mapWithCurrentHolder);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public void delete(Long id) {
        Asset asset = findActiveAssetOrThrow(id);

        if (asset.getStatus() == AssetStatus.ALLOCATED) {
            throw new BusinessRuleViolationException(
                    "Cannot delete asset '" + asset.getAssetName() + "' because it is currently allocated");
        }

        asset.setDeleted(true);
        assetRepository.save(asset);
        auditService.log("DELETE", "Asset", id, "Deleted asset: " + asset.getAssetCode());
        log.info("Asset soft-deleted: id={}", id);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public void retire(Long id) {
        Asset asset = findActiveAssetOrThrow(id);

        if (asset.getStatus() == AssetStatus.ALLOCATED) {
            throw new BusinessRuleViolationException(
                    "Cannot retire asset '" + asset.getAssetName() + "' while it is still allocated to an employee");
        }

        asset.setStatus(AssetStatus.RETIRED);
        assetRepository.save(asset);
        auditService.log("RETIRE", "Asset", id, "Retired asset: " + asset.getAssetCode());
        log.info("Asset retired: id={}", id);
    }

    private Asset findActiveAssetOrThrow(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", "id", id));
        if (asset.isDeleted())
            throw new ResourceNotFoundException("Asset", "id", id);
        return asset;
    }

    private AssetResponseDto mapWithCurrentHolder(Asset asset) {
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