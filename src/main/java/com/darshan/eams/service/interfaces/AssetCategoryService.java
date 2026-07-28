package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.request.AssetCategoryRequestDto;
import com.darshan.eams.dto.response.AssetCategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssetCategoryService {

    AssetCategoryResponseDto create(AssetCategoryRequestDto requestDto);

    AssetCategoryResponseDto update(Long id, AssetCategoryRequestDto requestDto);

    AssetCategoryResponseDto getById(Long id);

    List<AssetCategoryResponseDto> getAllList();

    Page<AssetCategoryResponseDto> getAll(Pageable pageable);

    void delete(Long id);
}