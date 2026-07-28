package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.request.VendorRequestDto;
import com.darshan.eams.dto.response.VendorResponseDto;
import com.darshan.eams.enums.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VendorService {

    VendorResponseDto create(VendorRequestDto requestDto);

    VendorResponseDto update(Long id, VendorRequestDto requestDto);

    VendorResponseDto getById(Long id);

    Page<VendorResponseDto> getAll(Pageable pageable);

    Page<VendorResponseDto> getByStatus(VendorStatus status, Pageable pageable);

    Page<VendorResponseDto> search(String keyword, Pageable pageable);

    void delete(Long id);
}