package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.request.AssetTransferRequestDto;
import com.darshan.eams.dto.response.AssetTransferResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetTransferService {

    AssetTransferResponseDto initiate(AssetTransferRequestDto requestDto);

    AssetTransferResponseDto approve(Long id);

    AssetTransferResponseDto complete(Long id);

    AssetTransferResponseDto reject(Long id);

    AssetTransferResponseDto getById(Long id);

    Page<AssetTransferResponseDto> getHistoryByAsset(Long assetId, Pageable pageable);

    Page<AssetTransferResponseDto> getAll(Pageable pageable);
}