package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.request.AssetAllocationRequestDto;
import com.darshan.eams.dto.response.AssetAllocationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetAllocationService {

    AssetAllocationResponseDto allocate(AssetAllocationRequestDto requestDto);

    AssetAllocationResponseDto returnAsset(Long allocationId, String remarks);

    AssetAllocationResponseDto getById(Long id);

    Page<AssetAllocationResponseDto> getByEmployee(Long employeeId, Pageable pageable);

    Page<AssetAllocationResponseDto> getByAsset(Long assetId, Pageable pageable);

    Page<AssetAllocationResponseDto> getAll(Pageable pageable);
}