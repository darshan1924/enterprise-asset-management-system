package com.darshan.eams.service.impl;

import com.darshan.eams.dto.request.VendorRequestDto;
import com.darshan.eams.dto.response.VendorResponseDto;
import com.darshan.eams.entity.Vendor;
import com.darshan.eams.enums.VendorStatus;
import com.darshan.eams.exception.BusinessRuleViolationException;
import com.darshan.eams.exception.DuplicateResourceException;
import com.darshan.eams.exception.ResourceNotFoundException;
import com.darshan.eams.mapper.VendorMapper;
import com.darshan.eams.repository.AssetRepository;
import com.darshan.eams.repository.MaintenanceRecordRepository;
import com.darshan.eams.repository.VendorRepository;
import com.darshan.eams.service.interfaces.AuditService;
import com.darshan.eams.service.interfaces.VendorService;
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
public class VendorServiceImpl implements VendorService {

    private static final Logger log = LoggerFactory.getLogger(VendorServiceImpl.class);

    private final VendorRepository vendorRepository;

    private final AssetRepository assetRepository;

    private final MaintenanceRecordRepository maintenanceRecordRepository;

    private final VendorMapper vendorMapper;

    private final AuditService auditService;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public VendorResponseDto create(VendorRequestDto requestDto) {
        if (vendorRepository.existsByVendorNameIgnoreCase(requestDto.getVendorName()))
            throw new DuplicateResourceException("Vendor", "name", requestDto.getVendorName());

        if (vendorRepository.existsByEmailIgnoreCase(requestDto.getEmail()))
            throw new DuplicateResourceException("Vendor", "email", requestDto.getEmail());

        Vendor vendor = vendorMapper.toEntity(requestDto);
        vendor.setStatus(VendorStatus.ACTIVE);
        Vendor saved = vendorRepository.save(vendor);
        auditService.log("CREATE", "Vendor", saved.getId(), "Created vendor: " + saved.getVendorName());
        log.info("Vendor created: id={}, name={}", saved.getId(), saved.getVendorName());
        return vendorMapper.toResponseDto(saved, 0L, 0L);
    }

    @Override
    @Transactional
    public VendorResponseDto update(Long id, VendorRequestDto requestDto) {
        Vendor vendor = findActiveVendorOrThrow(id);

        if (!vendor.getVendorName().equalsIgnoreCase(requestDto.getVendorName())
                && vendorRepository.existsByVendorNameIgnoreCase(requestDto.getVendorName())) {
            throw new DuplicateResourceException("Vendor", "name", requestDto.getVendorName());
        }
        if (!vendor.getEmail().equalsIgnoreCase(requestDto.getEmail())
                && vendorRepository.existsByEmailIgnoreCase(requestDto.getEmail())) {
            throw new DuplicateResourceException("Vendor", "email", requestDto.getEmail());
        }

        vendorMapper.updateEntity(vendor, requestDto);
        Vendor updated = vendorRepository.save(vendor);
        auditService.log("UPDATE", "Vendor", updated.getId(), "Updated vendor: " + updated.getVendorName());
        log.info("Vendor updated: id={}", updated.getId());
        return mapWithCounts(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponseDto getById(Long id) {
        Vendor vendor = findActiveVendorOrThrow(id);
        return mapWithCounts(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VendorResponseDto> getAll(Pageable pageable) {
        return vendorRepository.findByDeletedFalse(pageable).map(this::mapWithCounts);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VendorResponseDto> getByStatus(VendorStatus status, Pageable pageable) {
        return vendorRepository.findByStatusAndDeletedFalse(status, pageable).map(this::mapWithCounts);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VendorResponseDto> search(String keyword, Pageable pageable) {
        return vendorRepository.searchVendors(keyword, pageable).map(this::mapWithCounts);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public void delete(Long id) {
        Vendor vendor = findActiveVendorOrThrow(id);

        long activeAssetCount = assetRepository.findByVendorIdAndDeletedFalse(
                        id, org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        if (activeAssetCount > 0) {
            throw new BusinessRuleViolationException(
                    "Cannot delete vendor '" + vendor.getVendorName()
                            + "' because it still supplies " + activeAssetCount + " active asset(s)");
        }

        vendor.setDeleted(true);
        vendor.setStatus(VendorStatus.INACTIVE);
        vendorRepository.save(vendor);
        auditService.log("DELETE", "Vendor", id, "Deleted vendor: " + vendor.getVendorName());
        log.info("Vendor soft-deleted: id={}", id);
    }

    private Vendor findActiveVendorOrThrow(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
        if (vendor.isDeleted()) 
            throw new ResourceNotFoundException("Vendor", "id", id);
        return vendor;
    }

    private VendorResponseDto mapWithCounts(Vendor vendor) {
        long suppliedAssetsCount = assetRepository.findByVendorIdAndDeletedFalse(
                        vendor.getId(), org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        long maintenanceJobsCount = maintenanceRecordRepository.findByVendorIdAndDeletedFalse(
                        vendor.getId(), org.springframework.data.domain.Pageable.unpaged()).getTotalElements();
        return vendorMapper.toResponseDto(vendor, suppliedAssetsCount, maintenanceJobsCount);
    }
}