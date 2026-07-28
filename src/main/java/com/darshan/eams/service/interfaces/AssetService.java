package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.request.AssetRequestDto;
import com.darshan.eams.dto.response.AssetResponseDto;
import com.darshan.eams.enums.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetService {

    AssetResponseDto create(AssetRequestDto requestDto);

    AssetResponseDto update(Long id, AssetRequestDto requestDto);

    AssetResponseDto getById(Long id);

    Page<AssetResponseDto> getAll(Pageable pageable);

    Page<AssetResponseDto> filter(String keyword, Long categoryId, AssetStatus status, Long vendorId, Pageable pageable);

    void delete(Long id);

    void retire(Long id);
}