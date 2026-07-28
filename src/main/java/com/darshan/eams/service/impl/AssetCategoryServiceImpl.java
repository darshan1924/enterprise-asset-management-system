package com.darshan.eams.service.impl;

import com.darshan.eams.dto.request.AssetCategoryRequestDto;
import com.darshan.eams.dto.response.AssetCategoryResponseDto;
import com.darshan.eams.entity.AssetCategory;
import com.darshan.eams.exception.BusinessRuleViolationException;
import com.darshan.eams.exception.DuplicateResourceException;
import com.darshan.eams.exception.ResourceNotFoundException;
import com.darshan.eams.mapper.AssetCategoryMapper;
import com.darshan.eams.repository.AssetCategoryRepository;
import com.darshan.eams.repository.AssetRepository;
import com.darshan.eams.service.interfaces.AssetCategoryService;
import com.darshan.eams.service.interfaces.AuditService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetCategoryServiceImpl implements AssetCategoryService {

    private static final Logger log = LoggerFactory.getLogger(AssetCategoryServiceImpl.class);

    private final AssetCategoryRepository categoryRepository;

    private final AssetRepository assetRepository;

    private final AssetCategoryMapper categoryMapper;

    private final AuditService auditService;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public AssetCategoryResponseDto create(AssetCategoryRequestDto requestDto) {
        if (categoryRepository.existsByCategoryNameIgnoreCase(requestDto.getCategoryName()))
            throw new DuplicateResourceException("Asset Category", "name", requestDto.getCategoryName());

        AssetCategory category = categoryMapper.toEntity(requestDto);
        AssetCategory saved = categoryRepository.save(category);
        auditService.log("CREATE", "AssetCategory", saved.getId(), "Created category: " + saved.getCategoryName());
        log.info("Asset category created: id={}, name={}", saved.getId(), saved.getCategoryName());
        return categoryMapper.toResponseDto(saved, 0L);
    }

    @Override
    @Transactional
    public AssetCategoryResponseDto update(Long id, AssetCategoryRequestDto requestDto) {
        AssetCategory category = findActiveCategoryOrThrow(id);

        if (!category.getCategoryName().equalsIgnoreCase(requestDto.getCategoryName())
                && categoryRepository.existsByCategoryNameIgnoreCase(requestDto.getCategoryName())) {
            throw new DuplicateResourceException("Asset Category", "name", requestDto.getCategoryName());
        }

        categoryMapper.updateEntity(category, requestDto);
        AssetCategory updated = categoryRepository.save(category);
        auditService.log("UPDATE", "AssetCategory", updated.getId(), "Updated category: " + updated.getCategoryName());
        log.info("Asset category updated: id={}", updated.getId());
        return mapWithAssetCount(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetCategoryResponseDto getById(Long id) {
        AssetCategory category = findActiveCategoryOrThrow(id);
        return mapWithAssetCount(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetCategoryResponseDto> getAllList() {
        return categoryRepository.findByDeletedFalse().stream()
                .map(this::mapWithAssetCount)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetCategoryResponseDto> getAll(Pageable pageable) {
        return categoryRepository.findByDeletedFalse(pageable).map(this::mapWithAssetCount);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public void delete(Long id) {
        AssetCategory category = findActiveCategoryOrThrow(id);

        long assetCount = assetRepository.findByCategoryIdAndDeletedFalse(
                        id, org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        if (assetCount > 0) {
            throw new BusinessRuleViolationException(
                    "Cannot delete category '" + category.getCategoryName()
                            + "' because it still has " + assetCount + " asset(s) assigned");
        }

        category.setDeleted(true);
        categoryRepository.save(category);
        auditService.log("DELETE", "AssetCategory", id, "Deleted category: " + category.getCategoryName());
        log.info("Asset category soft-deleted: id={}", id);
    }

    private AssetCategory findActiveCategoryOrThrow(Long id) {
        AssetCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset Category", "id", id));
        if (category.isDeleted())
            throw new ResourceNotFoundException("Asset Category", "id", id);
        return category;
    }

    private AssetCategoryResponseDto mapWithAssetCount(AssetCategory category) {
        long assetCount = assetRepository.findByCategoryIdAndDeletedFalse(
                        category.getId(), org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();
        return categoryMapper.toResponseDto(category, assetCount);
    }
}