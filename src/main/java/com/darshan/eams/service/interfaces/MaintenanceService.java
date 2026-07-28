package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.request.MaintenanceRequestDto;
import com.darshan.eams.dto.response.MaintenanceResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaintenanceService {

    MaintenanceResponseDto create(MaintenanceRequestDto requestDto);

    MaintenanceResponseDto update(Long id, MaintenanceRequestDto requestDto);

    MaintenanceResponseDto complete(Long id);

    MaintenanceResponseDto cancel(Long id);

    MaintenanceResponseDto getById(Long id);

    Page<MaintenanceResponseDto> getByAsset(Long assetId, Pageable pageable);

    Page<MaintenanceResponseDto> getAll(Pageable pageable);
}